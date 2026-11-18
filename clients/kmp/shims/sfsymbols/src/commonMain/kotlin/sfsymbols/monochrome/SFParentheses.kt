package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFParentheses (monochrome)
 * Viewport: 18.6816 x 27.8223
 */
public val SfSymbols.Monochrome.SFParentheses: ImageVector
    get() {
        if (_sFParentheses != null) {
            return _sFParentheses!!
        }
        _sFParentheses = sfIcon(
            name = "Monochrome.SFParentheses",
            viewportWidth = 18.6816f,
            viewportHeight = 27.8223f
        ) {
            addSfPath("M5.12695 27.8125C5.51758 27.8125 5.83008 27.5293 5.83008 27.1777C5.83008 26.9531 5.74219 26.7383 5.56641 26.4551C3.31055 22.8223 2.08008 18.2031 2.08008 13.9062C2.08008 9.59961 3.31055 4.98047 5.56641 1.35742C5.74219 1.07422 5.83008 0.859375 5.83008 0.625C5.83008 0.273438 5.51758 0 5.12695 0C4.7168 0 4.45312 0.234375 4.10156 0.751953C1.38672 4.58008 0 9.32617 0 13.9062C0 18.4863 1.38672 23.2227 4.10156 27.0508C4.45312 27.5684 4.7168 27.8125 5.12695 27.8125ZM13.1934 27.8125C13.5938 27.8125 13.8672 27.5684 14.2188 27.0508C16.9238 23.2227 18.3203 18.4863 18.3203 13.9062C18.3203 9.32617 16.9238 4.58008 14.2188 0.751953C13.8672 0.234375 13.5938 0 13.1934 0C12.8027 0 12.4902 0.273438 12.4902 0.625C12.4902 0.859375 12.5781 1.07422 12.7539 1.35742C15.0098 4.98047 16.2402 9.59961 16.2402 13.9062C16.2402 18.2031 15.0098 22.8223 12.7539 26.4551C12.5781 26.7383 12.4902 26.9531 12.4902 27.1777C12.4902 27.5293 12.8027 27.8125 13.1934 27.8125Z", fillAlpha = 0.85f)
        }
        return _sFParentheses!!
    }

private var _sFParentheses: ImageVector? = null
