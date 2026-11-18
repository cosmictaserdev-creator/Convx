package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFVisionPro (dualtone)
 * Viewport: 35.5762 x 20.1465
 */
public val SfSymbols.Dualtone.SFVisionPro: ImageVector
    get() {
        if (_sFVisionPro != null) {
            return _sFVisionPro!!
        }
        _sFVisionPro = sfIcon(
            name = "Dualtone.SFVisionPro",
            viewportWidth = 35.5762f,
            viewportHeight = 20.1465f
        ) {
            addSfPath("M17.6074 15.5762C20.0098 15.5762 22.4609 19.5898 27.1875 19.5898C31.7676 19.5898 35.2148 15.4297 35.2148 9.95117C35.2148 0.986328 27.3438 0 17.6074 0C7.87109 0 0 0.996094 0 9.95117C0 15.4297 3.44727 19.5898 8.02734 19.5898C12.7539 19.5898 15.2051 15.5762 17.6074 15.5762ZM17.6074 13.8477C14.1699 13.8477 12.3828 17.8613 8.02734 17.8613C4.24805 17.8613 1.72852 14.7852 1.72852 9.95117C1.72852 2.5 8.05664 1.72852 17.6074 1.72852C27.1582 1.72852 33.4863 2.49023 33.4863 9.95117C33.4863 14.7852 30.9766 17.8613 27.1875 17.8613C22.832 17.8613 21.0449 13.8477 17.6074 13.8477Z", fillAlpha = 0.85f)
            addSfPath("M17.6074 13.8477C14.1699 13.8477 12.3828 17.8613 8.02734 17.8613C4.24805 17.8613 1.72852 14.7852 1.72852 9.95117C1.72852 2.5 8.05664 1.72852 17.6074 1.72852C27.1582 1.72852 33.4863 2.49023 33.4863 9.95117C33.4863 14.7852 30.9766 17.8613 27.1875 17.8613C22.832 17.8613 21.0449 13.8477 17.6074 13.8477Z", fillAlpha = 0.2125f)
        }
        return _sFVisionPro!!
    }

private var _sFVisionPro: ImageVector? = null
