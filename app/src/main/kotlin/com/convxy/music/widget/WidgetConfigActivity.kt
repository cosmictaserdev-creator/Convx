/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.convxy.music.R
import com.convxy.music.ui.component.PreferenceEntry
import com.convxy.music.ui.component.PreferenceGroupTitle
import com.convxy.music.ui.component.SwitchPreference
import com.convxy.music.ui.theme.vivimusicTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Per-widget configuration, launched by the launcher via APPWIDGET_CONFIGURE when
 * a widget is dropped, and re-openable later from widget settings.
 *
 * Result handling matters here: the launcher only keeps the widget if we return
 * RESULT_OK with the widget id, and we must return RESULT_CANCELED on back so a
 * half-configured widget isn't left on the home screen.
 */
@AndroidEntryPoint
class WidgetConfigActivity : ComponentActivity() {

    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        widgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // Cancelled unless the user explicitly confirms, per the configure contract.
        setResult(Activity.RESULT_CANCELED, resultIntent())

        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            vivimusicTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    WidgetConfigScreen(
                        initial = WidgetConfig.load(this, widgetId),
                        onConfirm = ::confirm,
                    )
                }
            }
        }
    }

    private fun resultIntent() =
        Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)

    private fun confirm(config: WidgetConfig) {
        WidgetConfig.save(this, widgetId, config)
        // Ask the service to repaint immediately so the widget doesn't sit on its
        // placeholder layout until the next playback event.
        NowPlayingWidgetReceiver.requestUpdate(this)
        setResult(Activity.RESULT_OK, resultIntent())
        finish()
    }
}

@Composable
private fun WidgetConfigScreen(
    initial: WidgetConfig,
    onConfirm: (WidgetConfig) -> Unit,
) {
    val context = LocalContext.current
    var config by remember { mutableStateOf(initial) }

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        if (uri != null) {
            // Widgets render from a different process later on, so the read grant
            // has to be persisted or the image goes blank after a reboot.
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
            config = config.copy(
                imageUri = uri.toString(),
                background = WidgetBackground.IMAGE,
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .verticalScroll(rememberScrollState()),
    ) {
        PreferenceGroupTitle(title = stringResource(R.string.widget_cfg_background))

        WidgetBackground.entries.forEach { option ->
            PreferenceEntry(
                title = { Text(stringResource(option.labelRes())) },
                trailingContent = {
                    RadioButton(
                        selected = config.background == option,
                        onClick = { config = config.copy(background = option) },
                    )
                },
                onClick = {
                    if (option == WidgetBackground.IMAGE) {
                        pickImage.launch(arrayOf("image/*"))
                    } else {
                        config = config.copy(background = option)
                    }
                },
            )
        }

        if (config.background == WidgetBackground.IMAGE) {
            PreferenceEntry(
                title = { Text(stringResource(R.string.widget_cfg_choose_image)) },
                description = config.imageUri ?: stringResource(R.string.widget_cfg_no_image),
                onClick = { pickImage.launch(arrayOf("image/*")) },
            )
        }

        PreferenceGroupTitle(title = stringResource(R.string.widget_cfg_content))

        SwitchPreference(
            title = { Text(stringResource(R.string.widget_cfg_show_artwork)) },
            checked = config.showArtwork,
            onCheckedChange = { config = config.copy(showArtwork = it) },
        )
        SwitchPreference(
            title = { Text(stringResource(R.string.widget_cfg_show_prev_next)) },
            checked = config.showPrevNext,
            onCheckedChange = { config = config.copy(showPrevNext = it) },
        )
        SwitchPreference(
            title = { Text(stringResource(R.string.widget_cfg_show_like)) },
            checked = config.showLike,
            onCheckedChange = { config = config.copy(showLike = it) },
        )

        Button(
            onClick = { onConfirm(config) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(stringResource(R.string.widget_cfg_done))
        }
    }
}

private fun WidgetBackground.labelRes(): Int = when (this) {
    WidgetBackground.ALBUM_TINT -> R.string.widget_cfg_bg_album
    WidgetBackground.DARK -> R.string.widget_cfg_bg_dark
    WidgetBackground.LIGHT -> R.string.widget_cfg_bg_light
    WidgetBackground.IMAGE -> R.string.widget_cfg_bg_image
}
