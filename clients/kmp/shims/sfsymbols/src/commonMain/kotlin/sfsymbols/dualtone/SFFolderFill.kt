package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFolderFill (dualtone)
 * Viewport: 28.7305 x 22.9395
 */
public val SfSymbols.Dualtone.SFFolderFill: ImageVector
    get() {
        if (_sFFolderFill != null) {
            return _sFFolderFill!!
        }
        _sFFolderFill = sfIcon(
            name = "Dualtone.SFFolderFill",
            viewportWidth = 28.7305f,
            viewportHeight = 22.9395f
        ) {
            addSfPath("M3.79883 22.8027L24.8633 22.8027C27.0996 22.8027 28.3691 21.5234 28.3691 19.043L28.3691 6.08398C28.3691 3.59375 27.0898 2.32422 24.5703 2.32422L11.9238 2.32422C11.0059 2.32422 10.498 2.12891 9.85352 1.5625L9.11133 0.9375C8.28125 0.214844 7.67578 0 6.42578 0L3.33008 0C1.19141 0 0 1.18164 0 3.57422L0 19.043C0 21.5332 1.2793 22.8027 3.79883 22.8027ZM2.13867 6.73828L2.13867 6.16211C2.13867 5.37109 2.68555 4.84375 3.69141 4.84375L24.6387 4.84375C25.6445 4.84375 26.1914 5.37109 26.1914 6.16211L26.1914 6.73828Z", fillAlpha = 0.85f)
        }
        return _sFFolderFill!!
    }

private var _sFFolderFill: ImageVector? = null
