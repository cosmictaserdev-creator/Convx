package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownHeartFill (monochrome)
 * Viewport: 25.0879 x 23.4668
 */
public val SfSymbols.Monochrome.SFArrowDownHeartFill: ImageVector
    get() {
        if (_sFArrowDownHeartFill != null) {
            return _sFArrowDownHeartFill!!
        }
        _sFArrowDownHeartFill = sfIcon(
            name = "Monochrome.SFArrowDownHeartFill",
            viewportWidth = 25.0879f,
            viewportHeight = 23.4668f
        ) {
            addSfPath("M24.7266 8.1543C24.7266 13.457 20.1758 18.6523 13.1934 23.1543C12.9492 23.3105 12.6074 23.4668 12.3633 23.4668C12.1289 23.4668 11.7871 23.3105 11.543 23.1543C4.55078 18.6523 0 13.457 0 8.1543C0 3.79883 2.99805 0.693359 6.91406 0.693359C9.31641 0.693359 11.2695 2.05078 12.3633 4.11133C13.4668 2.04102 15.4199 0.693359 17.8125 0.693359C21.7285 0.693359 24.7266 3.79883 24.7266 8.1543ZM11.5137 8.31055L11.5137 13.4082L11.5767 15.654L10.1465 14.1797L9.0625 13.0762C8.90625 12.9199 8.68164 12.8223 8.4668 12.8223C7.99805 12.8223 7.66602 13.1445 7.66602 13.6035C7.66602 13.877 7.79297 14.0625 7.97852 14.2383L11.7383 17.7637C11.9727 17.959 12.1484 18.0371 12.3633 18.0371C12.5879 18.0371 12.7637 17.959 12.9883 17.7637L16.748 14.2383C16.9336 14.0625 17.0605 13.877 17.0605 13.6035C17.0605 13.1445 16.709 12.8223 16.2695 12.8223C16.0449 12.8223 15.8301 12.9199 15.6641 13.0762L14.5898 14.1797L13.1517 15.6557L13.2227 13.4082L13.2227 8.31055C13.2227 7.86133 12.832 7.49023 12.3633 7.49023C11.9043 7.49023 11.5137 7.86133 11.5137 8.31055Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownHeartFill!!
    }

private var _sFArrowDownHeartFill: ImageVector? = null
