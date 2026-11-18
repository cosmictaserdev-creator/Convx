package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHockeyPuckCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFHockeyPuckCircleFill: ImageVector
    get() {
        if (_sFHockeyPuckCircleFill != null) {
            return _sFHockeyPuckCircleFill!!
        }
        _sFHockeyPuckCircleFill = sfIcon(
            name = "Dualtone.SFHockeyPuckCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 13.7109C8.80859 13.7109 5.74219 12.3242 5.74219 10.5664C5.74219 8.83789 8.80859 7.48047 12.7148 7.48047C16.6211 7.48047 19.6875 8.83789 19.6875 10.5664C19.6875 12.3242 16.6211 13.7109 12.7148 13.7109ZM12.7148 17.959C8.75 17.959 5.74219 16.2109 5.74219 14.0625L5.74219 12.4805C7.05078 13.8086 9.77539 14.5801 12.7148 14.5801C15.6641 14.5801 18.3887 13.8086 19.6875 12.4805L19.6875 14.0625C19.6875 16.2109 16.6699 17.959 12.7148 17.959Z", fillAlpha = 0.85f)
        }
        return _sFHockeyPuckCircleFill!!
    }

private var _sFHockeyPuckCircleFill: ImageVector? = null
