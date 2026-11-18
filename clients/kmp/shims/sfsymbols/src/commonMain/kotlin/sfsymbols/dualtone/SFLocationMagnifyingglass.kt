package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocationMagnifyingglass (dualtone)
 * Viewport: 24.7656 x 24.6387
 */
public val SfSymbols.Dualtone.SFLocationMagnifyingglass: ImageVector
    get() {
        if (_sFLocationMagnifyingglass != null) {
            return _sFLocationMagnifyingglass!!
        }
        _sFLocationMagnifyingglass = sfIcon(
            name = "Dualtone.SFLocationMagnifyingglass",
            viewportWidth = 24.7656f,
            viewportHeight = 24.6387f
        ) {
            addSfPath("M0 9.88281C0 15.3223 4.43359 19.7559 9.88281 19.7559C12.0801 19.7559 14.1016 19.0332 15.7422 17.8125L22.1777 24.2578C22.4219 24.5117 22.7637 24.6387 23.1152 24.6387C23.8867 24.6387 24.4043 24.0527 24.4043 23.3203C24.4043 22.959 24.2773 22.6465 24.043 22.4023L17.6367 15.9668C18.9648 14.2969 19.7656 12.1777 19.7656 9.88281C19.7656 4.43359 15.332 0 9.88281 0C4.43359 0 0 4.43359 0 9.88281ZM1.82617 9.88281C1.82617 5.43945 5.43945 1.82617 9.88281 1.82617C14.3262 1.82617 17.9297 5.43945 17.9297 9.88281C17.9297 14.3164 14.3262 17.9297 9.88281 17.9297C5.43945 17.9297 1.82617 14.3164 1.82617 9.88281Z", fillAlpha = 0.425f)
            addSfPath("M4.99023 10.5859L8.85742 10.5957C8.99414 10.5957 9.0918 10.6836 9.0918 10.8301L9.0918 14.6582C9.0918 15.3516 9.83398 15.4199 10.0684 14.9121L14.0527 6.43555C14.3457 5.82031 13.8184 5.3125 13.2129 5.60547L4.73633 9.61914C4.21875 9.85352 4.31641 10.5859 4.99023 10.5859Z", fillAlpha = 0.85f)
        }
        return _sFLocationMagnifyingglass!!
    }

private var _sFLocationMagnifyingglass: ImageVector? = null
