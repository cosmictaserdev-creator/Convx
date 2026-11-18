package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltHorizontal (dualtone)
 * Viewport: 34.5178 x 16.3184
 */
public val SfSymbols.Dualtone.SFBoltHorizontal: ImageVector
    get() {
        if (_sFBoltHorizontal != null) {
            return _sFBoltHorizontal!!
        }
        _sFBoltHorizontal = sfIcon(
            name = "Dualtone.SFBoltHorizontal",
            viewportWidth = 34.5178f,
            viewportHeight = 16.3184f
        ) {
            addSfPath("M0.378992 13.5254C-0.66593 14.8633 0.65243 15.9668 1.8829 15.2539L10.9942 10.0195L22.0782 15.9668C22.5079 16.1914 22.879 16.3184 23.2306 16.3184C23.7384 16.3184 24.1974 16.084 24.627 15.5371L33.836 3.88672C34.7247 2.75391 33.6505 1.37695 32.3028 2.12891L23.2208 7.39258L12.1075 1.43555C11.6876 1.20117 11.3067 1.09375 10.9552 1.09375C10.4571 1.09375 10.0177 1.32812 9.58798 1.875ZM2.8204 13.125C2.69345 13.2031 2.62509 13.0859 2.71298 12.9785L10.838 3.04688C10.9845 2.86133 11.1309 2.83203 11.3263 2.93945L22.7716 9.05273C23.2599 9.31641 23.4259 9.24805 23.8263 9.00391L31.4044 4.24805C31.5216 4.16992 31.629 4.29688 31.5411 4.4043L23.3673 14.3652C23.2208 14.5508 23.0743 14.5801 22.879 14.4727L11.4337 8.35938C10.9454 8.0957 10.7794 8.16406 10.3888 8.4082Z", fillAlpha = 0.85f)
        }
        return _sFBoltHorizontal!!
    }

private var _sFBoltHorizontal: ImageVector? = null
