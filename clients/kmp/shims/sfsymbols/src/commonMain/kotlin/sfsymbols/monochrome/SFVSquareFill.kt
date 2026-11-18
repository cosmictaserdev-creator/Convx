package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFVSquareFill: ImageVector
    get() {
        if (_sFVSquareFill != null) {
            return _sFVSquareFill!!
        }
        _sFVSquareFill = sfIcon(
            name = "Monochrome.SFVSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM14.7656 6.18164L11.5234 15.332L11.4453 15.332L8.20312 6.18164C8.00781 5.63477 7.74414 5.41016 7.25586 5.41016C6.72852 5.41016 6.35742 5.76172 6.35742 6.25C6.35742 6.41602 6.37695 6.55273 6.44531 6.73828L10.1562 16.4258C10.4395 17.168 10.8105 17.4609 11.4941 17.4609C12.1484 17.4609 12.5586 17.1289 12.8223 16.4062L16.5234 6.73828C16.5918 6.55273 16.6113 6.41602 16.6113 6.25C16.6113 5.75195 16.2402 5.41016 15.7129 5.41016C15.2246 5.41016 14.9609 5.64453 14.7656 6.18164Z", fillAlpha = 0.85f)
        }
        return _sFVSquareFill!!
    }

private var _sFVSquareFill: ImageVector? = null
