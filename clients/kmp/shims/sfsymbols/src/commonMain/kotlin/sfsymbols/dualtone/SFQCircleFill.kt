package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFQCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFQCircleFill: ImageVector
    get() {
        if (_sFQCircleFill != null) {
            return _sFQCircleFill!!
        }
        _sFQCircleFill = sfIcon(
            name = "Dualtone.SFQCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 18.7402C9.45312 18.7402 7.2168 16.2305 7.2168 12.6172C7.2168 9.01367 9.45312 6.51367 12.7148 6.51367C15.9766 6.51367 18.2129 9.01367 18.2129 12.6172C18.2129 14.8047 17.3926 16.5723 16.0352 17.627L16.8652 18.7598C17.0801 19.0332 17.1387 19.1797 17.1387 19.375C17.1387 19.7656 16.8652 20.0488 16.4648 20.0488C16.1914 20.0488 15.9863 19.9316 15.7617 19.6387L14.8145 18.3496C14.1895 18.6035 13.4766 18.7402 12.7148 18.7402ZM12.7148 17.3145C13.1641 17.3145 13.5547 17.2461 13.9062 17.1191L13.1543 16.0645C12.9883 15.8594 12.9297 15.6836 12.9297 15.498C12.9297 15.127 13.2324 14.8535 13.6035 14.8535C13.8672 14.8535 14.043 14.9414 14.2188 15.1758L15.0781 16.3379C15.9473 15.5371 16.4258 14.2383 16.4258 12.6172C16.4258 9.80469 14.9512 7.94922 12.7148 7.94922C10.4785 7.94922 9.00391 9.80469 9.00391 12.6172C9.00391 15.459 10.4785 17.3145 12.7148 17.3145Z", fillAlpha = 0.85f)
        }
        return _sFQCircleFill!!
    }

private var _sFQCircleFill: ImageVector? = null
