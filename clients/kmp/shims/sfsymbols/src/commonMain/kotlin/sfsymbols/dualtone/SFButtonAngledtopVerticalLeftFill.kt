package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonAngledtopVerticalLeftFill (dualtone)
 * Viewport: 22.4707 x 26.9434
 */
public val SfSymbols.Dualtone.SFButtonAngledtopVerticalLeftFill: ImageVector
    get() {
        if (_sFButtonAngledtopVerticalLeftFill != null) {
            return _sFButtonAngledtopVerticalLeftFill!!
        }
        _sFButtonAngledtopVerticalLeftFill = sfIcon(
            name = "Dualtone.SFButtonAngledtopVerticalLeftFill",
            viewportWidth = 22.4707f,
            viewportHeight = 26.9434f
        ) {
            addSfPath("M7.35352 26.9434L18.4277 26.9434C20.7324 26.9434 22.0996 25.5859 22.0996 23.3008L22.1094 3.65234C22.1094 1.43555 20.6641 0 18.457 0L14.5898 0C12.6465 0 11.0352 0.488281 9.51172 1.89453L1.55273 9.20898C0.517578 10.166 0 11.5137 0 13.1934L0 19.5898C0 24.0625 2.87109 26.9434 7.35352 26.9434Z", fillAlpha = 0.85f)
        }
        return _sFButtonAngledtopVerticalLeftFill!!
    }

private var _sFButtonAngledtopVerticalLeftFill: ImageVector? = null
