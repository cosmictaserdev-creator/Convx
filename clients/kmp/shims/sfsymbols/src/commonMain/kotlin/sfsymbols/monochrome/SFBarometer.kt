package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBarometer (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFBarometer: ImageVector
    get() {
        if (_sFBarometer != null) {
            return _sFBarometer!!
        }
        _sFBarometer = sfIcon(
            name = "Monochrome.SFBarometer",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.7148 15.3809C14.1895 15.3809 15.3809 14.1895 15.3809 12.7246C15.3809 11.25 14.1895 10.0586 12.7148 10.0586C11.25 10.0586 10.0586 11.25 10.0586 12.7246C10.0586 14.1895 11.25 15.3809 12.7148 15.3809ZM12.7148 13.9941C12.0117 13.9941 11.4453 13.4277 11.4453 12.7246C11.4453 12.0117 12.0117 11.4453 12.7148 11.4453C13.4277 11.4453 13.9941 12.0117 13.9941 12.7246C13.9941 13.4277 13.4277 13.9941 12.7148 13.9941ZM13.457 10.791L14.6875 12.002L19.2773 7.41211C19.6094 7.08008 19.6094 6.52344 19.2773 6.19141C18.9453 5.85938 18.3887 5.85938 18.0566 6.19141ZM12.0312 14.6582L10.8105 13.4375L9.0918 15.1562C8.75 15.498 8.75 16.0449 9.0918 16.377C9.42383 16.7188 9.9707 16.7188 10.3125 16.377Z", fillAlpha = 0.85f)
        }
        return _sFBarometer!!
    }

private var _sFBarometer: ImageVector? = null
