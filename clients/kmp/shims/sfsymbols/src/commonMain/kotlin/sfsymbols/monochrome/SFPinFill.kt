package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPinFill (monochrome)
 * Viewport: 19.4434 x 29.9316
 */
public val SfSymbols.Monochrome.SFPinFill: ImageVector
    get() {
        if (_sFPinFill != null) {
            return _sFPinFill!!
        }
        _sFPinFill = sfIcon(
            name = "Monochrome.SFPinFill",
            viewportWidth = 19.4434f,
            viewportHeight = 29.9316f
        ) {
            addSfPath("M0 18.7988C0 19.7656 0.673828 20.4102 1.71875 20.4102L8.56445 20.4102L8.56445 26.7871C8.56445 28.4863 9.26758 29.9316 9.54102 29.9316C9.82422 29.9316 10.5176 28.4863 10.5176 26.7871L10.5176 20.4102L17.373 20.4102C18.418 20.4102 19.082 19.7656 19.082 18.7988C19.082 16.1133 16.9629 13.3691 13.4863 12.1289L13.0762 6.28906C14.7656 5.3125 16.2695 4.14062 16.9336 3.27148C17.2363 2.86133 17.3828 2.4707 17.3828 2.11914C17.3828 1.40625 16.8457 0.878906 16.0156 0.878906L3.07617 0.878906C2.24609 0.878906 1.69922 1.40625 1.69922 2.11914C1.69922 2.4707 1.8457 2.86133 2.1582 3.27148C2.8125 4.14062 4.32617 5.32227 6.00586 6.28906L5.5957 12.1289C2.12891 13.3691 0 16.1133 0 18.7988Z", fillAlpha = 0.85f)
        }
        return _sFPinFill!!
    }

private var _sFPinFill: ImageVector? = null
