package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSignpostRightCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFSignpostRightCircleFill: ImageVector
    get() {
        if (_sFSignpostRightCircleFill != null) {
            return _sFSignpostRightCircleFill!!
        }
        _sFSignpostRightCircleFill = sfIcon(
            name = "Monochrome.SFSignpostRightCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM12.0508 6.25977L12.0508 7.79297L7.92969 7.79297C7.08008 7.79297 6.60156 8.25195 6.60156 9.08203L6.60156 12.6855C6.60156 13.5156 7.08008 13.9941 7.92969 13.9941L12.0508 13.9844L12.0508 18.5742L10.1758 18.5742C9.82422 18.5742 9.53125 18.8574 9.53125 19.2188C9.53125 19.5605 9.82422 19.8535 10.1758 19.8535L15.2148 19.8535C15.5566 19.8535 15.8496 19.5605 15.8496 19.2188C15.8496 18.8574 15.5566 18.5742 15.2148 18.5742L13.3203 18.5742L13.3203 13.9844L16.582 13.9844C17.1387 13.9844 17.4219 13.8086 17.7246 13.5156L19.4824 11.8652C20.1562 11.2305 20.1758 10.5566 19.4727 9.91211L17.7246 8.26172C17.4219 7.94922 17.1387 7.79297 16.582 7.79297L13.3203 7.79297L13.3203 6.25977C13.3203 5.89844 13.0371 5.61523 12.6953 5.61523C12.334 5.61523 12.0508 5.89844 12.0508 6.25977Z", fillAlpha = 0.85f)
        }
        return _sFSignpostRightCircleFill!!
    }

private var _sFSignpostRightCircleFill: ImageVector? = null
