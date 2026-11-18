package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFVSquareFill: ImageVector
    get() {
        if (_sFVSquareFill != null) {
            return _sFVSquareFill!!
        }
        _sFVSquareFill = sfIcon(
            name = "Dualtone.SFVSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.4941 17.4609C10.8105 17.4609 10.4395 17.168 10.1562 16.4258L6.44531 6.73828C6.37695 6.55273 6.35742 6.41602 6.35742 6.25C6.35742 5.76172 6.72852 5.41016 7.25586 5.41016C7.74414 5.41016 8.00781 5.63477 8.20312 6.18164L11.4453 15.332L11.5234 15.332L14.7656 6.18164C14.9609 5.64453 15.2246 5.41016 15.7129 5.41016C16.2402 5.41016 16.6113 5.75195 16.6113 6.25C16.6113 6.41602 16.5918 6.55273 16.5234 6.73828L12.8223 16.4062C12.5586 17.1289 12.1484 17.4609 11.4941 17.4609Z", fillAlpha = 0.85f)
        }
        return _sFVSquareFill!!
    }

private var _sFVSquareFill: ImageVector? = null
