package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWave3Down (monochrome)
 * Viewport: 29.8116 x 18.2324
 */
public val SfSymbols.Monochrome.SFWave3Down: ImageVector
    get() {
        if (_sFWave3Down != null) {
            return _sFWave3Down!!
        }
        _sFWave3Down = sfIcon(
            name = "Monochrome.SFWave3Down",
            viewportWidth = 29.8116f,
            viewportHeight = 18.2324f
        ) {
            addSfPath("M14.7251 18.2324C19.9693 18.2324 24.8716 16.6016 28.9732 13.6719C30.0474 12.9102 29.1099 11.3379 27.9869 12.1582C24.1685 14.9219 19.5982 16.3867 14.7251 16.3867C9.86186 16.3867 5.29155 14.9121 1.47319 12.1582C0.369674 11.3672-0.62642 12.8711 0.486861 13.6719C4.58842 16.6016 9.49077 18.2324 14.7251 18.2324Z", fillAlpha = 0.85f)
            addSfPath("M14.7251 11.4453C18.6314 11.4453 22.2935 10.2148 25.2915 7.99805C26.4244 7.14844 25.3306 5.66406 24.2857 6.46484C21.5611 8.51562 18.2505 9.60938 14.7251 9.60938C11.1998 9.60938 7.89897 8.51562 5.17436 6.46484C4.12944 5.66406 3.03569 7.14844 4.1685 7.99805C7.16655 10.2148 10.8287 11.4453 14.7251 11.4453Z", fillAlpha = 0.85f)
            addSfPath("M14.7251 4.67773C17.2935 4.67773 19.6861 3.79883 21.5318 2.20703C22.606 1.29883 21.3951 0.0195312 20.5455 0.693359C18.6802 2.20703 16.981 2.8418 14.7251 2.8418C12.479 2.8418 10.7603 2.20703 8.90483 0.693359C8.06499 0.0292969 6.88335 1.32812 7.92827 2.20703C9.77397 3.79883 12.1665 4.67773 14.7251 4.67773Z", fillAlpha = 0.85f)
        }
        return _sFWave3Down!!
    }

private var _sFWave3Down: ImageVector? = null
