package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMessage (monochrome)
 * Viewport: 29.2871 x 25.9863
 */
public val SfSymbols.Monochrome.SFMessage: ImageVector
    get() {
        if (_sFMessage != null) {
            return _sFMessage!!
        }
        _sFMessage = sfIcon(
            name = "Monochrome.SFMessage",
            viewportWidth = 29.2871f,
            viewportHeight = 25.9863f
        ) {
            addSfPath("M14.4629 24.1602C22.8223 24.1602 28.9258 19.082 28.9258 12.0801C28.9258 5.05859 22.8125 0 14.4629 0C6.10352 0 0 5.05859 0 12.0801C0 14.3945 0.673828 16.543 1.875 18.3008C2.48047 19.209 2.70508 19.8535 2.70508 20.4004C2.70508 21.123 2.46094 21.6602 1.875 22.1875C0.986328 22.9297 1.43555 24.1602 2.59766 24.1602C4.16016 24.1602 5.9082 23.584 7.1875 22.6953C9.30664 23.6523 11.7871 24.1602 14.4629 24.1602ZM14.4629 22.4316C12.0117 22.4316 9.80469 21.9629 7.94922 21.123C7.26562 20.8105 6.74805 20.8984 6.08398 21.2988C5.42969 21.7285 4.60938 22.0801 3.81836 22.1973C4.17969 21.709 4.43359 21.1621 4.43359 20.4004C4.43359 19.502 4.10156 18.5059 3.32031 17.3242C2.29492 15.8398 1.72852 14.043 1.72852 12.0801C1.72852 6.09375 7.00195 1.72852 14.4629 1.72852C21.9238 1.72852 27.1973 6.09375 27.1973 12.0801C27.1973 18.0566 21.9238 22.4316 14.4629 22.4316Z", fillAlpha = 0.85f)
        }
        return _sFMessage!!
    }

private var _sFMessage: ImageVector? = null
