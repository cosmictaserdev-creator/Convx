package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCloudBoltCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFCloudBoltCircleFill: ImageVector
    get() {
        if (_sFCloudBoltCircleFill != null) {
            return _sFCloudBoltCircleFill!!
        }
        _sFCloudBoltCircleFill = sfIcon(
            name = "Dualtone.SFCloudBoltCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M8.50586 14.0137C6.66992 14.0137 5.61523 12.8223 5.61523 11.3867C5.61523 10.0684 6.49414 9.11133 7.62695 8.78906C7.8418 6.46484 9.75586 4.60938 12.0801 4.60938C13.916 4.60938 15.332 5.63477 16.1133 7.17773C18.2031 7.17773 19.8242 8.61328 19.8242 10.5762C19.8242 12.4707 18.2812 14.0137 16.2793 14.0137ZM10.7227 17.6855L11.7773 15.2539C11.8555 15.0781 11.9531 15 12.1875 15L13.6523 15C13.8477 15 13.916 15.1562 13.8281 15.3418L13.0273 16.875L14.4922 16.875C14.7559 16.875 14.8242 17.0898 14.668 17.3047L11.4746 20.7617C11.3965 20.8789 11.2891 20.8887 11.2207 20.8789C11.123 20.8203 11.0938 20.7129 11.1426 20.5859L12.1875 18.0469L10.957 18.0469C10.7129 18.0469 10.625 17.8809 10.7227 17.6855Z", fillAlpha = 0.85f)
        }
        return _sFCloudBoltCircleFill!!
    }

private var _sFCloudBoltCircleFill: ImageVector? = null
