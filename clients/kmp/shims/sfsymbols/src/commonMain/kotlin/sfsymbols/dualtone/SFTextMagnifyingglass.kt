package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextMagnifyingglass (dualtone)
 * Viewport: 24.7656 x 24.6387
 */
public val SfSymbols.Dualtone.SFTextMagnifyingglass: ImageVector
    get() {
        if (_sFTextMagnifyingglass != null) {
            return _sFTextMagnifyingglass!!
        }
        _sFTextMagnifyingglass = sfIcon(
            name = "Dualtone.SFTextMagnifyingglass",
            viewportWidth = 24.7656f,
            viewportHeight = 24.6387f
        ) {
            addSfPath("M0 9.88281C0 15.3223 4.43359 19.7559 9.88281 19.7559C12.0801 19.7559 14.1016 19.0332 15.7422 17.8125L22.1777 24.2578C22.4219 24.5117 22.7637 24.6387 23.1152 24.6387C23.8867 24.6387 24.4043 24.0527 24.4043 23.3203C24.4043 22.959 24.2773 22.6465 24.043 22.4023L17.6367 15.9668C18.9648 14.2969 19.7656 12.1777 19.7656 9.88281C19.7656 4.43359 15.332 0 9.88281 0C4.43359 0 0 4.43359 0 9.88281ZM1.82617 9.88281C1.82617 5.43945 5.43945 1.82617 9.88281 1.82617C14.3262 1.82617 17.9297 5.43945 17.9297 9.88281C17.9297 14.3164 14.3262 17.9297 9.88281 17.9297C5.43945 17.9297 1.82617 14.3164 1.82617 9.88281Z", fillAlpha = 0.425f)
            addSfPath("M5.86914 9.00391L13.9648 9.00391C14.3848 9.00391 14.7168 8.66211 14.7168 8.23242C14.7168 7.8125 14.3848 7.48047 13.9648 7.48047L5.86914 7.48047C5.42969 7.48047 5.10742 7.8125 5.10742 8.23242C5.10742 8.66211 5.42969 9.00391 5.86914 9.00391ZM5.86914 12.2656L11.7285 12.2656C12.1484 12.2656 12.5 11.9336 12.5 11.5039C12.5 11.084 12.1484 10.7422 11.7285 10.7422L5.86914 10.7422C5.42969 10.7422 5.10742 11.084 5.10742 11.5039C5.10742 11.9336 5.42969 12.2656 5.86914 12.2656Z", fillAlpha = 0.85f)
        }
        return _sFTextMagnifyingglass!!
    }

private var _sFTextMagnifyingglass: ImageVector? = null
