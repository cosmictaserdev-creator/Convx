package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCharacterSutton (dualtone)
 * Viewport: 18.1152 x 25.4395
 */
public val SfSymbols.Dualtone.SFCharacterSutton: ImageVector
    get() {
        if (_sFCharacterSutton != null) {
            return _sFCharacterSutton!!
        }
        _sFCharacterSutton = sfIcon(
            name = "Dualtone.SFCharacterSutton",
            viewportWidth = 18.1152f,
            viewportHeight = 25.4395f
        ) {
            addSfPath("M8.22266 25.4004C12.8125 25.4004 16.4355 21.7773 16.4355 17.1875C16.4355 12.5684 12.8125 8.94531 8.20312 8.94531C3.60352 8.94531 0 12.5684 0 17.1875C0 21.7773 3.62305 25.4004 8.22266 25.4004ZM8.22266 23.5938C4.59961 23.5938 1.80664 20.8008 1.80664 17.1875C1.80664 13.5547 4.58984 10.7617 8.20312 10.7617C11.8262 10.7617 14.6289 13.5547 14.6289 17.1875C14.6289 20.8008 11.8359 23.5938 8.22266 23.5938ZM14.6289 17.1875L16.4355 17.1875L16.4355 3.25195L14.6289 3.25195ZM15.5273 4.45312C16.7676 4.45312 17.7539 3.45703 17.7539 2.22656C17.7539 0.996094 16.7676 0 15.5273 0C14.2969 0 13.3008 0.996094 13.3008 2.22656C13.3008 3.45703 14.2969 4.45312 15.5273 4.45312Z", fillAlpha = 0.85f)
        }
        return _sFCharacterSutton!!
    }

private var _sFCharacterSutton: ImageVector? = null
