package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTarget (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFTarget: ImageVector
    get() {
        if (_sFTarget != null) {
            return _sFTarget!!
        }
        _sFTarget = sfIcon(
            name = "Monochrome.SFTarget",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.7148 20.7129C17.1289 20.7129 20.7031 17.1387 20.7031 12.7246C20.7031 8.32031 17.1289 4.73633 12.7148 4.73633C8.31055 4.73633 4.72656 8.32031 4.72656 12.7246C4.72656 17.1387 8.31055 20.7129 12.7148 20.7129ZM12.7148 18.9844C9.25781 18.9844 6.46484 16.1914 6.46484 12.7246C6.46484 9.26758 9.25781 6.47461 12.7148 6.47461C16.1816 6.47461 18.9746 9.26758 18.9746 12.7246C18.9746 16.1914 16.1816 18.9844 12.7148 18.9844Z", fillAlpha = 0.85f)
            addSfPath("M12.7148 16.084C14.5801 16.084 16.084 14.5801 16.084 12.7148C16.084 10.8594 14.5801 9.35547 12.7148 9.35547C10.8594 9.35547 9.35547 10.8594 9.35547 12.7148C9.35547 14.5801 10.8594 16.084 12.7148 16.084Z", fillAlpha = 0.85f)
        }
        return _sFTarget!!
    }

private var _sFTarget: ImageVector? = null
