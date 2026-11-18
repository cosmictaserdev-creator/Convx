package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpHeartFill (monochrome)
 * Viewport: 25.0879 x 23.4668
 */
public val SfSymbols.Monochrome.SFArrowUpHeartFill: ImageVector
    get() {
        if (_sFArrowUpHeartFill != null) {
            return _sFArrowUpHeartFill!!
        }
        _sFArrowUpHeartFill = sfIcon(
            name = "Monochrome.SFArrowUpHeartFill",
            viewportWidth = 25.0879f,
            viewportHeight = 23.4668f
        ) {
            addSfPath("M24.7266 8.1543C24.7266 13.457 20.1758 18.6523 13.1934 23.1543C12.9492 23.3105 12.6074 23.4668 12.3633 23.4668C12.1289 23.4668 11.7871 23.3105 11.543 23.1543C4.55078 18.6523 0 13.457 0 8.1543C0 3.79883 2.99805 0.693359 6.91406 0.693359C9.31641 0.693359 11.2695 2.05078 12.3633 4.11133C13.4668 2.04102 15.4199 0.693359 17.8125 0.693359C21.7285 0.693359 24.7266 3.79883 24.7266 8.1543ZM11.7383 7.76367L7.97852 11.2891C7.79297 11.4648 7.66602 11.6504 7.66602 11.9238C7.66602 12.3828 7.99805 12.7051 8.4668 12.7051C8.68164 12.7051 8.90625 12.6074 9.0625 12.4512L10.1465 11.3477L11.5767 9.87332L11.5137 12.1191L11.5137 17.2168C11.5137 17.666 11.9043 18.0371 12.3633 18.0371C12.832 18.0371 13.2227 17.666 13.2227 17.2168L13.2227 12.1191L13.1517 9.87165L14.5898 11.3477L15.6641 12.4512C15.8301 12.6074 16.0449 12.7051 16.2695 12.7051C16.709 12.7051 17.0605 12.3828 17.0605 11.9238C17.0605 11.6504 16.9336 11.4648 16.748 11.2891L12.9883 7.76367C12.7637 7.56836 12.5879 7.49023 12.3633 7.49023C12.1484 7.49023 11.9727 7.56836 11.7383 7.76367Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpHeartFill!!
    }

private var _sFArrowUpHeartFill: ImageVector? = null
