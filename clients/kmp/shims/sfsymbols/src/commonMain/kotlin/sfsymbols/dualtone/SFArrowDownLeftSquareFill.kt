package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownLeftSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFArrowDownLeftSquareFill: ImageVector
    get() {
        if (_sFArrowDownLeftSquareFill != null) {
            return _sFArrowDownLeftSquareFill!!
        }
        _sFArrowDownLeftSquareFill = sfIcon(
            name = "Dualtone.SFArrowDownLeftSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.75 15.2637L10.918 13.2227L16.1719 7.97852C16.3379 7.8125 16.4453 7.59766 16.4453 7.37305C16.4453 6.86523 16.1133 6.54297 15.625 6.54297C15.3516 6.54297 15.1367 6.64062 14.9805 6.79688L9.73633 12.0508L7.70508 14.209C6.88477 15.0781 7.91016 16.0449 8.75 15.2637ZM8.16406 11.7969L8.16406 9.19922C8.16406 8.66211 7.8418 8.33008 7.34375 8.33008C6.85547 8.33008 6.5332 8.69141 6.5332 9.21875L6.5332 15.5176C6.5332 16.1621 6.88477 16.4355 7.45117 16.4355L13.7793 16.4355C14.3066 16.4355 14.6387 16.1133 14.6387 15.625C14.6387 15.127 14.2969 14.8047 13.7598 14.8047L10.957 14.8047L7.88086 15.1074Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownLeftSquareFill!!
    }

private var _sFArrowDownLeftSquareFill: ImageVector? = null
