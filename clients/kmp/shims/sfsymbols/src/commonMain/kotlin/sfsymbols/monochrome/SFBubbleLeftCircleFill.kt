package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBubbleLeftCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFBubbleLeftCircleFill: ImageVector
    get() {
        if (_sFBubbleLeftCircleFill != null) {
            return _sFBubbleLeftCircleFill!!
        }
        _sFBubbleLeftCircleFill = sfIcon(
            name = "Monochrome.SFBubbleLeftCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM8.4668 6.97266C6.67969 6.97266 5.6543 8.00781 5.6543 9.77539L5.6543 14.8926C5.6543 16.6895 6.67969 17.7246 8.4668 17.7246L8.85742 17.7246L8.85742 19.5898C8.85742 19.9414 9.04297 20.166 9.35547 20.166C9.58008 20.166 9.73633 20.0684 9.99023 19.8242L12.334 17.7246L16.9629 17.7246C18.7598 17.7246 19.7754 16.6797 19.7754 14.8926L19.7754 9.77539C19.7754 8.00781 18.7598 6.97266 16.9629 6.97266Z", fillAlpha = 0.85f)
        }
        return _sFBubbleLeftCircleFill!!
    }

private var _sFBubbleLeftCircleFill: ImageVector? = null
