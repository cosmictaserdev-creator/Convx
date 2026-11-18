package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFButtonAngledtopVerticalRightFill (dualtone)
 * Viewport: 22.4707 x 26.9434
 */
public val SfSymbols.Dualtone.SFButtonAngledtopVerticalRightFill: ImageVector
    get() {
        if (_sFButtonAngledtopVerticalRightFill != null) {
            return _sFButtonAngledtopVerticalRightFill!!
        }
        _sFButtonAngledtopVerticalRightFill = sfIcon(
            name = "Dualtone.SFButtonAngledtopVerticalRightFill",
            viewportWidth = 22.4707f,
            viewportHeight = 26.9434f
        ) {
            addSfPath("M14.7559 26.9434C19.2285 26.9434 22.1094 24.0625 22.1094 19.5898L22.1094 13.1934C22.1094 11.5137 21.5918 10.166 20.5566 9.20898L12.5977 1.89453C11.0742 0.488281 9.46289 0 7.51953 0L3.64258 0C1.43555 0 0 1.43555 0 3.65234L0 23.3008C0 25.5859 1.35742 26.9434 3.67188 26.9434Z", fillAlpha = 0.85f)
        }
        return _sFButtonAngledtopVerticalRightFill!!
    }

private var _sFButtonAngledtopVerticalRightFill: ImageVector? = null
