package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBookClosed (dualtone)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Dualtone.SFBookClosed: ImageVector
    get() {
        if (_sFBookClosed != null) {
            return _sFBookClosed!!
        }
        _sFBookClosed = sfIcon(
            name = "Dualtone.SFBookClosed",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M3.70117 26.9238L20.1074 26.9238C20.5859 26.9238 20.9668 26.543 20.9668 26.0547C20.9668 25.6934 20.7129 25.3613 20.3809 25.2344C18.7207 24.4727 18.3594 22.3828 19.9512 20.7324C20.4395 20.2344 20.9668 19.5703 20.9668 18.3496L20.9668 3.81836C20.9668 1.29883 19.707 0.0195312 17.207 0.0195312L3.75977 0.0195312C1.25977 0.0195312 0 1.28906 0 3.81836L0 23.2324C0 25.6641 1.26953 26.9238 3.70117 26.9238ZM1.72852 20.166L1.72852 3.85742C1.72852 2.49023 2.45117 1.74805 3.84766 1.74805L17.1094 1.74805C18.5156 1.74805 19.2383 2.49023 19.2383 3.85742L19.2383 18.125C19.2383 19.043 18.7305 19.541 17.8027 19.541L3.86719 19.541C2.96875 19.541 2.28516 19.7559 1.72852 20.166ZM3.78906 25.1953C2.46094 25.1953 1.72852 24.4727 1.72852 23.2129C1.72852 22.0215 2.55859 21.2695 3.87695 21.2695L16.9922 21.2695C17.2363 21.2695 17.4414 21.2402 17.6367 21.1914C16.9434 22.5977 17.0898 24.082 17.9492 25.1953ZM3.24219 20.459L4.67773 20.459L4.67773 1.28906L3.24219 1.28906Z", fillAlpha = 0.85f)
        }
        return _sFBookClosed!!
    }

private var _sFBookClosed: ImageVector? = null
