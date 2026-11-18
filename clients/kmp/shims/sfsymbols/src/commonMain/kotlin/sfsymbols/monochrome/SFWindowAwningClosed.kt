package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWindowAwningClosed (monochrome)
 * Viewport: 25.2832 x 26.4551
 */
public val SfSymbols.Monochrome.SFWindowAwningClosed: ImageVector
    get() {
        if (_sFWindowAwningClosed != null) {
            return _sFWindowAwningClosed!!
        }
        _sFWindowAwningClosed = sfIcon(
            name = "Monochrome.SFWindowAwningClosed",
            viewportWidth = 25.2832f,
            viewportHeight = 26.4551f
        ) {
            addSfPath("M0 25.5078C0 26.0156 0.419922 26.4355 0.927734 26.4355L23.9941 26.4355C24.502 26.4355 24.9219 26.0156 24.9219 25.5078C24.9219 25 24.502 24.5801 23.9941 24.5801L23.291 24.5801L23.291 1.66016C23.291 0.673828 22.6074 0 21.5625 0L3.29102 0C2.25586 0 1.57227 0.673828 1.57227 1.66016L1.57227 24.5801L0.927734 24.5801C0.419922 24.5801 0 25 0 25.5078ZM3.31055 12.3535L3.31055 2.08008C3.31055 1.875 3.4375 1.73828 3.62305 1.73828L11.709 1.73828L11.709 12.3535ZM13.1445 12.3535L13.1445 1.73828L21.2402 1.73828C21.4258 1.73828 21.5527 1.875 21.5527 2.08008L21.5527 12.3535ZM3.31055 24.5801L3.31055 14.082L11.709 14.082L11.709 24.5801ZM13.1445 24.5801L13.1445 14.082L21.5527 14.082L21.5527 24.5801Z", fillAlpha = 0.85f)
        }
        return _sFWindowAwningClosed!!
    }

private var _sFWindowAwningClosed: ImageVector? = null
