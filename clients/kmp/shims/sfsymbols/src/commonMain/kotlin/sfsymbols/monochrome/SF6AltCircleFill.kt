package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF6AltCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SF6AltCircleFill: ImageVector
    get() {
        if (_sF6AltCircleFill != null) {
            return _sF6AltCircleFill!!
        }
        _sF6AltCircleFill = sfIcon(
            name = "Monochrome.SF6AltCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM12.5781 7.16797C11.7578 8.23242 10.791 9.44336 10.2148 10.2148C8.89648 11.9531 8.33984 13.3301 8.33984 14.707C8.33984 17.1289 10.127 18.8086 12.7441 18.8086C15.3418 18.8086 17.1289 17.1387 17.1289 14.7754C17.1289 12.6465 15.5273 11.084 13.2617 11.084C12.373 11.084 11.4746 11.4062 11.0059 11.8652L10.8789 11.8359C11.0254 11.5918 11.1914 11.3574 11.5918 10.8398C12.168 10.0879 13.1836 8.83789 13.8574 7.96875C14.0234 7.75391 14.1211 7.56836 14.1211 7.34375C14.1211 7.01172 13.8965 6.66992 13.3594 6.66992C12.998 6.66992 12.7734 6.91406 12.5781 7.16797ZM15.5566 14.8535C15.5566 16.2988 14.3652 17.3926 12.7441 17.3926C11.1426 17.3926 9.92188 16.2793 9.92188 14.834C9.92188 13.3984 11.123 12.3535 12.7344 12.3535C14.3652 12.3535 15.5566 13.4082 15.5566 14.8535Z", fillAlpha = 0.85f)
        }
        return _sF6AltCircleFill!!
    }

private var _sF6AltCircleFill: ImageVector? = null
