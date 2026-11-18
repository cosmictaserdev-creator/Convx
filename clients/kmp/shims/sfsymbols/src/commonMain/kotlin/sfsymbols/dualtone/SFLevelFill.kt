package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLevelFill (dualtone)
 * Viewport: 30.8203 x 13.252
 */
public val SfSymbols.Dualtone.SFLevelFill: ImageVector
    get() {
        if (_sFLevelFill != null) {
            return _sFLevelFill!!
        }
        _sFLevelFill = sfIcon(
            name = "Dualtone.SFLevelFill",
            viewportWidth = 30.8203f,
            viewportHeight = 13.252f
        ) {
            addSfPath("M3.32031 13.252L5.19531 13.252L5.19531 0L3.30078 0C1.14258 0 0 1.14258 0 3.29102L0.00976562 9.96094C0.00976562 12.1191 1.14258 13.252 3.32031 13.252ZM6.2793 13.252L24.1699 13.252L24.1699 0L22.0312 0C21.7383 3.55469 18.9844 5.86914 15.2441 5.86914C11.5039 5.86914 8.74023 3.55469 8.4375 0L6.2793 0ZM25.2637 13.252L27.1289 13.252C29.3066 13.252 30.4297 12.1191 30.4395 9.96094L30.459 3.29102C30.4688 1.14258 29.3164 0 27.1387 0L25.2637 0ZM15.2441 4.83398C18.3105 4.83398 20.5957 2.92969 20.8398 0L18.8184 0C18.6523 1.875 17.207 3.11523 15.2441 3.11523C13.2617 3.11523 11.8164 1.875 11.6602 0L9.62891 0C9.87305 2.92969 12.1582 4.83398 15.2441 4.83398Z", fillAlpha = 0.85f)
        }
        return _sFLevelFill!!
    }

private var _sFLevelFill: ImageVector? = null
