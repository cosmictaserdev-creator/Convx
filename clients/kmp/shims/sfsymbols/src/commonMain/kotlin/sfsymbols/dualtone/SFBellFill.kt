package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBellFill (dualtone)
 * Viewport: 23.916 x 26.1426
 */
public val SfSymbols.Dualtone.SFBellFill: ImageVector
    get() {
        if (_sFBellFill != null) {
            return _sFBellFill!!
        }
        _sFBellFill = sfIcon(
            name = "Dualtone.SFBellFill",
            viewportWidth = 23.916f,
            viewportHeight = 26.1426f
        ) {
            addSfPath("M1.62109 21.3672L21.9238 21.3672C22.9395 21.3672 23.5547 20.8301 23.5547 20.0293C23.5547 18.7598 22.3047 17.6465 21.2402 16.5332C20.2832 15.5078 20.1367 13.418 19.9707 11.6309C19.8047 7.17773 18.5254 4.0332 15.2734 2.92969C14.8438 1.25977 13.5645 0 11.7773 0C9.99023 0 8.71094 1.25977 8.28125 2.92969C5.0293 4.0332 3.75 7.17773 3.58398 11.6309C3.41797 13.418 3.27148 15.5078 2.31445 16.5332C1.24023 17.6465 0 18.7598 0 20.0293C0 20.8301 0.615234 21.3672 1.62109 21.3672ZM11.7773 26.1426C13.8574 26.1426 15.3418 24.6387 15.5469 22.9199L8.00781 22.9199C8.20312 24.6387 9.69727 26.1426 11.7773 26.1426Z", fillAlpha = 0.85f)
        }
        return _sFBellFill!!
    }

private var _sFBellFill: ImageVector? = null
