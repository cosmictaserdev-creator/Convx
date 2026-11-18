package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectanglePortraitSlashFill (monochrome)
 * Viewport: 28.4595 x 28.1178
 */
public val SfSymbols.Monochrome.SFRectanglePortraitSlashFill: ImageVector
    get() {
        if (_sFRectanglePortraitSlashFill != null) {
            return _sFRectanglePortraitSlashFill!!
        }
        _sFRectanglePortraitSlashFill = sfIcon(
            name = "Monochrome.SFRectanglePortraitSlashFill",
            viewportWidth = 28.4595f,
            viewportHeight = 28.1178f
        ) {
            addSfPath("M23.5344 26.6214C22.9092 27.2204 21.9891 27.5208 20.7825 27.5208L7.33521 27.5208C4.83521 27.5208 3.57544 26.2415 3.57544 23.722L3.57544 6.68709ZM24.5422 4.40556L24.5422 21.4368L4.59649 1.49106C5.2197 0.901892 6.13439 0.606737 7.33521 0.606737L20.7825 0.606737C23.2727 0.606737 24.5422 1.88603 24.5422 4.40556Z", fillAlpha = 0.85f)
            addSfPath("M25.3137 26.4954C25.6555 26.8177 26.1731 26.8177 26.4954 26.4954C26.8176 26.1634 26.8176 25.6263 26.4954 25.304L2.78443 1.6126C2.47193 1.29033 1.93482 1.28056 1.60279 1.6126C1.28052 1.9251 1.28052 2.47197 1.60279 2.78447Z", fillAlpha = 0.85f)
        }
        return _sFRectanglePortraitSlashFill!!
    }

private var _sFRectanglePortraitSlashFill: ImageVector? = null
