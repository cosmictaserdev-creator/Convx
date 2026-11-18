package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpRightCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowUpRightCircle: ImageVector
    get() {
        if (_sFArrowUpRightCircle != null) {
            return _sFArrowUpRightCircle!!
        }
        _sFArrowUpRightCircle = sfIcon(
            name = "Dualtone.SFArrowUpRightCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M15.4004 9.02344L13.291 10.9961L8.13477 16.1523C7.98828 16.3086 7.88086 16.5137 7.88086 16.7383C7.88086 17.2266 8.19336 17.5391 8.67188 17.5391C8.94531 17.5391 9.14062 17.4414 9.29688 17.2949L14.4434 12.1484L16.416 10.0488C17.207 9.19922 16.2109 8.27148 15.4004 9.02344ZM15.9668 12.4023L15.9668 14.9512C15.9668 15.4688 16.2793 15.791 16.7578 15.791C17.2363 15.791 17.5488 15.4395 17.5488 14.9316L17.5488 8.7793C17.5488 8.14453 17.207 7.89062 16.6602 7.89062L10.4688 7.89062C9.96094 7.89062 9.63867 8.20312 9.63867 8.67188C9.63867 9.15039 9.9707 9.47266 10.4883 9.47266L13.2617 9.47266L16.2402 9.16992Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpRightCircle!!
    }

private var _sFArrowUpRightCircle: ImageVector? = null
