package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIphoneGen1Landscape (monochrome)
 * Viewport: 26.582 x 15.6738
 */
public val SfSymbols.Monochrome.SFIphoneGen1Landscape: ImageVector
    get() {
        if (_sFIphoneGen1Landscape != null) {
            return _sFIphoneGen1Landscape!!
        }
        _sFIphoneGen1Landscape = sfIcon(
            name = "Monochrome.SFIphoneGen1Landscape",
            viewportWidth = 26.582f,
            viewportHeight = 15.6738f
        ) {
            addSfPath("M23.0078 15.6641C24.9707 15.6641 26.2207 14.3652 26.2207 12.3242L26.2207 3.33008C26.2207 1.28906 24.9707 0 23.0078 0L3.22266 0C1.25 0 0 1.28906 0 3.33008L0 12.3242C0 14.3652 1.25 15.6641 3.22266 15.6641ZM22.1289 13.9258L4.10156 13.9258L4.10156 1.72852L22.1289 1.72852ZM25.0195 7.8125C25.0195 8.39844 24.5508 8.86719 23.9746 8.86719C23.3984 8.86719 22.9395 8.39844 22.9395 7.8125C22.9395 7.24609 23.3984 6.77734 23.9746 6.77734C24.5508 6.77734 25.0195 7.24609 25.0195 7.8125ZM2.11914 10.0684C1.8457 10.0684 1.67969 9.88281 1.67969 9.62891L1.67969 6.02539C1.67969 5.76172 1.8457 5.58594 2.11914 5.58594C2.38281 5.58594 2.55859 5.76172 2.55859 6.02539L2.55859 9.62891C2.55859 9.88281 2.38281 10.0684 2.11914 10.0684Z", fillAlpha = 0.85f)
        }
        return _sFIphoneGen1Landscape!!
    }

private var _sFIphoneGen1Landscape: ImageVector? = null
