package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHockeyPuckCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFHockeyPuckCircleFill: ImageVector
    get() {
        if (_sFHockeyPuckCircleFill != null) {
            return _sFHockeyPuckCircleFill!!
        }
        _sFHockeyPuckCircleFill = sfIcon(
            name = "Monochrome.SFHockeyPuckCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM12.7148 14.5801C9.77539 14.5801 7.05078 13.8086 5.74219 12.4805L5.74219 14.0625C5.74219 16.2109 8.75 17.959 12.7148 17.959C16.6699 17.959 19.6875 16.2109 19.6875 14.0625L19.6875 12.4805C18.3887 13.8086 15.6641 14.5801 12.7148 14.5801ZM5.74219 10.5664C5.74219 12.3242 8.80859 13.7109 12.7148 13.7109C16.6211 13.7109 19.6875 12.3242 19.6875 10.5664C19.6875 8.83789 16.6211 7.48047 12.7148 7.48047C8.80859 7.48047 5.74219 8.83789 5.74219 10.5664Z", fillAlpha = 0.85f)
        }
        return _sFHockeyPuckCircleFill!!
    }

private var _sFHockeyPuckCircleFill: ImageVector? = null
