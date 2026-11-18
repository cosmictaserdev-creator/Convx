package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF1Magnifyingglass (monochrome)
 * Viewport: 24.7656 x 24.6387
 */
public val SfSymbols.Monochrome.SF1Magnifyingglass: ImageVector
    get() {
        if (_sF1Magnifyingglass != null) {
            return _sF1Magnifyingglass!!
        }
        _sF1Magnifyingglass = sfIcon(
            name = "Monochrome.SF1Magnifyingglass",
            viewportWidth = 24.7656f,
            viewportHeight = 24.6387f
        ) {
            addSfPath("M0 9.88281C0 15.3223 4.43359 19.7559 9.88281 19.7559C12.0801 19.7559 14.1016 19.0332 15.7422 17.8125L22.1777 24.2578C22.4219 24.5117 22.7637 24.6387 23.1152 24.6387C23.8867 24.6387 24.4043 24.0527 24.4043 23.3203C24.4043 22.959 24.2773 22.6465 24.043 22.4023L17.6367 15.9668C18.9648 14.2969 19.7656 12.1777 19.7656 9.88281C19.7656 4.43359 15.332 0 9.88281 0C4.43359 0 0 4.43359 0 9.88281ZM1.82617 9.88281C1.82617 5.43945 5.43945 1.82617 9.88281 1.82617C14.3262 1.82617 17.9297 5.43945 17.9297 9.88281C17.9297 14.3164 14.3262 17.9297 9.88281 17.9297C5.43945 17.9297 1.82617 14.3164 1.82617 9.88281Z", fillAlpha = 0.85f)
            addSfPath("M10.4297 14.5605C10.8691 14.5605 11.1816 14.3457 11.1816 13.877L11.1816 5.83984C11.1816 5.33203 10.8496 4.98047 10.3125 4.98047C9.93164 4.98047 9.69727 5.07812 9.27734 5.37109L7.44141 6.62109C7.1582 6.81641 7.06055 6.99219 7.06055 7.27539C7.06055 7.59766 7.34375 7.86133 7.64648 7.86133C7.86133 7.86133 7.95898 7.80273 8.13477 7.67578L9.58008 6.67969L9.66797 6.67969L9.66797 13.7695C9.66797 14.2578 9.9707 14.5605 10.4297 14.5605Z", fillAlpha = 0.85f)
        }
        return _sF1Magnifyingglass!!
    }

private var _sF1Magnifyingglass: ImageVector? = null
