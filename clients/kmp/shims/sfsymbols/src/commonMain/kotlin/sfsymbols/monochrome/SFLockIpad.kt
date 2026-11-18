package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockIpad (monochrome)
 * Viewport: 21.084 x 27.998
 */
public val SfSymbols.Monochrome.SFLockIpad: ImageVector
    get() {
        if (_sFLockIpad != null) {
            return _sFLockIpad!!
        }
        _sFLockIpad = sfIcon(
            name = "Monochrome.SFLockIpad",
            viewportWidth = 21.084f,
            viewportHeight = 27.998f
        ) {
            addSfPath("M0 24.6777C0 26.6992 1.34766 27.998 3.4375 27.998L17.2852 27.998C19.375 27.998 20.7227 26.6992 20.7227 24.6777L20.7227 3.32031C20.7227 1.29883 19.375 0 17.2852 0L3.4375 0C1.34766 0 0 1.29883 0 3.32031ZM1.72852 24.4043L1.72852 3.59375C1.72852 2.40234 2.42188 1.72852 3.64258 1.72852L17.0703 1.72852C18.291 1.72852 18.9844 2.40234 18.9844 3.59375L18.9844 24.4043C18.9844 25.5859 18.291 26.2598 17.0703 26.2598L3.64258 26.2598C2.42188 26.2598 1.72852 25.5859 1.72852 24.4043Z", fillAlpha = 0.85f)
            addSfPath("M8.21289 18.252L12.5098 18.252C13.2031 18.252 13.5547 17.8809 13.5547 17.1387L13.5547 13.75C13.5547 13.1445 13.3105 12.7832 12.832 12.666L12.832 11.9043C12.832 10.2832 11.8359 9.16016 10.3613 9.16016C8.88672 9.16016 7.89062 10.2832 7.89062 11.9043L7.89062 12.6758C7.41211 12.7832 7.16797 13.1445 7.16797 13.75L7.16797 17.1387C7.16797 17.8809 7.51953 18.252 8.21289 18.252ZM9.02344 12.6367L9.02344 11.8164C9.02344 10.8789 9.55078 10.2637 10.3613 10.2637C11.1719 10.2637 11.6992 10.8789 11.6992 11.8164L11.6992 12.6367Z", fillAlpha = 0.85f)
        }
        return _sFLockIpad!!
    }

private var _sFLockIpad: ImageVector? = null
