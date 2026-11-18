package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusCapsuleFill (monochrome)
 * Viewport: 32.3828 x 23.8184
 */
public val SfSymbols.Monochrome.SFPlusCapsuleFill: ImageVector
    get() {
        if (_sFPlusCapsuleFill != null) {
            return _sFPlusCapsuleFill!!
        }
        _sFPlusCapsuleFill = sfIcon(
            name = "Monochrome.SFPlusCapsuleFill",
            viewportWidth = 32.3828f,
            viewportHeight = 23.8184f
        ) {
            addSfPath("M32.0215 11.9043C32.0215 19.1211 27.1289 23.8086 19.7168 23.8086L12.3047 23.8086C4.89258 23.8086 0 19.1211 0 11.9043C0 4.67773 4.89258 0 12.3047 0L19.7168 0C27.1289 0 32.0215 4.67773 32.0215 11.9043ZM15.0781 6.92383L15.0781 10.957L11.0449 10.957C10.4883 10.957 10.0977 11.3379 10.0977 11.9043C10.0977 12.4414 10.4883 12.8125 11.0449 12.8125L15.0781 12.8125L15.0781 16.8555C15.0781 17.4023 15.459 17.8027 15.9961 17.8027C16.5527 17.8027 16.9434 17.4121 16.9434 16.8555L16.9434 12.8125L20.9863 12.8125C21.5234 12.8125 21.9238 12.4414 21.9238 11.9043C21.9238 11.3379 21.5332 10.957 20.9863 10.957L16.9434 10.957L16.9434 6.92383C16.9434 6.35742 16.5527 5.9668 15.9961 5.9668C15.459 5.9668 15.0781 6.36719 15.0781 6.92383Z", fillAlpha = 0.85f)
        }
        return _sFPlusCapsuleFill!!
    }

private var _sFPlusCapsuleFill: ImageVector? = null
