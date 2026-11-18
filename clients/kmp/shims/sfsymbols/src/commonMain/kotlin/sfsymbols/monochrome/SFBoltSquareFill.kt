package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFBoltSquareFill: ImageVector
    get() {
        if (_sFBoltSquareFill != null) {
            return _sFBoltSquareFill!!
        }
        _sFBoltSquareFill = sfIcon(
            name = "Monochrome.SFBoltSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM13.1934 2.90039L5.99609 11.9238C5.85938 12.0898 5.79102 12.2656 5.79102 12.4023C5.79102 12.7051 6.02539 12.9199 6.33789 12.9199L10.791 12.9199L8.4082 19.3359C8.10547 20.1074 8.92578 20.5273 9.43359 19.9023L16.6309 10.8691C16.7578 10.7129 16.8262 10.5371 16.8262 10.3906C16.8262 10.0879 16.5918 9.88281 16.2891 9.88281L11.8359 9.88281L14.2188 3.45703C14.5215 2.69531 13.6914 2.27539 13.1934 2.90039Z", fillAlpha = 0.85f)
        }
        return _sFBoltSquareFill!!
    }

private var _sFBoltSquareFill: ImageVector? = null
