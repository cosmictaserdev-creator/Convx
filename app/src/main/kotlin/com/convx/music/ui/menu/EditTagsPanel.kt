/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.ui.menu

import android.app.Activity
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.core.net.toUri
import com.convx.music.LocalDatabase
import com.convx.music.R
import com.convx.music.db.entities.Song
import com.convx.music.ui.component.LocalBottomSheetPageState
import com.convx.music.ui.ios.compat.Button
import com.convx.music.ui.ios.compat.OutlinedTextField
import com.convx.music.ui.ios.compat.Text
import com.convx.music.ui.ios.compat.TextButton
import com.convx.music.utils.LocalTagEditor
import kotlinx.coroutines.launch

/**
 * Edits the tags inside a local song's file and keeps the library in step.
 *
 * Android makes the user approve a change to a file another app indexed, so saving first asks the system for
 * write access (Android 11+), then writes the tags into the file, then updates MediaStore and the library.
 */
@Composable
fun EditTagsPanel(song: Song) {
    val context = LocalContext.current
    val database = LocalDatabase.current.get()
    val page = LocalBottomSheetPageState.current
    val scope = rememberCoroutineScope()
    val uri = remember(song.id) { song.id.toUri() }

    var loaded by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(song.song.title) }
    var artist by remember { mutableStateOf(song.artists.joinToString { it.name }) }
    var album by remember { mutableStateOf(song.album?.title.orEmpty()) }
    var year by remember { mutableStateOf(song.song.year?.toString().orEmpty()) }
    var track by remember { mutableStateOf("") }
    var genre by remember { mutableStateOf("") }

    // What the file itself says wins over what the library guessed from the filename.
    LaunchedEffect(uri) {
        LocalTagEditor.read(context, uri)?.let { tags ->
            if (tags.title.isNotBlank()) title = tags.title
            if (tags.artist.isNotBlank()) artist = tags.artist
            if (tags.album.isNotBlank()) album = tags.album
            if (tags.year.isNotBlank()) year = tags.year
            track = tags.track
            genre = tags.genre
        }
        loaded = true
    }

    fun performSave() {
        saving = true
        scope.launch {
            val tags = LocalTagEditor.Tags(title, artist, album, year, track, genre)
            val written = LocalTagEditor.write(context, uri, tags)
            if (written) LocalTagEditor.applyToLibrary(database, song.id, tags)
            saving = false
            Toast.makeText(
                context,
                context.getString(if (written) R.string.tag_editor_saved else R.string.tag_editor_failed),
                Toast.LENGTH_SHORT,
            ).show()
            if (written) page.dismiss()
        }
    }

    val permission = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            performSave()
        } else {
            Toast.makeText(context, context.getString(R.string.tag_editor_permission), Toast.LENGTH_SHORT).show()
        }
    }

    fun save() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val request = MediaStore.createWriteRequest(context.contentResolver, listOf(uri))
            permission.launch(IntentSenderRequest.Builder(request.intentSender).build())
        } else {
            performSave()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(text = stringResource(R.string.tag_editor_title))
        TagField(title, { title = it }, R.string.tag_title)
        TagField(artist, { artist = it }, R.string.tag_artist)
        TagField(album, { album = it }, R.string.tag_album)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TagField(year, { year = it.filter(Char::isDigit).take(4) }, R.string.tag_year, Modifier.weight(1f), number = true)
            TagField(track, { track = it }, R.string.tag_track, Modifier.weight(1f), number = true)
        }
        TagField(genre, { genre = it }, R.string.tag_genre)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TextButton(onClick = { page.dismiss() }, modifier = Modifier.weight(1f)) {
                Text(stringResource(android.R.string.cancel))
            }
            Button(onClick = ::save, enabled = loaded && !saving && title.isNotBlank(), modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.tag_editor_save))
            }
        }
    }
}

@Composable
private fun TagField(
    value: String,
    onChange: (String) -> Unit,
    label: Int,
    modifier: Modifier = Modifier,
    number: Boolean = false,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(stringResource(label)) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = if (number) KeyboardType.Number else KeyboardType.Text),
    )
}
