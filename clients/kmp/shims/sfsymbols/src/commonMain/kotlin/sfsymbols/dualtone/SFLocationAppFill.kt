package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationAppFill (dualtone)
 * Viewport: 23.2715 x 22.9004
 */
public val SfSymbols.Dualtone.SFLocationAppFill: ImageVector
    get() {
        if (_sFLocationAppFill != null) {
            return _sFLocationAppFill!!
        }
        _sFLocationAppFill = sfIcon(
            name = "Dualtone.SFLocationAppFill",
            viewportWidth = 23.2715f,
            viewportHeight = 22.9004f
        ) {
            addSfPath("M6.81641 22.9004L16.0938 22.9004C18.3105 22.9004 20.0293 22.2754 21.1523 21.1523C22.3047 20.0098 22.9102 18.291 22.9102 16.084L22.9102 6.81641C22.9102 4.60938 22.3145 2.90039 21.1523 1.74805C20.0195 0.605469 18.3105 0 16.0938 0L6.81641 0C4.59961 0 2.88086 0.625 1.75781 1.74805C0.605469 2.89062 0 4.60938 0 6.81641L0 16.084C0 18.291 0.595703 20 1.75781 21.1523C2.89062 22.2852 4.59961 22.9004 6.81641 22.9004Z", fillAlpha = 0.2125f)
            addSfPath("M4.33594 12.3926C3.34961 12.3926 3.21289 11.3184 3.97461 10.957L16.3965 5.07812C17.2949 4.6582 18.0566 5.40039 17.6367 6.30859L11.7871 18.7402C11.4453 19.4727 10.3516 19.3848 10.3516 18.3496L10.3418 12.7539C10.3418 12.5391 10.2051 12.4023 10 12.4023Z", fillAlpha = 0.85f)
        }
        return _sFLocationAppFill!!
    }

private var _sFLocationAppFill: ImageVector? = null
