package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBubbleLeftCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFBubbleLeftCircleFill: ImageVector
    get() {
        if (_sFBubbleLeftCircleFill != null) {
            return _sFBubbleLeftCircleFill!!
        }
        _sFBubbleLeftCircleFill = sfIcon(
            name = "Dualtone.SFBubbleLeftCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M9.35547 20.166C9.04297 20.166 8.85742 19.9414 8.85742 19.5898L8.85742 17.7246L8.4668 17.7246C6.67969 17.7246 5.6543 16.6895 5.6543 14.8926L5.6543 9.77539C5.6543 8.00781 6.67969 6.97266 8.4668 6.97266L16.9629 6.97266C18.7598 6.97266 19.7754 8.00781 19.7754 9.77539L19.7754 14.8926C19.7754 16.6797 18.7598 17.7246 16.9629 17.7246L12.334 17.7246L9.99023 19.8242C9.73633 20.0684 9.58008 20.166 9.35547 20.166Z", fillAlpha = 0.85f)
        }
        return _sFBubbleLeftCircleFill!!
    }

private var _sFBubbleLeftCircleFill: ImageVector? = null
