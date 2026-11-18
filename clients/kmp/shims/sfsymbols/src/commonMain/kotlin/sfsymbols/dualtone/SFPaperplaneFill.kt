package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPaperplaneFill (dualtone)
 * Viewport: 27.7539 x 27.373
 */
public val SfSymbols.Dualtone.SFPaperplaneFill: ImageVector
    get() {
        if (_sFPaperplaneFill != null) {
            return _sFPaperplaneFill!!
        }
        _sFPaperplaneFill = sfIcon(
            name = "Dualtone.SFPaperplaneFill",
            viewportWidth = 27.7539f,
            viewportHeight = 27.373f
        ) {
            addSfPath("M15.625 27.373C16.4551 27.373 17.041 26.6895 17.4609 25.5762L25.8008 3.78906C25.9961 3.28125 26.1035 2.83203 26.1035 2.46094C26.1035 1.71875 25.6543 1.25977 24.9121 1.25977C24.5312 1.25977 24.0918 1.37695 23.584 1.57227L1.69922 9.94141C0.712891 10.3223 0 10.9082 0 11.748C0 12.7734 0.761719 13.1445 1.91406 13.4863L9.11133 15.6836C9.81445 15.8984 10.2441 15.8594 10.7324 15.4102L24.4141 2.5293C24.5703 2.39258 24.7363 2.40234 24.8535 2.50977C24.9805 2.61719 24.9707 2.80273 24.8438 2.95898L11.9922 16.6699C11.5625 17.1387 11.5039 17.5586 11.7188 18.291L13.8574 25.3711C14.209 26.5625 14.5801 27.373 15.625 27.373Z", fillAlpha = 0.85f)
        }
        return _sFPaperplaneFill!!
    }

private var _sFPaperplaneFill: ImageVector? = null
