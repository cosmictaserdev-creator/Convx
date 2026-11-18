package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFQCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFQCircleFill: ImageVector
    get() {
        if (_sFQCircleFill != null) {
            return _sFQCircleFill!!
        }
        _sFQCircleFill = sfIcon(
            name = "Monochrome.SFQCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM7.2168 12.6172C7.2168 16.2305 9.45312 18.7402 12.7148 18.7402C13.4766 18.7402 14.1895 18.6035 14.8145 18.3496L15.7617 19.6387C15.9863 19.9316 16.1914 20.0488 16.4648 20.0488C16.8652 20.0488 17.1387 19.7656 17.1387 19.375C17.1387 19.1797 17.0801 19.0332 16.8652 18.7598L16.0352 17.627C17.3926 16.5723 18.2129 14.8047 18.2129 12.6172C18.2129 9.01367 15.9766 6.51367 12.7148 6.51367C9.45312 6.51367 7.2168 9.01367 7.2168 12.6172ZM16.4258 12.6172C16.4258 14.2383 15.9473 15.5371 15.0781 16.3379L14.2188 15.1758C14.043 14.9414 13.8672 14.8535 13.6035 14.8535C13.2324 14.8535 12.9297 15.127 12.9297 15.498C12.9297 15.6836 12.9883 15.8594 13.1543 16.0645L13.9062 17.1191C13.5547 17.2461 13.1641 17.3145 12.7148 17.3145C10.4785 17.3145 9.00391 15.459 9.00391 12.6172C9.00391 9.80469 10.4785 7.94922 12.7148 7.94922C14.9512 7.94922 16.4258 9.80469 16.4258 12.6172Z", fillAlpha = 0.85f)
        }
        return _sFQCircleFill!!
    }

private var _sFQCircleFill: ImageVector? = null
