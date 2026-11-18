package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIphoneLandscape (monochrome)
 * Viewport: 26.582 x 15.6934
 */
public val SfSymbols.Monochrome.SFIphoneLandscape: ImageVector
    get() {
        if (_sFIphoneLandscape != null) {
            return _sFIphoneLandscape!!
        }
        _sFIphoneLandscape = sfIcon(
            name = "Monochrome.SFIphoneLandscape",
            viewportWidth = 26.582f,
            viewportHeight = 15.6934f
        ) {
            addSfPath("M3.22266 15.6543L22.998 15.6543C24.9707 15.6543 26.2207 14.3652 26.2207 12.3242L26.2207 3.33008C26.2207 1.28906 24.9707 0 22.998 0L3.22266 0C1.25977 0 0 1.28906 0 3.33008L0 12.3242C0 14.3652 1.25977 15.6543 3.22266 15.6543ZM3.48633 13.9258C2.33398 13.9258 1.73828 13.3008 1.73828 12.1191L1.73828 3.54492C1.73828 2.35352 2.33398 1.72852 3.48633 1.72852L22.7441 1.72852C23.8965 1.72852 24.4922 2.35352 24.4922 3.54492L24.4922 12.1191C24.4922 13.3008 23.8965 13.9258 22.7441 13.9258ZM9.77539 13.0273L16.4551 13.0273C16.7676 13.0273 16.9922 12.8125 16.9922 12.5C16.9922 12.1777 16.7676 11.9629 16.4551 11.9629L9.77539 11.9629C9.47266 11.9629 9.24805 12.1777 9.24805 12.5C9.24805 12.8125 9.47266 13.0273 9.77539 13.0273ZM3.37891 10.127C3.83789 10.127 4.18945 9.77539 4.18945 9.31641L4.18945 6.33789C4.18945 5.88867 3.83789 5.53711 3.37891 5.53711C2.92969 5.53711 2.57812 5.88867 2.57812 6.33789L2.57812 9.31641C2.57812 9.77539 2.92969 10.127 3.37891 10.127Z", fillAlpha = 0.85f)
        }
        return _sFIphoneLandscape!!
    }

private var _sFIphoneLandscape: ImageVector? = null
