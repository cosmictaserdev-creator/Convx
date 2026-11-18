package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCloudBoltCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFCloudBoltCircleFill: ImageVector
    get() {
        if (_sFCloudBoltCircleFill != null) {
            return _sFCloudBoltCircleFill!!
        }
        _sFCloudBoltCircleFill = sfIcon(
            name = "Monochrome.SFCloudBoltCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM12.1875 15C11.9531 15 11.8555 15.0781 11.7773 15.2539L10.7227 17.6855C10.625 17.8809 10.7129 18.0469 10.957 18.0469L12.1875 18.0469L11.1426 20.5859C11.0938 20.7129 11.123 20.8203 11.2207 20.8789C11.2891 20.8887 11.3965 20.8789 11.4746 20.7617L14.668 17.3047C14.8242 17.0898 14.7559 16.875 14.4922 16.875L13.0273 16.875L13.8281 15.3418C13.916 15.1562 13.8477 15 13.6523 15ZM7.62695 8.78906C6.49414 9.11133 5.61523 10.0684 5.61523 11.3867C5.61523 12.8223 6.66992 14.0137 8.50586 14.0137L16.2793 14.0137C18.2812 14.0137 19.8242 12.4707 19.8242 10.5762C19.8242 8.61328 18.2031 7.17773 16.1133 7.17773C15.332 5.63477 13.916 4.60938 12.0801 4.60938C9.75586 4.60938 7.8418 6.46484 7.62695 8.78906Z", fillAlpha = 0.85f)
        }
        return _sFCloudBoltCircleFill!!
    }

private var _sFCloudBoltCircleFill: ImageVector? = null
