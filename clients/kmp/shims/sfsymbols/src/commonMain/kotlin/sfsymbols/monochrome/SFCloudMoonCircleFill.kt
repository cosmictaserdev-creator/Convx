package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCloudMoonCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFCloudMoonCircleFill: ImageVector
    get() {
        if (_sFCloudMoonCircleFill != null) {
            return _sFCloudMoonCircleFill!!
        }
        _sFCloudMoonCircleFill = sfIcon(
            name = "Monochrome.SFCloudMoonCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM5.9082 13.125C4.85352 13.4668 4.0918 14.375 4.0918 15.5566C4.0918 16.9727 5.16602 18.0371 6.79688 18.0371L13.7402 18.0371C15.5859 18.0371 17.0117 16.6406 17.0117 14.834C17.0117 13.0664 15.5762 11.709 13.6719 11.6699C12.9199 10.2734 11.6211 9.38477 9.99023 9.38477C7.89062 9.38477 6.13281 11.0352 5.9082 13.125ZM16.875 6.80664C15.4688 6.91406 13.9844 8.50586 13.8086 10.3711C13.9453 10.5176 14.0625 10.6738 14.1699 10.8496C16.1816 11.0938 17.6953 12.5586 17.8613 14.502C20.2051 14.2285 21.4746 12.4609 21.4746 11.416C21.4746 11.1914 21.3867 11.0254 21.1914 11.0254C20.9082 11.0254 20.3711 11.4062 19.5215 11.4062C17.8809 11.4062 16.8262 10.4199 16.8262 8.89648C16.8262 8.04688 17.2754 7.40234 17.2754 7.08984C17.2754 6.91406 17.1191 6.79688 16.875 6.80664Z", fillAlpha = 0.85f)
        }
        return _sFCloudMoonCircleFill!!
    }

private var _sFCloudMoonCircleFill: ImageVector? = null
