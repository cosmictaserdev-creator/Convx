package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareFillAndLineVerticalAndSquareFill (dualtone)
 * Viewport: 38.75 x 26.084
 */
public val SfSymbols.Dualtone.SFSquareFillAndLineVerticalAndSquareFill: ImageVector
    get() {
        if (_sFSquareFillAndLineVerticalAndSquareFill != null) {
            return _sFSquareFillAndLineVerticalAndSquareFill!!
        }
        _sFSquareFillAndLineVerticalAndSquareFill = sfIcon(
            name = "Dualtone.SFSquareFillAndLineVerticalAndSquareFill",
            viewportWidth = 38.75f,
            viewportHeight = 26.084f
        ) {
            addSfPath("M2.71484 20.3027L11.7773 20.3027C13.4766 20.3027 14.502 19.2871 14.502 17.627L14.502 8.48633C14.502 6.82617 13.4766 5.81055 11.7773 5.81055L2.71484 5.81055C1.02539 5.81055 0 6.82617 0 8.48633L0 17.627C0 19.2871 1.02539 20.3027 2.71484 20.3027ZM19.1895 26.084C19.668 26.084 19.9902 25.7715 19.9902 25.3223L19.9902 0.791016C19.9902 0.341797 19.668 0.0292969 19.1895 0.0292969C18.7109 0.0292969 18.3984 0.341797 18.3984 0.791016L18.3984 25.3223C18.3984 25.7715 18.7109 26.084 19.1895 26.084ZM26.6016 20.3027L35.6641 20.3027C37.3633 20.3027 38.3887 19.2871 38.3887 17.627L38.3887 8.48633C38.3887 6.82617 37.3633 5.81055 35.6641 5.81055L26.6016 5.81055C24.9121 5.81055 23.8867 6.82617 23.8867 8.48633L23.8867 17.627C23.8867 19.2871 24.9121 20.3027 26.6016 20.3027Z", fillAlpha = 0.85f)
        }
        return _sFSquareFillAndLineVerticalAndSquareFill!!
    }

private var _sFSquareFillAndLineVerticalAndSquareFill: ImageVector? = null
