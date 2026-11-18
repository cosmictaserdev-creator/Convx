package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBellBadgeFill (dualtone)
 * Viewport: 23.916 x 29.4238
 */
public val SfSymbols.Dualtone.SFBellBadgeFill: ImageVector
    get() {
        if (_sFBellBadgeFill != null) {
            return _sFBellBadgeFill!!
        }
        _sFBellBadgeFill = sfIcon(
            name = "Dualtone.SFBellBadgeFill",
            viewportWidth = 23.916f,
            viewportHeight = 29.4238f
        ) {
            addSfPath("M11.7773 27.7832C9.69727 27.7832 8.20312 26.2793 8.00781 24.5605L15.5469 24.5605C15.3418 26.2793 13.8574 27.7832 11.7773 27.7832ZM13.2979 1.98615C12.3939 3.00541 11.8457 4.34378 11.8457 5.80078C11.8457 8.97461 14.4629 11.5918 17.6465 11.5918C18.4012 11.5918 19.1253 11.4429 19.7888 11.1693C19.8849 11.8292 19.9431 12.5323 19.9707 13.2715C20.1367 15.0586 20.2832 17.1484 21.2402 18.1738C22.3047 19.2871 23.5547 20.4004 23.5547 21.6699C23.5547 22.4707 22.9395 23.0078 21.9238 23.0078L1.62109 23.0078C0.615234 23.0078 0 22.4707 0 21.6699C0 20.4004 1.24023 19.2871 2.31445 18.1738C3.27148 17.1484 3.41797 15.0586 3.58398 13.2715C3.75 8.81836 5.0293 5.67383 8.28125 4.57031C8.71094 2.90039 9.99023 1.64062 11.7773 1.64062C12.3377 1.64062 12.8481 1.76447 13.2979 1.98615Z", fillAlpha = 0.425f)
            addSfPath("M17.6465 10.0488C19.9609 10.0488 21.8848 8.13477 21.8848 5.81055C21.8848 3.47656 19.9609 1.57227 17.6465 1.57227C15.3125 1.57227 13.4082 3.47656 13.4082 5.81055C13.4082 8.13477 15.3125 10.0488 17.6465 10.0488Z", fillAlpha = 0.85f)
        }
        return _sFBellBadgeFill!!
    }

private var _sFBellBadgeFill: ImageVector? = null
