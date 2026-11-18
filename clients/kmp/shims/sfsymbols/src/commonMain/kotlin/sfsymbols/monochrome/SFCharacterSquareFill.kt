package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCharacterSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFCharacterSquareFill: ImageVector
    get() {
        if (_sFCharacterSquareFill != null) {
            return _sFCharacterSquareFill!!
        }
        _sFCharacterSquareFill = sfIcon(
            name = "Monochrome.SFCharacterSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM10.2344 6.18164L6.52344 16.123C6.43555 16.3672 6.40625 16.5234 6.40625 16.6895C6.40625 17.1484 6.72852 17.4609 7.24609 17.4609C7.64648 17.4609 7.92969 17.2754 8.10547 16.748L9.12109 13.8477L13.8281 13.8477L14.8438 16.748C15.0293 17.2754 15.3027 17.4609 15.7227 17.4609C16.2109 17.4609 16.543 17.1484 16.543 16.6992C16.543 16.5234 16.5234 16.3672 16.4258 16.123L12.7148 6.18164C12.5 5.5957 12.0703 5.30273 11.4648 5.30273C10.8691 5.30273 10.459 5.5957 10.2344 6.18164ZM13.3691 12.4805L9.58008 12.4805L11.416 7.27539L11.5332 7.27539Z", fillAlpha = 0.85f)
        }
        return _sFCharacterSquareFill!!
    }

private var _sFCharacterSquareFill: ImageVector? = null
