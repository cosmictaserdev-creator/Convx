package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEllipsisRectangleFill (monochrome)
 * Viewport: 26.8945 x 17.1191
 */
public val SfSymbols.Monochrome.SFEllipsisRectangleFill: ImageVector
    get() {
        if (_sFEllipsisRectangleFill != null) {
            return _sFEllipsisRectangleFill!!
        }
        _sFEllipsisRectangleFill = sfIcon(
            name = "Monochrome.SFEllipsisRectangleFill",
            viewportWidth = 26.8945f,
            viewportHeight = 17.1191f
        ) {
            addSfPath("M26.5332 3.42773L26.5332 13.6719C26.5332 15.8984 25.293 17.1094 23.0664 17.1094L3.47656 17.1094C1.25 17.1094 0 15.8984 0 13.6719L0 3.42773C0 1.20117 1.25 0 3.47656 0L23.0664 0C25.293 0 26.5332 1.20117 26.5332 3.42773ZM17.4707 8.53516C17.4707 9.53125 18.2715 10.332 19.2676 10.332C20.2441 10.332 21.0645 9.53125 21.0645 8.53516C21.0645 7.54883 20.2441 6.74805 19.2676 6.74805C18.2715 6.74805 17.4707 7.54883 17.4707 8.53516ZM11.4844 8.53516C11.4844 9.53125 12.2949 10.332 13.2812 10.332C14.2773 10.332 15.0781 9.53125 15.0781 8.53516C15.0781 7.54883 14.2773 6.74805 13.2812 6.74805C12.2949 6.74805 11.4844 7.54883 11.4844 8.53516ZM5.50781 8.53516C5.50781 9.53125 6.31836 10.332 7.29492 10.332C8.29102 10.332 9.0918 9.53125 9.0918 8.53516C9.0918 7.54883 8.28125 6.74805 7.29492 6.74805C6.31836 6.74805 5.50781 7.54883 5.50781 8.53516Z", fillAlpha = 0.85f)
        }
        return _sFEllipsisRectangleFill!!
    }

private var _sFEllipsisRectangleFill: ImageVector? = null
