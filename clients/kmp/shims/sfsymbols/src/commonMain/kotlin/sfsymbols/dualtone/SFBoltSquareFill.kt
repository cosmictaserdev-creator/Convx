package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFBoltSquareFill: ImageVector
    get() {
        if (_sFBoltSquareFill != null) {
            return _sFBoltSquareFill!!
        }
        _sFBoltSquareFill = sfIcon(
            name = "Dualtone.SFBoltSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M5.79102 12.4023C5.79102 12.2656 5.85938 12.0898 5.99609 11.9238L13.1934 2.90039C13.6914 2.27539 14.5215 2.69531 14.2188 3.45703L11.8359 9.88281L16.2891 9.88281C16.5918 9.88281 16.8262 10.0879 16.8262 10.3906C16.8262 10.5371 16.7578 10.7129 16.6309 10.8691L9.43359 19.9023C8.92578 20.5273 8.10547 20.1074 8.4082 19.3359L10.791 12.9199L6.33789 12.9199C6.02539 12.9199 5.79102 12.7051 5.79102 12.4023Z", fillAlpha = 0.85f)
        }
        return _sFBoltSquareFill!!
    }

private var _sFBoltSquareFill: ImageVector? = null
