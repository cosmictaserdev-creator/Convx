package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockHeartFill (monochrome)
 * Viewport: 25.0879 x 23.4668
 */
public val SfSymbols.Monochrome.SFLockHeartFill: ImageVector
    get() {
        if (_sFLockHeartFill != null) {
            return _sFLockHeartFill!!
        }
        _sFLockHeartFill = sfIcon(
            name = "Monochrome.SFLockHeartFill",
            viewportWidth = 25.0879f,
            viewportHeight = 23.4668f
        ) {
            addSfPath("M24.7266 8.1543C24.7266 13.457 20.1758 18.6523 13.1934 23.1543C12.9492 23.3105 12.6074 23.4668 12.3633 23.4668C12.1289 23.4668 11.7871 23.3105 11.543 23.1543C4.55078 18.6523 0 13.457 0 8.1543C0 3.79883 2.99805 0.693359 6.91406 0.693359C9.31641 0.693359 11.2695 2.05078 12.3633 4.11133C13.4668 2.04102 15.4199 0.693359 17.8125 0.693359C21.7285 0.693359 24.7266 3.79883 24.7266 8.1543ZM9.76562 9.88281L9.76562 11.084C9.16016 11.1133 8.86719 11.4551 8.86719 12.1582L8.86719 16.0645C8.86719 16.7969 9.18945 17.1387 9.87305 17.1387L14.8633 17.1387C15.5469 17.1387 15.8789 16.7969 15.8789 16.0645L15.8789 12.1582C15.8789 11.4551 15.5762 11.1133 14.9805 11.084L14.9805 9.88281C14.9805 8.14453 13.9258 6.98242 12.373 6.98242C10.8203 6.98242 9.76562 8.14453 9.76562 9.88281ZM14.0723 9.79492L14.0723 11.0742L10.6641 11.0742L10.6641 9.79492C10.6641 8.63281 11.3477 7.85156 12.373 7.85156C13.3887 7.85156 14.0723 8.63281 14.0723 9.79492Z", fillAlpha = 0.85f)
        }
        return _sFLockHeartFill!!
    }

private var _sFLockHeartFill: ImageVector? = null
