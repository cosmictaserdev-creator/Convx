package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVisionPro (monochrome)
 * Viewport: 35.5762 x 20.1465
 */
public val SfSymbols.Monochrome.SFVisionPro: ImageVector
    get() {
        if (_sFVisionPro != null) {
            return _sFVisionPro!!
        }
        _sFVisionPro = sfIcon(
            name = "Monochrome.SFVisionPro",
            viewportWidth = 35.5762f,
            viewportHeight = 20.1465f
        ) {
            addSfPath("M35.2148 9.95117C35.2148 15.4297 31.7676 19.5898 27.1875 19.5898C22.4609 19.5898 20.0098 15.5762 17.6074 15.5762C15.2051 15.5762 12.7539 19.5898 8.02734 19.5898C3.44727 19.5898 0 15.4297 0 9.95117C0 0.996094 7.87109 0 17.6074 0C27.3438 0 35.2148 0.986328 35.2148 9.95117ZM1.72852 9.95117C1.72852 14.7852 4.24805 17.8613 8.02734 17.8613C12.3828 17.8613 14.1699 13.8477 17.6074 13.8477C21.0449 13.8477 22.832 17.8613 27.1875 17.8613C30.9766 17.8613 33.4863 14.7852 33.4863 9.95117C33.4863 2.49023 27.1582 1.72852 17.6074 1.72852C8.05664 1.72852 1.72852 2.5 1.72852 9.95117Z", fillAlpha = 0.85f)
        }
        return _sFVisionPro!!
    }

private var _sFVisionPro: ImageVector? = null
