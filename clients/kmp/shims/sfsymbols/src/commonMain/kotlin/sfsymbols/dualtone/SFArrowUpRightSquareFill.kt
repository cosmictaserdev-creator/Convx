package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpRightSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFArrowUpRightSquareFill: ImageVector
    get() {
        if (_sFArrowUpRightSquareFill != null) {
            return _sFArrowUpRightSquareFill!!
        }
        _sFArrowUpRightSquareFill = sfIcon(
            name = "Dualtone.SFArrowUpRightSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M14.2285 7.69531L12.0605 9.72656L6.80664 14.9707C6.64062 15.1367 6.5332 15.3516 6.5332 15.5762C6.5332 16.084 6.86523 16.416 7.35352 16.416C7.62695 16.416 7.8418 16.3184 7.99805 16.1523L13.2422 10.9082L15.2734 8.75C16.0938 7.87109 15.0684 6.9043 14.2285 7.69531ZM14.8145 11.1621L14.8145 13.75C14.8145 14.2871 15.1367 14.6191 15.6348 14.6191C16.123 14.6191 16.4453 14.2578 16.4453 13.7305L16.4453 7.43164C16.4453 6.78711 16.0938 6.52344 15.5273 6.52344L9.19922 6.52344C8.67188 6.52344 8.33984 6.8457 8.33984 7.32422C8.33984 7.82227 8.68164 8.1543 9.21875 8.1543L12.0215 8.1543L15.0977 7.8418Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpRightSquareFill!!
    }

private var _sFArrowUpRightSquareFill: ImageVector? = null
