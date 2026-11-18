package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWindowCasementClosed (monochrome)
 * Viewport: 25.2832 x 26.4551
 */
public val SfSymbols.Monochrome.SFWindowCasementClosed: ImageVector
    get() {
        if (_sFWindowCasementClosed != null) {
            return _sFWindowCasementClosed!!
        }
        _sFWindowCasementClosed = sfIcon(
            name = "Monochrome.SFWindowCasementClosed",
            viewportWidth = 25.2832f,
            viewportHeight = 26.4551f
        ) {
            addSfPath("M0 25.5078C0 26.0156 0.419922 26.4355 0.927734 26.4355L23.9941 26.4355C24.502 26.4355 24.9219 26.0156 24.9219 25.5078C24.9219 25 24.502 24.5801 23.9941 24.5801L23.291 24.5801L23.291 1.66016C23.291 0.673828 22.6074 0 21.5625 0L3.29102 0C2.25586 0 1.57227 0.673828 1.57227 1.66016L1.57227 24.5801L0.927734 24.5801C0.419922 24.5801 0 25 0 25.5078ZM3.31055 24.5801L3.31055 18.1152L11.709 18.1152L11.709 24.5801ZM3.31055 16.3867L3.31055 9.93164L11.709 9.93164L11.709 16.3867ZM3.31055 8.20312L3.31055 2.08008C3.31055 1.875 3.4375 1.73828 3.62305 1.73828L11.709 1.73828L11.709 8.20312ZM13.1445 24.5801L13.1445 18.1152L21.5527 18.1152L21.5527 24.5801ZM13.1445 16.3867L13.1445 9.93164L21.5527 9.93164L21.5527 16.3867ZM13.1445 8.20312L13.1445 1.73828L21.2402 1.73828C21.4258 1.73828 21.5527 1.875 21.5527 2.08008L21.5527 8.20312Z", fillAlpha = 0.85f)
        }
        return _sFWindowCasementClosed!!
    }

private var _sFWindowCasementClosed: ImageVector? = null
