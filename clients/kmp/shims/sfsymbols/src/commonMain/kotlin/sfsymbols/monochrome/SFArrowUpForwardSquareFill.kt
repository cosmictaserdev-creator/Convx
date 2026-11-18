package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpForwardSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowUpForwardSquareFill: ImageVector
    get() {
        if (_sFArrowUpForwardSquareFill != null) {
            return _sFArrowUpForwardSquareFill!!
        }
        _sFArrowUpForwardSquareFill = sfIcon(
            name = "Monochrome.SFArrowUpForwardSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM9.19922 6.52344C8.67188 6.52344 8.33984 6.8457 8.33984 7.32422C8.33984 7.82227 8.68164 8.1543 9.21875 8.1543L12.0215 8.1543L13.9475 7.95864L12.0605 9.72656L6.80664 14.9707C6.64062 15.1367 6.5332 15.3516 6.5332 15.5762C6.5332 16.084 6.86523 16.416 7.35352 16.416C7.62695 16.416 7.8418 16.3184 7.99805 16.1523L13.2422 10.9082L14.995 9.04589L14.8145 11.1621L14.8145 13.75C14.8145 14.2871 15.1367 14.6191 15.6348 14.6191C16.123 14.6191 16.4453 14.2578 16.4453 13.7305L16.4453 7.43164C16.4453 6.78711 16.0938 6.52344 15.5273 6.52344Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpForwardSquareFill!!
    }

private var _sFArrowUpForwardSquareFill: ImageVector? = null
