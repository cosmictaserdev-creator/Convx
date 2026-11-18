package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPowerCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPowerCircleFill: ImageVector
    get() {
        if (_sFPowerCircleFill != null) {
            return _sFPowerCircleFill!!
        }
        _sFPowerCircleFill = sfIcon(
            name = "Monochrome.SFPowerCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM8.27148 7.76367C6.85547 9.00391 6.02539 10.8496 6.02539 12.7246C6.02539 16.3672 9.0625 19.4141 12.7246 19.4141C16.3867 19.4141 19.4238 16.3672 19.4238 12.7246C19.4238 10.8594 18.5938 9.02344 17.1777 7.76367C16.4062 7.05078 15.3613 8.1543 16.1523 8.89648C17.2363 9.87305 17.8613 11.2402 17.8711 12.7246C17.8711 15.5859 15.5762 17.8711 12.7246 17.8711C9.87305 17.8711 7.58789 15.5859 7.58789 12.7246C7.58789 11.2402 8.20312 9.87305 9.29688 8.88672C10.0879 8.14453 9.04297 7.04102 8.27148 7.76367ZM11.9824 6.42578L11.9824 12.1875C11.9824 12.6367 12.2852 12.959 12.7246 12.959C13.1641 12.959 13.4766 12.6367 13.4766 12.1875L13.4766 6.42578C13.4766 5.97656 13.1641 5.66406 12.7246 5.66406C12.2852 5.66406 11.9824 5.97656 11.9824 6.42578Z", fillAlpha = 0.85f)
        }
        return _sFPowerCircleFill!!
    }

private var _sFPowerCircleFill: ImageVector? = null
