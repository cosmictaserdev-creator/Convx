package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPyramid (dualtone)
 * Viewport: 25.8098 x 28.9845
 */
public val SfSymbols.Dualtone.SFPyramid: ImageVector
    get() {
        if (_sFPyramid != null) {
            return _sFPyramid!!
        }
        _sFPyramid = sfIcon(
            name = "Dualtone.SFPyramid",
            viewportWidth = 25.8098f,
            viewportHeight = 28.9845f
        ) {
            addSfPath("M0.312138 21.5088C-0.2738 22.544-0.0394245 23.6573 1.05433 24.1358L11.6989 28.7452C12.4215 29.0674 12.9977 29.0674 13.7789 28.7256L24.4039 24.1358C25.4879 23.6573 25.7321 22.544 25.1364 21.5088L13.8571 1.85064C13.5348 1.294 13.1344 1.15728 12.7242 1.15728C12.3239 1.15728 11.9137 1.294 11.6012 1.85064ZM2.04065 21.8702L12.4606 3.794C12.5289 3.68658 12.6168 3.62798 12.7242 3.62798C12.8414 3.62798 12.9293 3.68658 12.9879 3.794L23.4078 21.8702C23.652 22.2999 23.5055 22.6905 23.0953 22.876L13.4274 27.2315C12.8707 27.4756 12.5875 27.4756 12.0797 27.251L2.35315 22.876C1.943 22.6905 1.79651 22.2999 2.04065 21.8702ZM11.9137 28.1983L13.5446 28.1983L13.5446 2.74908L11.9137 2.74908Z", fillAlpha = 0.85f)
        }
        return _sFPyramid!!
    }

private var _sFPyramid: ImageVector? = null
