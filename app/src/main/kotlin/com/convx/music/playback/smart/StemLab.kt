package com.convx.music.playback.smart

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.media3.common.util.UnstableApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

/**
 * Debug tool: splits one song with every separator available and saves each stem as its own file
 * in `Music/Convx Stem Lab/<song>/`, so the two can be listened to side by side.
 */
@UnstableApi
object StemLab {
    sealed interface State {
        data class Running(val step: String) : State
        data class Done(val folder: String, val report: List<String>) : State
        data class Failed(val reason: String) : State
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var job: Job? = null
    private val _state = MutableStateFlow<State?>(null)
    val state: StateFlow<State?> = _state.asStateFlow()

    fun cancel() {
        job?.cancel()
        _state.value = null
    }

    fun dismiss() {
        if (_state.value !is State.Running) _state.value = null
    }

    fun run(context: Context, mediaId: String, title: String) {
        if (job?.isActive == true) return
        val appContext = context.applicationContext
        job = scope.launch {
            val uri = if (mediaId.startsWith("content://") || mediaId.startsWith("file://")) mediaId.toUri()
            else "https://music.youtube.com/watch?v=$mediaId".toUri()
            val folder = title.replace(Regex("[\\\\/:*?\"<>|]"), "_").trim().take(80).ifBlank { mediaId }
            val work = File(appContext.cacheDir, "stemlab").apply { deleteRecursively(); mkdirs() }
            val report = mutableListOf<String>()
            val qualities = buildList {
                add(StemQuality.FAST)
                if (DemucsModel.isInstalled(appContext)) add(StemQuality.DEMUCS)
            }
            if (qualities.size == 1) report += "Demucs: skipped, the model is not installed"
            for (quality in qualities) {
                val label = if (quality == StemQuality.FAST) "fast" else "demucs"
                _state.value = State.Running("Splitting with $label…")
                val started = System.currentTimeMillis()
                val names = StemSeparator.split(appContext, uri, quality, { isActive }) { File(work, "${label}_$it.m4a") }
                val seconds = (System.currentTimeMillis() - started) / 1000.0
                if (!isActive) return@launch
                if (names == null) {
                    report += "$label: failed after ${"%.1f".format(seconds)} s (song not downloaded or cached?)"
                    continue
                }
                report += "$label: ${names.joinToString()} in ${"%.1f".format(seconds)} s"
            }
            val files = work.listFiles()?.filter { it.extension == "m4a" }.orEmpty()
            if (files.isEmpty()) {
                _state.value = State.Failed(report.joinToString("\n"))
                return@launch
            }
            _state.value = State.Running("Saving ${files.size} files…")
            val where = runCatching { publish(appContext, folder, files) }.getOrElse {
                _state.value = State.Failed("Couldn't save the files: ${it.message}")
                return@launch
            }
            work.deleteRecursively()
            _state.value = State.Done(where, report)
        }
    }

    /** Copies [files] into the shared Music folder; returns where a file manager will find them. */
    private fun publish(context: Context, folder: String, files: List<File>): String {
        val relative = "${Environment.DIRECTORY_MUSIC}/Convx Stem Lab/$folder"
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Without scoped storage the shared folder needs a permission this debug tool does not ask for.
            val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "Convx Stem Lab/$folder").apply { mkdirs() }
            files.forEach { it.copyTo(File(dir, it.name), overwrite = true) }
            return dir.absolutePath
        }
        val resolver = context.contentResolver
        val collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        for (file in files) {
            val values = ContentValues().apply {
                put(MediaStore.Audio.Media.DISPLAY_NAME, file.name)
                put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp4")
                put(MediaStore.Audio.Media.RELATIVE_PATH, relative)
                put(MediaStore.Audio.Media.TITLE, "$folder · ${file.nameWithoutExtension}")
                put(MediaStore.Audio.Media.IS_PENDING, 1)
            }
            val item = resolver.insert(collection, values) ?: error("MediaStore refused ${file.name}")
            resolver.openOutputStream(item)?.use { out -> file.inputStream().use { it.copyTo(out) } }
                ?: error("Couldn't write ${file.name}")
            resolver.update(item, ContentValues().apply { put(MediaStore.Audio.Media.IS_PENDING, 0) }, null, null)
        }
        return relative
    }
}
