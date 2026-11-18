package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCloudCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFCloudCircleFill: ImageVector
    get() {
        if (_sFCloudCircleFill != null) {
            return _sFCloudCircleFill!!
        }
        _sFCloudCircleFill = sfIcon(
            name = "Monochrome.SFCloudCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM7.03125 11.6309C5.70312 12.002 4.74609 13.1055 4.74609 14.5215C4.74609 16.2305 6.01562 17.4609 7.97852 17.4609L16.7383 17.4609C18.9551 17.4609 20.6836 15.7812 20.6836 13.623C20.6836 11.4355 18.8965 9.81445 16.5137 9.84375C15.6445 8.05664 14.0332 6.95312 12.002 6.95312C9.4043 6.95312 7.25586 9.00391 7.03125 11.6309Z", fillAlpha = 0.85f)
        }
        return _sFCloudCircleFill!!
    }

private var _sFCloudCircleFill: ImageVector? = null
