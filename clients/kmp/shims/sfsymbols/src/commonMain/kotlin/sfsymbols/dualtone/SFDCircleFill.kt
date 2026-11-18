package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFDCircleFill: ImageVector
    get() {
        if (_sFDCircleFill != null) {
            return _sFDCircleFill!!
        }
        _sFDCircleFill = sfIcon(
            name = "Dualtone.SFDCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M9.26758 18.4766C8.70117 18.4766 8.39844 18.0762 8.39844 17.4707L8.39844 7.79297C8.39844 7.19727 8.70117 6.79688 9.26758 6.79688L12.5977 6.79688C16.1914 6.79688 18.2227 8.89648 18.2227 12.5977C18.2227 16.3867 16.1621 18.4766 12.5977 18.4766ZM10.1367 17.0703L12.3926 17.0703C14.9707 17.0703 16.416 15.5762 16.416 12.627C16.416 9.77539 14.9512 8.19336 12.3926 8.19336L10.1367 8.19336Z", fillAlpha = 0.85f)
        }
        return _sFDCircleFill!!
    }

private var _sFDCircleFill: ImageVector? = null
