package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFXmarkRectanglePortraitFill (monochrome)
 * Viewport: 21.3281 x 26.9238
 */
public val SfSymbols.Monochrome.SFXmarkRectanglePortraitFill: ImageVector
    get() {
        if (_sFXmarkRectanglePortraitFill != null) {
            return _sFXmarkRectanglePortraitFill!!
        }
        _sFXmarkRectanglePortraitFill = sfIcon(
            name = "Monochrome.SFXmarkRectanglePortraitFill",
            viewportWidth = 21.3281f,
            viewportHeight = 26.9238f
        ) {
            addSfPath("M20.9668 3.80859L20.9668 23.125C20.9668 25.6445 19.707 26.9238 17.207 26.9238L3.75977 26.9238C1.25977 26.9238 0 25.6445 0 23.125L0 3.80859C0 1.28906 1.25977 0.00976562 3.75977 0.00976562L17.207 0.00976562C19.707 0.00976562 20.9668 1.28906 20.9668 3.80859ZM14.3359 8.39844L10.5008 12.2168L6.66992 8.39844C6.48438 8.22266 6.2793 8.13477 6.03516 8.13477C5.52734 8.13477 5.12695 8.52539 5.12695 9.01367C5.12695 9.25781 5.22461 9.48242 5.40039 9.6582L9.22501 13.487L5.40039 17.2949C5.22461 17.4805 5.12695 17.6953 5.12695 17.9395C5.12695 18.4473 5.52734 18.8477 6.03516 18.8477C6.28906 18.8477 6.50391 18.7402 6.68945 18.5742L10.498 14.7614L14.3066 18.5742C14.4824 18.7402 14.6973 18.8477 14.9609 18.8477C15.459 18.8477 15.8594 18.4473 15.8594 17.9395C15.8594 17.6953 15.7715 17.4805 15.5957 17.2949L11.7732 13.4849L15.5957 9.6582C15.7715 9.48242 15.8594 9.25781 15.8594 9.01367C15.8594 8.52539 15.459 8.13477 14.9609 8.13477C14.7168 8.13477 14.502 8.22266 14.3359 8.39844Z", fillAlpha = 0.85f)
        }
        return _sFXmarkRectanglePortraitFill!!
    }

private var _sFXmarkRectanglePortraitFill: ImageVector? = null
