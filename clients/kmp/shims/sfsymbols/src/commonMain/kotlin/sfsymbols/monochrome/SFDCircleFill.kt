package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFDCircleFill: ImageVector
    get() {
        if (_sFDCircleFill != null) {
            return _sFDCircleFill!!
        }
        _sFDCircleFill = sfIcon(
            name = "Monochrome.SFDCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM9.26758 6.79688C8.70117 6.79688 8.39844 7.19727 8.39844 7.79297L8.39844 17.4707C8.39844 18.0762 8.70117 18.4766 9.26758 18.4766L12.5977 18.4766C16.1621 18.4766 18.2227 16.3867 18.2227 12.5977C18.2227 8.89648 16.1914 6.79688 12.5977 6.79688ZM16.416 12.627C16.416 15.5762 14.9707 17.0703 12.3926 17.0703L10.1367 17.0703L10.1367 8.19336L12.3926 8.19336C14.9512 8.19336 16.416 9.77539 16.416 12.627Z", fillAlpha = 0.85f)
        }
        return _sFDCircleFill!!
    }

private var _sFDCircleFill: ImageVector? = null
