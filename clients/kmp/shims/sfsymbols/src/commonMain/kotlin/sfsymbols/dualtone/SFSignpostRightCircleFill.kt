package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSignpostRightCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSignpostRightCircleFill: ImageVector
    get() {
        if (_sFSignpostRightCircleFill != null) {
            return _sFSignpostRightCircleFill!!
        }
        _sFSignpostRightCircleFill = sfIcon(
            name = "Dualtone.SFSignpostRightCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M7.92969 13.9941C7.08008 13.9941 6.60156 13.5156 6.60156 12.6855L6.60156 9.08203C6.60156 8.25195 7.08008 7.79297 7.92969 7.79297L12.0508 7.79297L12.0508 6.25977C12.0508 5.89844 12.334 5.61523 12.6953 5.61523C13.0371 5.61523 13.3203 5.89844 13.3203 6.25977L13.3203 7.79297L16.582 7.79297C17.1387 7.79297 17.4219 7.94922 17.7246 8.26172L19.4727 9.91211C20.1758 10.5566 20.1562 11.2305 19.4824 11.8652L17.7246 13.5156C17.4219 13.8086 17.1387 13.9844 16.582 13.9844L13.3203 13.9844L13.3203 18.5742L15.2148 18.5742C15.5566 18.5742 15.8496 18.8574 15.8496 19.2188C15.8496 19.5605 15.5566 19.8535 15.2148 19.8535L10.1758 19.8535C9.82422 19.8535 9.53125 19.5605 9.53125 19.2188C9.53125 18.8574 9.82422 18.5742 10.1758 18.5742L12.0508 18.5742L12.0508 13.9844Z", fillAlpha = 0.85f)
        }
        return _sFSignpostRightCircleFill!!
    }

private var _sFSignpostRightCircleFill: ImageVector? = null
