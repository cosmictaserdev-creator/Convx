package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusMagnifyingglass (dualtone)
 * Viewport: 24.7656 x 24.6387
 */
public val SfSymbols.Dualtone.SFPlusMagnifyingglass: ImageVector
    get() {
        if (_sFPlusMagnifyingglass != null) {
            return _sFPlusMagnifyingglass!!
        }
        _sFPlusMagnifyingglass = sfIcon(
            name = "Dualtone.SFPlusMagnifyingglass",
            viewportWidth = 24.7656f,
            viewportHeight = 24.6387f
        ) {
            addSfPath("M0 9.88281C0 15.3223 4.43359 19.7559 9.88281 19.7559C12.0801 19.7559 14.1016 19.0332 15.7422 17.8125L22.1777 24.2578C22.4219 24.5117 22.7637 24.6387 23.1152 24.6387C23.8867 24.6387 24.4043 24.0527 24.4043 23.3203C24.4043 22.959 24.2773 22.6465 24.043 22.4023L17.6367 15.9668C18.9648 14.2969 19.7656 12.1777 19.7656 9.88281C19.7656 4.43359 15.332 0 9.88281 0C4.43359 0 0 4.43359 0 9.88281ZM1.82617 9.88281C1.82617 5.43945 5.43945 1.82617 9.88281 1.82617C14.3262 1.82617 17.9297 5.43945 17.9297 9.88281C17.9297 14.3164 14.3262 17.9297 9.88281 17.9297C5.43945 17.9297 1.82617 14.3164 1.82617 9.88281Z", fillAlpha = 0.425f)
            addSfPath("M6.60156 10.6738L13.1543 10.6738C13.5938 10.6738 13.9453 10.3223 13.9453 9.88281C13.9453 9.43359 13.5938 9.08203 13.1543 9.08203L6.60156 9.08203C6.15234 9.08203 5.80078 9.43359 5.80078 9.88281C5.80078 10.3223 6.15234 10.6738 6.60156 10.6738ZM10.6738 13.1543L10.6738 6.60156C10.6738 6.15234 10.3223 5.80078 9.88281 5.80078C9.43359 5.80078 9.08203 6.15234 9.08203 6.60156L9.08203 13.1543C9.08203 13.6035 9.43359 13.9453 9.88281 13.9453C10.3223 13.9453 10.6738 13.5938 10.6738 13.1543Z", fillAlpha = 0.85f)
        }
        return _sFPlusMagnifyingglass!!
    }

private var _sFPlusMagnifyingglass: ImageVector? = null
