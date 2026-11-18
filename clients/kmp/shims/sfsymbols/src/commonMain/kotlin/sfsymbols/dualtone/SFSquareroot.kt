package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareroot (dualtone)
 * Viewport: 22.9492 x 21.8848
 */
public val SfSymbols.Dualtone.SFSquareroot: ImageVector
    get() {
        if (_sFSquareroot != null) {
            return _sFSquareroot!!
        }
        _sFSquareroot = sfIcon(
            name = "Dualtone.SFSquareroot",
            viewportWidth = 22.9492f,
            viewportHeight = 21.8848f
        ) {
            addSfPath("M4.44336 21.8848C5.42969 21.8848 5.94727 21.1914 6.2207 20.3027L11.8555 1.91406L21.5625 1.91406C22.1777 1.91406 22.5879 1.5332 22.5879 0.957031C22.5879 0.390625 22.1777 0.0195312 21.5625 0.0195312L11.8262 0.0195312C10.752 0.0195312 10.2637 0.419922 9.9707 1.33789L4.57031 19.3457L4.38477 19.3457L1.96289 10.8789C1.79688 10.293 1.48438 10.0195 0.976562 10.0195C0.419922 10.0195 0 10.4199 0 10.9473C0 11.1523 0.0488281 11.3477 0.107422 11.5234L2.79297 20.4199C3.05664 21.2793 3.53516 21.8848 4.44336 21.8848Z", fillAlpha = 0.85f)
        }
        return _sFSquareroot!!
    }

private var _sFSquareroot: ImageVector? = null
