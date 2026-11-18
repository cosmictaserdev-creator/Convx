package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEllipsisRectangleFill (dualtone)
 * Viewport: 26.8945 x 17.1191
 */
public val SfSymbols.Dualtone.SFEllipsisRectangleFill: ImageVector
    get() {
        if (_sFEllipsisRectangleFill != null) {
            return _sFEllipsisRectangleFill!!
        }
        _sFEllipsisRectangleFill = sfIcon(
            name = "Dualtone.SFEllipsisRectangleFill",
            viewportWidth = 26.8945f,
            viewportHeight = 17.1191f
        ) {
            addSfPath("M0 13.6719C0 15.8984 1.25 17.1094 3.47656 17.1094L23.0664 17.1094C25.293 17.1094 26.5332 15.8984 26.5332 13.6719L26.5332 3.42773C26.5332 1.20117 25.293 0 23.0664 0L3.47656 0C1.25 0 0 1.20117 0 3.42773Z", fillAlpha = 0.2125f)
            addSfPath("M19.2676 10.332C18.2715 10.332 17.4707 9.53125 17.4707 8.53516C17.4707 7.54883 18.2715 6.74805 19.2676 6.74805C20.2441 6.74805 21.0645 7.54883 21.0645 8.53516C21.0645 9.53125 20.2441 10.332 19.2676 10.332Z", fillAlpha = 0.85f)
            addSfPath("M13.2812 10.332C12.2949 10.332 11.4844 9.53125 11.4844 8.53516C11.4844 7.54883 12.2949 6.74805 13.2812 6.74805C14.2773 6.74805 15.0781 7.54883 15.0781 8.53516C15.0781 9.53125 14.2773 10.332 13.2812 10.332Z", fillAlpha = 0.85f)
            addSfPath("M7.29492 10.332C6.31836 10.332 5.50781 9.53125 5.50781 8.53516C5.50781 7.54883 6.31836 6.74805 7.29492 6.74805C8.28125 6.74805 9.0918 7.54883 9.0918 8.53516C9.0918 9.53125 8.29102 10.332 7.29492 10.332Z", fillAlpha = 0.85f)
        }
        return _sFEllipsisRectangleFill!!
    }

private var _sFEllipsisRectangleFill: ImageVector? = null
