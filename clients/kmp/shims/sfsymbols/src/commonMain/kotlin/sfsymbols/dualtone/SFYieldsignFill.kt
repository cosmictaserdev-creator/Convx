package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFYieldsignFill (dualtone)
 * Viewport: 26.6504 x 24.0723
 */
public val SfSymbols.Dualtone.SFYieldsignFill: ImageVector
    get() {
        if (_sFYieldsignFill != null) {
            return _sFYieldsignFill!!
        }
        _sFYieldsignFill = sfIcon(
            name = "Dualtone.SFYieldsignFill",
            viewportWidth = 26.6504f,
            viewportHeight = 24.0723f
        ) {
            addSfPath("M3.26172 0C1.23047 0 0 1.42578 0 3.24219C0 3.81836 0.15625 4.41406 0.488281 4.99023L10.3613 22.2559C10.9766 23.3301 12.0508 23.8672 13.1445 23.8672C14.2285 23.8672 15.3125 23.3301 15.9277 22.2559L25.8008 4.99023C26.123 4.42383 26.2891 3.81836 26.2891 3.24219C26.2891 1.42578 25.0586 0 23.0176 0Z", fillAlpha = 0.2125f)
            addSfPath("M4.61914 3.32031L21.6504 3.32031C22.1875 3.32031 22.4902 3.83789 22.2168 4.33594L13.7207 19.2383C13.4375 19.7266 12.832 19.7266 12.5684 19.2383L4.0625 4.33594C3.7793 3.82812 4.0918 3.32031 4.61914 3.32031Z", fillAlpha = 0.85f)
        }
        return _sFYieldsignFill!!
    }

private var _sFYieldsignFill: ImageVector? = null
