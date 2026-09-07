/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

/**
 * Hands a local file to whatever tag editor the user has installed.
 *
 * Convx does not write tags itself and this is not a step towards doing so: editing
 * ID3/Vorbis frames correctly across every container is a whole application's worth of
 * work, and several good ones already exist. This just opens the file in one.
 *
 * `ACTION_EDIT` with the file's audio mime type is what the established editors register
 * for. The read/write grants are the ones that make the target able to open it at all —
 * a content URI is useless to another process without them.
 */
object ExternalTagEditor {

    /**
     * @param contentUri a local song's id, which is its MediaStore content URI.
     * @return false when no installed app can handle it, so the caller can say so rather
     *   than appearing to do nothing.
     */
    fun launch(context: Context, contentUri: String, mimeType: String?): Boolean {
        val intent = Intent(Intent.ACTION_EDIT).apply {
            setDataAndType(contentUri.toUri(), mimeType ?: "audio/*")
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
            )
        }
        return try {
            // A chooser rather than the bare intent: ACTION_EDIT on audio is claimed by
            // players as well as editors, and without it the system's sticky default can
            // send the file to whichever one the user last picked for something else.
            context.startActivity(
                Intent.createChooser(intent, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
            true
        } catch (_: ActivityNotFoundException) {
            false
        } catch (_: SecurityException) {
            // Some editors declare the filter but refuse the grant on scoped storage.
            false
        }
    }
}
