package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWave3Up (monochrome)
 * Viewport: 29.8116 x 18.2293
 */
public val SfSymbols.Monochrome.SFWave3Up: ImageVector
    get() {
        if (_sFWave3Up != null) {
            return _sFWave3Up!!
        }
        _sFWave3Up = sfIcon(
            name = "Monochrome.SFWave3Up",
            viewportWidth = 29.8116f,
            viewportHeight = 18.2293f
        ) {
            addSfPath("M14.7251 0.506232C9.49077 0.506232 4.58842 2.13709 0.486861 5.05701C-0.62642 5.85779 0.369674 7.37147 1.47319 6.57069C5.29155 3.81678 9.86186 2.34217 14.7251 2.34217C19.5982 2.34217 24.1685 3.81678 27.9869 6.57069C29.1099 7.391 30.0474 5.81873 28.9732 5.05701C24.8716 2.12733 19.9693 0.506232 14.7251 0.506232Z", fillAlpha = 0.85f)
            addSfPath("M14.7251 7.28358C10.8287 7.28358 7.16655 8.51404 4.1685 10.7406C3.03569 11.5805 4.12944 13.0746 5.17436 12.2738C7.89897 10.2133 11.1998 9.11951 14.7251 9.11951C18.2505 9.11951 21.5611 10.2133 24.2857 12.2738C25.3306 13.0746 26.4244 11.5805 25.2915 10.7406C22.2935 8.51404 18.6314 7.28358 14.7251 7.28358Z", fillAlpha = 0.85f)
            addSfPath("M14.7251 14.0512C12.1665 14.0512 9.77397 14.9398 7.92827 16.5219C6.88335 17.4008 8.06499 18.6996 8.90483 18.0355C10.7603 16.5316 12.479 15.8871 14.7251 15.8871C16.981 15.8871 18.6802 16.5316 20.5455 18.0453C21.3951 18.7094 22.606 17.4301 21.5318 16.5219C19.6861 14.9398 17.2935 14.0512 14.7251 14.0512Z", fillAlpha = 0.85f)
        }
        return _sFWave3Up!!
    }

private var _sFWave3Up: ImageVector? = null
