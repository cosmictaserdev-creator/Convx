package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMugFill (dualtone)
 * Viewport: 23.6328 x 26.0645
 */
public val SfSymbols.Dualtone.SFMugFill: ImageVector
    get() {
        if (_sFMugFill != null) {
            return _sFMugFill!!
        }
        _sFMugFill = sfIcon(
            name = "Dualtone.SFMugFill",
            viewportWidth = 23.6328f,
            viewportHeight = 26.0645f
        ) {
            addSfPath("M8.36914 26.0547L10.6738 26.0547C15.8203 26.0547 19.0527 23.3398 19.0527 19.0039L19.0527 3.23242C19.0527 1.13281 14.9805 0 9.52148 0C4.07227 0 0 1.13281 0 3.23242L0 19.0039C0 23.3398 3.22266 26.0547 8.36914 26.0547ZM9.52148 4.13086C5.12695 4.13086 2.03125 3.58398 2.03125 2.83203C2.03125 2.08008 5.12695 1.5332 9.52148 1.5332C13.916 1.5332 17.0215 2.08008 17.0215 2.83203C17.0215 3.58398 13.916 4.13086 9.52148 4.13086ZM17.9395 18.7109L19.8633 18.7109C21.9531 18.7109 23.2715 17.334 23.2715 15.2148L23.2715 10C23.2715 7.87109 21.9531 6.49414 19.8633 6.49414L17.9395 6.49414L17.9395 8.125L19.8633 8.125C21.1133 8.125 21.6406 8.68164 21.6406 10L21.6406 15.2148C21.6406 16.5234 21.1133 17.0801 19.8633 17.0801L17.9395 17.0801Z", fillAlpha = 0.85f)
        }
        return _sFMugFill!!
    }

private var _sFMugFill: ImageVector? = null
