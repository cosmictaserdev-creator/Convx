package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationFill (monochrome)
 * Viewport: 25.8012 x 23.6879
 */
public val SfSymbols.Monochrome.SFLocationFill: ImageVector
    get() {
        if (_sFLocationFill != null) {
            return _sFLocationFill!!
        }
        _sFLocationFill = sfIcon(
            name = "Monochrome.SFLocationFill",
            viewportWidth = 25.8012f,
            viewportHeight = 23.6879f
        ) {
            addSfPath("M1.46505 12.4836L11.0158 12.5227C11.1818 12.5227 11.2404 12.591 11.2404 12.7571L11.2697 22.2395C11.2697 23.8899 13.2619 24.2512 13.9943 22.6887L23.5158 2.27854C24.2678 0.637911 22.9787-0.426543 21.426 0.296114L0.908411 9.83713C-0.517371 10.4914-0.214636 12.4738 1.46505 12.4836Z", fillAlpha = 0.85f)
        }
        return _sFLocationFill!!
    }

private var _sFLocationFill: ImageVector? = null
