package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCloudCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFCloudCircleFill: ImageVector
    get() {
        if (_sFCloudCircleFill != null) {
            return _sFCloudCircleFill!!
        }
        _sFCloudCircleFill = sfIcon(
            name = "Dualtone.SFCloudCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M7.97852 17.4609C6.01562 17.4609 4.74609 16.2305 4.74609 14.5215C4.74609 13.1055 5.70312 12.002 7.03125 11.6309C7.25586 9.00391 9.4043 6.95312 12.002 6.95312C14.0332 6.95312 15.6445 8.05664 16.5137 9.84375C18.8965 9.81445 20.6836 11.4355 20.6836 13.623C20.6836 15.7812 18.9551 17.4609 16.7383 17.4609Z", fillAlpha = 0.85f)
        }
        return _sFCloudCircleFill!!
    }

private var _sFCloudCircleFill: ImageVector? = null
