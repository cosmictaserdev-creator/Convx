package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFUnderlineDouble (dualtone)
 * Viewport: 14.8047 x 32.5488
 */
public val SfSymbols.Dualtone.SFUnderlineDouble: ImageVector
    get() {
        if (_sFUnderlineDouble != null) {
            return _sFUnderlineDouble!!
        }
        _sFUnderlineDouble = sfIcon(
            name = "Dualtone.SFUnderlineDouble",
            viewportWidth = 14.8047f,
            viewportHeight = 32.5488f
        ) {
            addSfPath("M0.664062 32.5488L13.7793 32.5488C14.1602 32.5488 14.4434 32.2754 14.4434 31.9043C14.4434 31.5234 14.1602 31.25 13.7793 31.25L0.664062 31.25C0.292969 31.25 0 31.5234 0 31.9043C0 32.2754 0.292969 32.5488 0.664062 32.5488Z", fillAlpha = 0.85f)
            addSfPath("M0.664062 29.3652L13.7793 29.3652C14.1602 29.3652 14.4434 29.082 14.4434 28.7109C14.4434 28.3398 14.1602 28.0566 13.7793 28.0566L0.664062 28.0566C0.292969 28.0566 0 28.3398 0 28.7109C0 29.082 0.292969 29.3652 0.664062 29.3652Z", fillAlpha = 0.85f)
            addSfPath("M7.2168 25.625C11.6797 25.625 14.4434 22.9395 14.4434 18.9746L14.4434 7.91016C14.4434 7.30469 14.0527 6.92383 13.457 6.92383C12.8516 6.92383 12.4805 7.30469 12.4805 7.91016L12.4805 18.8184C12.4805 21.8164 10.5176 23.8086 7.2168 23.8086C3.93555 23.8086 1.96289 21.8164 1.96289 18.8184L1.96289 7.91016C1.96289 7.30469 1.57227 6.92383 0.976562 6.92383C0.371094 6.92383 0 7.30469 0 7.91016L0 18.9746C0 22.9395 2.76367 25.625 7.2168 25.625Z", fillAlpha = 0.425f)
        }
        return _sFUnderlineDouble!!
    }

private var _sFUnderlineDouble: ImageVector? = null
