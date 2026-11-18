package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleBadgeMinus (monochrome)
 * Viewport: 43.9746 x 30.9473
 */
public val SfSymbols.Monochrome.SFRectangleBadgeMinus: ImageVector
    get() {
        if (_sFRectangleBadgeMinus != null) {
            return _sFRectangleBadgeMinus!!
        }
        _sFRectangleBadgeMinus = sfIcon(
            name = "Monochrome.SFRectangleBadgeMinus",
            viewportWidth = 43.9746f,
            viewportHeight = 30.9473f
        ) {
            addSfPath("M36.6016 7.76367L36.6016 23.1934C36.6016 25.6738 35.3223 26.9531 32.793 26.9531L14.7043 26.9531C15.021 26.4159 15.2701 25.836 15.4401 25.2246L32.7637 25.2246C34.1016 25.2246 34.8633 24.502 34.8633 23.0957L34.8633 7.85156C34.8633 6.45508 34.1016 5.72266 32.7637 5.72266L10.8496 5.72266C9.48242 5.72266 8.74023 6.45508 8.74023 7.85156L8.74023 15.2551C8.45835 15.2211 8.17133 15.2051 7.88086 15.2051C7.58705 15.2051 7.29673 15.2215 7.01172 15.2568L7.01172 7.76367C7.01172 5.26367 8.29102 3.99414 10.8105 3.99414L32.793 3.99414C35.3223 3.99414 36.6016 5.27344 36.6016 7.76367Z", fillAlpha = 0.85f)
            addSfPath("M14.1895 23.0762C14.1895 26.5332 11.3086 29.3945 7.88086 29.3945C4.41406 29.3945 1.5625 26.5527 1.5625 23.0762C1.5625 19.6191 4.41406 16.7578 7.88086 16.7578C11.3379 16.7578 14.1895 19.6094 14.1895 23.0762ZM4.58984 22.3633C4.20898 22.3633 3.88672 22.6953 3.88672 23.0762C3.88672 23.4473 4.20898 23.7891 4.58984 23.7891L11.1523 23.7891C11.543 23.7891 11.8652 23.4473 11.8652 23.0762C11.8652 22.6953 11.543 22.3633 11.1523 22.3633Z", fillAlpha = 0.85f)
        }
        return _sFRectangleBadgeMinus!!
    }

private var _sFRectangleBadgeMinus: ImageVector? = null
