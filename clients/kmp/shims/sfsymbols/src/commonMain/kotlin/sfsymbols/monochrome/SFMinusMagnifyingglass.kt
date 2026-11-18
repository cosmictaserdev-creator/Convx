package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMinusMagnifyingglass (monochrome)
 * Viewport: 24.7656 x 24.6387
 */
public val SfSymbols.Monochrome.SFMinusMagnifyingglass: ImageVector
    get() {
        if (_sFMinusMagnifyingglass != null) {
            return _sFMinusMagnifyingglass!!
        }
        _sFMinusMagnifyingglass = sfIcon(
            name = "Monochrome.SFMinusMagnifyingglass",
            viewportWidth = 24.7656f,
            viewportHeight = 24.6387f
        ) {
            addSfPath("M0 9.88281C0 15.3223 4.43359 19.7559 9.88281 19.7559C12.0801 19.7559 14.1016 19.0332 15.7422 17.8125L22.1777 24.2578C22.4219 24.5117 22.7637 24.6387 23.1152 24.6387C23.8867 24.6387 24.4043 24.0527 24.4043 23.3203C24.4043 22.959 24.2773 22.6465 24.043 22.4023L17.6367 15.9668C18.9648 14.2969 19.7656 12.1777 19.7656 9.88281C19.7656 4.43359 15.332 0 9.88281 0C4.43359 0 0 4.43359 0 9.88281ZM1.82617 9.88281C1.82617 5.43945 5.43945 1.82617 9.88281 1.82617C14.3262 1.82617 17.9297 5.43945 17.9297 9.88281C17.9297 14.3164 14.3262 17.9297 9.88281 17.9297C5.43945 17.9297 1.82617 14.3164 1.82617 9.88281Z", fillAlpha = 0.85f)
            addSfPath("M6.5918 10.6738L13.1543 10.6738C13.5938 10.6738 13.9551 10.3125 13.9551 9.88281C13.9551 9.44336 13.5938 9.08203 13.1543 9.08203L6.5918 9.08203C6.16211 9.08203 5.80078 9.44336 5.80078 9.88281C5.80078 10.3125 6.16211 10.6738 6.5918 10.6738Z", fillAlpha = 0.85f)
        }
        return _sFMinusMagnifyingglass!!
    }

private var _sFMinusMagnifyingglass: ImageVector? = null
