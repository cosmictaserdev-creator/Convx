package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHatCap (monochrome)
 * Viewport: 34.9152 x 19.9707
 */
public val SfSymbols.Monochrome.SFHatCap: ImageVector
    get() {
        if (_sFHatCap != null) {
            return _sFHatCap!!
        }
        _sFHatCap = sfIcon(
            name = "Monochrome.SFHatCap",
            viewportWidth = 34.9152f,
            viewportHeight = 19.9707f
        ) {
            addSfPath("M1.52008 18.7598C8.79547 18.3398 22.8091 15.2344 26.1685 12.1582C23.6002 5.99609 20.5728 0.273438 10.1138 2.03125C1.88141 3.41797-1.34125 10.3223 0.504455 18.0078C0.631408 18.5352 0.96344 18.7891 1.52008 18.7598ZM2.11578 17.0508C0.660705 10.4492 3.41461 4.82422 10.4361 3.67188C18.942 2.27539 21.7349 6.23047 24.0201 11.5039C20.9634 13.8379 8.31696 16.7285 2.11578 17.0508ZM8.38531 2.71484L11.8716 2.23633L11.7642 1.41602C11.6861 0.791016 11.0611 0.419922 10.2603 0.546875L9.36188 0.703125C8.61969 0.820312 8.19 1.41602 8.28766 2.06055ZM28.3463 19.9707C31.3834 19.9707 33.6295 17.2656 34.274 15.752C34.8306 14.3457 34.5181 13.8281 33.815 13.6133L25.065 10.9668L24.5572 12.5098L32.5162 14.9121C32.1646 16.4648 30.2994 18.2324 28.2193 18.2324C25.4361 18.2324 22.1646 16.9531 18.0728 14.9414L15.6998 15.6445C19.6744 17.5488 24.1763 19.9707 28.3463 19.9707Z", fillAlpha = 0.85f)
        }
        return _sFHatCap!!
    }

private var _sFHatCap: ImageVector? = null
