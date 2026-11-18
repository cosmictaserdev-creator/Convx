package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMusicMicrophoneCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFMusicMicrophoneCircle: ImageVector
    get() {
        if (_sFMusicMicrophoneCircle != null) {
            return _sFMusicMicrophoneCircle!!
        }
        _sFMusicMicrophoneCircle = sfIcon(
            name = "Monochrome.SFMusicMicrophoneCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.9395 9.62891L6.78711 16.2598C6.49414 16.5723 6.49414 16.9531 6.77734 17.2852L5.94727 18.3203C5.83008 18.4668 5.83984 18.6719 5.98633 18.7988L6.18164 18.9941C6.31836 19.1211 6.50391 19.1406 6.66016 19.0234L7.70508 18.1934C8.01758 18.4668 8.4082 18.4766 8.71094 18.1836L12.0312 15.1172L12.0312 18.7598C12.0312 19.1309 12.3242 19.4336 12.7148 19.4336C13.0859 19.4336 13.3887 19.1309 13.3887 18.7598L13.3887 13.8379L15.3125 12.0703C16.1328 12.1777 16.9629 11.8164 17.7051 11.0742L13.8965 7.26562C13.1543 8.01758 12.8223 8.80859 12.9395 9.62891ZM7.59766 16.8359L13.3594 10.7129C13.457 10.8984 13.584 11.0547 13.7402 11.2305C13.8867 11.3965 14.0527 11.5332 14.2188 11.6406L8.14453 17.3828ZM18.4082 6.55273C17.2461 5.39062 15.7227 5.44922 14.4922 6.67969L18.3008 10.4785C19.5312 9.24805 19.5801 7.71484 18.4082 6.55273Z", fillAlpha = 0.85f)
        }
        return _sFMusicMicrophoneCircle!!
    }

private var _sFMusicMicrophoneCircle: ImageVector? = null
