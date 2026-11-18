package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBedDoubleFill (dualtone)
 * Viewport: 31.8848 x 20.7324
 */
public val SfSymbols.Dualtone.SFBedDoubleFill: ImageVector
    get() {
        if (_sFBedDoubleFill != null) {
            return _sFBedDoubleFill!!
        }
        _sFBedDoubleFill = sfIcon(
            name = "Dualtone.SFBedDoubleFill",
            viewportWidth = 31.8848f,
            viewportHeight = 20.7324f
        ) {
            addSfPath("M0 19.6973C0 20.3223 0.380859 20.7129 1.00586 20.7129L1.70898 20.7129C2.32422 20.7129 2.70508 20.3223 2.70508 19.6973L2.70508 17.7734C2.8418 17.8125 3.18359 17.832 3.44727 17.832L28.0762 17.832C28.3398 17.832 28.6816 17.8125 28.8184 17.7734L28.8184 19.6973C28.8184 20.3223 29.209 20.7129 29.8242 20.7129L30.5273 20.7129C31.1426 20.7129 31.5234 20.3223 31.5234 19.6973L31.5234 12.0117C31.5234 9.81445 30.2832 8.57422 28.0859 8.57422L3.4375 8.57422C1.24023 8.57422 0 9.81445 0 12.0117Z", fillAlpha = 0.85f)
            addSfPath("M3.02734 6.94336L6.08398 6.94336L6.08398 5.22461C6.08398 4.0918 6.73828 3.4375 7.88086 3.4375L12.6953 3.4375C13.8379 3.4375 14.4922 4.0918 14.4922 5.22461L14.4922 6.94336L17.3145 6.94336L17.3145 5.22461C17.3145 4.0918 17.9688 3.4375 19.2383 3.4375L23.5254 3.4375C24.7949 3.4375 25.459 4.0918 25.459 5.22461L25.459 6.94336L28.5156 6.94336L28.5156 3.39844C28.5156 1.13281 27.3145 0 25.1953 0L6.33789 0C4.22852 0 3.02734 1.13281 3.02734 3.39844Z", fillAlpha = 0.425f)
        }
        return _sFBedDoubleFill!!
    }

private var _sFBedDoubleFill: ImageVector? = null
