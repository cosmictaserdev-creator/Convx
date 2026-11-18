package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCharacterSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFCharacterSquareFill: ImageVector
    get() {
        if (_sFCharacterSquareFill != null) {
            return _sFCharacterSquareFill!!
        }
        _sFCharacterSquareFill = sfIcon(
            name = "Dualtone.SFCharacterSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M7.24609 17.4609C6.72852 17.4609 6.40625 17.1484 6.40625 16.6895C6.40625 16.5234 6.43555 16.3672 6.52344 16.123L10.2344 6.18164C10.459 5.5957 10.8691 5.30273 11.4648 5.30273C12.0703 5.30273 12.5 5.5957 12.7148 6.18164L16.4258 16.123C16.5234 16.3672 16.543 16.5234 16.543 16.6992C16.543 17.1484 16.2109 17.4609 15.7227 17.4609C15.3027 17.4609 15.0293 17.2754 14.8438 16.748L13.8281 13.8477L9.12109 13.8477L8.10547 16.748C7.92969 17.2754 7.64648 17.4609 7.24609 17.4609ZM9.58008 12.4805L13.3691 12.4805L11.5332 7.27539L11.416 7.27539Z", fillAlpha = 0.85f)
        }
        return _sFCharacterSquareFill!!
    }

private var _sFCharacterSquareFill: ImageVector? = null
