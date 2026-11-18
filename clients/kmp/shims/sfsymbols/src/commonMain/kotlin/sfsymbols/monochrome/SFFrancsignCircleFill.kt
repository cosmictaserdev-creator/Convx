package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFrancsignCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFFrancsignCircleFill: ImageVector
    get() {
        if (_sFFrancsignCircleFill != null) {
            return _sFFrancsignCircleFill!!
        }
        _sFFrancsignCircleFill = sfIcon(
            name = "Monochrome.SFFrancsignCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM10.6055 7.08984C10.166 7.08984 9.90234 7.35352 9.90234 7.80273L9.90234 15.7129L8.67188 15.7129C8.44727 15.7129 8.28125 15.8887 8.28125 16.1035C8.28125 16.3379 8.44727 16.4941 8.67188 16.4941L9.90234 16.4941L9.90234 17.959C9.90234 18.3691 10.1855 18.6621 10.5957 18.6621C10.9863 18.6621 11.2793 18.3691 11.2793 17.959L11.2793 16.4941L14.1113 16.4941C14.3359 16.4941 14.502 16.3379 14.502 16.1035C14.502 15.8887 14.3359 15.7129 14.1113 15.7129L11.2793 15.7129L11.2793 13.3105L15.6348 13.3105C15.9863 13.3105 16.25 13.0566 16.25 12.7246C16.25 12.3926 15.9863 12.1387 15.6348 12.1387L11.2793 12.1387L11.2793 8.28125L16.084 8.28125C16.4453 8.28125 16.709 8.02734 16.709 7.69531C16.709 7.34375 16.4453 7.08984 16.084 7.08984Z", fillAlpha = 0.85f)
        }
        return _sFFrancsignCircleFill!!
    }

private var _sFFrancsignCircleFill: ImageVector? = null
