package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRecordingtape (dualtone)
 * Viewport: 37.5781 x 16.2793
 */
public val SfSymbols.Dualtone.SFRecordingtape: ImageVector
    get() {
        if (_sFRecordingtape != null) {
            return _sFRecordingtape!!
        }
        _sFRecordingtape = sfIcon(
            name = "Dualtone.SFRecordingtape",
            viewportWidth = 37.5781f,
            viewportHeight = 16.2793f
        ) {
            addSfPath("M0 8.13477C0 12.6367 3.64258 16.2695 8.13477 16.2695L29.082 16.2695C33.584 16.2695 37.2168 12.627 37.2168 8.13477C37.2168 3.63281 33.5742 0 29.082 0C24.5801 0 20.9473 3.63281 20.9473 8.13477C20.9473 10.7129 22.1973 13.0273 24.1797 14.4336L13.0371 14.4336C15.0195 13.0273 16.2695 10.7129 16.2695 8.13477C16.2695 3.63281 12.627 0 8.13477 0C3.63281 0 0 3.63281 0 8.13477ZM1.82617 8.125C1.82617 4.63867 4.6582 1.81641 8.14453 1.81641C11.6211 1.81641 14.4434 4.63867 14.4434 8.125C14.4434 11.6113 11.6211 14.4336 8.14453 14.4336C4.6582 14.4336 1.82617 11.6113 1.82617 8.125ZM22.7734 8.125C22.7734 4.63867 25.5957 1.81641 29.0723 1.81641C32.5586 1.81641 35.3906 4.63867 35.3906 8.125C35.3906 11.6113 32.5586 14.4336 29.0723 14.4336C25.5957 14.4336 22.7734 11.6113 22.7734 8.125Z", fillAlpha = 0.85f)
        }
        return _sFRecordingtape!!
    }

private var _sFRecordingtape: ImageVector? = null
