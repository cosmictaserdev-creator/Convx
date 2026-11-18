package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTentFill (monochrome)
 * Viewport: 37.5781 x 25.7129
 */
public val SfSymbols.Monochrome.SFTentFill: ImageVector
    get() {
        if (_sFTentFill != null) {
            return _sFTentFill!!
        }
        _sFTentFill = sfIcon(
            name = "Monochrome.SFTentFill",
            viewportWidth = 37.5781f,
            viewportHeight = 25.7129f
        ) {
            addSfPath("M7.40234 25.7129L29.8047 25.7129C31.2305 25.7129 31.6504 24.4629 31.084 23.3301L24.1309 9.36523L35.3809 25.1465C35.6445 25.5273 35.9863 25.6641 36.3086 25.6641C36.7773 25.6641 37.2168 25.3809 37.2168 24.7461L37.2168 21.0938C37.2168 20.6836 36.8848 20.3516 36.4746 20.3516C36.0742 20.3516 35.7422 20.6836 35.7422 21.0938L35.7422 23.1543L20 1.08398C19.2383 0.0390625 17.959 0.0488281 17.2168 1.08398L1.47461 23.1543L1.47461 21.0938C1.47461 20.6836 1.14258 20.3516 0.732422 20.3516C0.332031 20.3516 0 20.6836 0 21.0938L0 24.7461C0 25.3809 0.439453 25.6641 0.908203 25.6641C1.23047 25.6641 1.5625 25.5273 1.83594 25.1465L13.0859 9.36523L6.13281 23.3301C5.55664 24.4629 5.98633 25.7129 7.40234 25.7129ZM12.4512 24.0625L17.8809 12.4512C18.0273 12.1289 18.3105 11.9727 18.6035 11.9727C18.8965 11.9727 19.1699 12.1289 19.3262 12.4512L24.7559 24.0625L20.5957 24.0625L18.6035 15.1172L16.6113 24.0625Z", fillAlpha = 0.85f)
        }
        return _sFTentFill!!
    }

private var _sFTentFill: ImageVector? = null
