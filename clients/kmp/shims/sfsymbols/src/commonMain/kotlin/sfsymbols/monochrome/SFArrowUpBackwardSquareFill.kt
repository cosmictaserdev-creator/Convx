package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpBackwardSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowUpBackwardSquareFill: ImageVector
    get() {
        if (_sFArrowUpBackwardSquareFill != null) {
            return _sFArrowUpBackwardSquareFill!!
        }
        _sFArrowUpBackwardSquareFill = sfIcon(
            name = "Monochrome.SFArrowUpBackwardSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM7.44141 6.52344C6.875 6.52344 6.52344 6.78711 6.52344 7.43164L6.52344 13.7305C6.52344 14.2578 6.8457 14.6191 7.33398 14.6191C7.83203 14.6191 8.1543 14.2871 8.1543 13.75L8.1543 11.1621L7.9738 9.04589L9.72656 10.9082L14.9707 16.1523C15.127 16.3184 15.3418 16.416 15.6152 16.416C16.1035 16.416 16.4355 16.084 16.4355 15.5762C16.4355 15.3516 16.3281 15.1367 16.1621 14.9707L10.9082 9.72656L9.02129 7.95864L10.9473 8.1543L13.75 8.1543C14.2871 8.1543 14.6289 7.82227 14.6289 7.32422C14.6289 6.8457 14.2969 6.52344 13.7695 6.52344Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpBackwardSquareFill!!
    }

private var _sFArrowUpBackwardSquareFill: ImageVector? = null
