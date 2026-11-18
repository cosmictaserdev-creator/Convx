package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockHeartFill (dualtone)
 * Viewport: 25.0879 x 23.4668
 */
public val SfSymbols.Dualtone.SFLockHeartFill: ImageVector
    get() {
        if (_sFLockHeartFill != null) {
            return _sFLockHeartFill!!
        }
        _sFLockHeartFill = sfIcon(
            name = "Dualtone.SFLockHeartFill",
            viewportWidth = 25.0879f,
            viewportHeight = 23.4668f
        ) {
            addSfPath("M12.3633 23.4668C12.6074 23.4668 12.9492 23.3105 13.1934 23.1543C20.1758 18.6523 24.7266 13.457 24.7266 8.1543C24.7266 3.79883 21.7285 0.693359 17.8125 0.693359C15.4199 0.693359 13.4668 2.04102 12.3633 4.11133C11.2695 2.05078 9.31641 0.693359 6.91406 0.693359C2.99805 0.693359 0 3.79883 0 8.1543C0 13.457 4.55078 18.6523 11.543 23.1543C11.7871 23.3105 12.1289 23.4668 12.3633 23.4668Z", fillAlpha = 0.2125f)
            addSfPath("M8.86719 16.0645L8.86719 12.1582C8.86719 11.4551 9.16016 11.1133 9.76562 11.084L9.76562 9.88281C9.76562 8.14453 10.8203 6.98242 12.373 6.98242C13.9258 6.98242 14.9805 8.14453 14.9805 9.88281L14.9805 11.084C15.5762 11.1133 15.8789 11.4551 15.8789 12.1582L15.8789 16.0645C15.8789 16.7969 15.5469 17.1387 14.8633 17.1387L9.87305 17.1387C9.18945 17.1387 8.86719 16.7969 8.86719 16.0645ZM10.6641 11.0742L14.0723 11.0742L14.0723 9.79492C14.0723 8.63281 13.3887 7.85156 12.373 7.85156C11.3477 7.85156 10.6641 8.63281 10.6641 9.79492Z", fillAlpha = 0.85f)
        }
        return _sFLockHeartFill!!
    }

private var _sFLockHeartFill: ImageVector? = null
