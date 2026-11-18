package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFYieldsignFill (monochrome)
 * Viewport: 26.6504 x 24.0723
 */
public val SfSymbols.Monochrome.SFYieldsignFill: ImageVector
    get() {
        if (_sFYieldsignFill != null) {
            return _sFYieldsignFill!!
        }
        _sFYieldsignFill = sfIcon(
            name = "Monochrome.SFYieldsignFill",
            viewportWidth = 26.6504f,
            viewportHeight = 24.0723f
        ) {
            addSfPath("M26.2891 3.24219C26.2891 3.81836 26.123 4.42383 25.8008 4.99023L15.9277 22.2559C15.3125 23.3301 14.2285 23.8672 13.1445 23.8672C12.0508 23.8672 10.9766 23.3301 10.3613 22.2559L0.488281 4.99023C0.15625 4.41406 0 3.81836 0 3.24219C0 1.42578 1.23047 0 3.26172 0L23.0176 0C25.0586 0 26.2891 1.42578 26.2891 3.24219ZM4.61914 3.32031C4.0918 3.32031 3.7793 3.82812 4.0625 4.33594L12.5684 19.2383C12.832 19.7266 13.4375 19.7266 13.7207 19.2383L22.2168 4.33594C22.4902 3.83789 22.1875 3.32031 21.6504 3.32031Z", fillAlpha = 0.85f)
        }
        return _sFYieldsignFill!!
    }

private var _sFYieldsignFill: ImageVector? = null
