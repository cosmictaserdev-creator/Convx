package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowTurnRightDown (monochrome)
 * Viewport: 19.707 x 23.6816
 */
public val SfSymbols.Monochrome.SFArrowTurnRightDown: ImageVector
    get() {
        if (_sFArrowTurnRightDown != null) {
            return _sFArrowTurnRightDown!!
        }
        _sFArrowTurnRightDown = sfIcon(
            name = "Monochrome.SFArrowTurnRightDown",
            viewportWidth = 19.707f,
            viewportHeight = 23.6816f
        ) {
            addSfPath("M9.90234 21.709L6.48438 17.998L2.03125 13.5449C1.86523 13.3789 1.61133 13.291 1.35742 13.291C0.830078 13.291 0.439453 13.6914 0.439453 14.2285C0.439453 14.4824 0.537109 14.6973 0.732422 14.9121L9.18945 23.3594C9.38477 23.5742 9.62891 23.6816 9.89258 23.6816C10.1562 23.6816 10.4004 23.5742 10.6055 23.3594L19.0527 14.9121C19.2578 14.6973 19.3457 14.4824 19.3457 14.2285C19.3457 13.6914 18.9551 13.291 18.4277 13.291C18.1738 13.291 17.9199 13.3789 17.7539 13.5449L13.3008 17.998ZM2.5 0C1.77734 0 1.15234 0.00976562 0.810547 0.078125C0.380859 0.175781 0 0.439453 0 0.986328C0 1.54297 0.400391 1.9043 0.947266 1.9043C1.28906 1.89453 1.78711 1.875 2.40234 1.86523C6.98242 1.86523 8.96484 3.55469 8.95508 8.16406L8.94531 17.4512L9.04297 21.709C9.05273 22.1777 9.43359 22.5586 9.90234 22.5586C10.3613 22.5586 10.7422 22.1777 10.752 21.709L10.8398 17.4512L10.8398 8.07617C10.8398 2.35352 8.22266 0 2.5 0Z", fillAlpha = 0.85f)
        }
        return _sFArrowTurnRightDown!!
    }

private var _sFArrowTurnRightDown: ImageVector? = null
