package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationAppFill (monochrome)
 * Viewport: 23.2715 x 22.9004
 */
public val SfSymbols.Monochrome.SFLocationAppFill: ImageVector
    get() {
        if (_sFLocationAppFill != null) {
            return _sFLocationAppFill!!
        }
        _sFLocationAppFill = sfIcon(
            name = "Monochrome.SFLocationAppFill",
            viewportWidth = 23.2715f,
            viewportHeight = 22.9004f
        ) {
            addSfPath("M21.1523 1.74805C22.3145 2.90039 22.9102 4.60938 22.9102 6.81641L22.9102 16.084C22.9102 18.291 22.3047 20.0098 21.1523 21.1523C20.0293 22.2754 18.3105 22.9004 16.0938 22.9004L6.81641 22.9004C4.59961 22.9004 2.89062 22.2852 1.75781 21.1523C0.595703 20 0 18.291 0 16.084L0 6.81641C0 4.60938 0.605469 2.89062 1.75781 1.74805C2.88086 0.625 4.59961 0 6.81641 0L16.0938 0C18.3105 0 20.0195 0.605469 21.1523 1.74805ZM16.3965 5.07812L3.97461 10.957C3.21289 11.3184 3.34961 12.3926 4.33594 12.3926L10 12.4023C10.2051 12.4023 10.3418 12.5391 10.3418 12.7539L10.3516 18.3496C10.3516 19.3848 11.4453 19.4727 11.7871 18.7402L17.6367 6.30859C18.0566 5.40039 17.2949 4.6582 16.3965 5.07812Z", fillAlpha = 0.85f)
        }
        return _sFLocationAppFill!!
    }

private var _sFLocationAppFill: ImageVector? = null
