package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBubbleRightCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFBubbleRightCircleFill: ImageVector
    get() {
        if (_sFBubbleRightCircleFill != null) {
            return _sFBubbleRightCircleFill!!
        }
        _sFBubbleRightCircleFill = sfIcon(
            name = "Dualtone.SFBubbleRightCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M16.0742 20.166C15.8496 20.166 15.6934 20.0684 15.4395 19.8242L13.0957 17.7246L8.4668 17.7246C6.67969 17.7246 5.6543 16.6797 5.6543 14.8926L5.6543 9.77539C5.6543 8.00781 6.67969 6.97266 8.4668 6.97266L16.9629 6.97266C18.7598 6.97266 19.7754 8.00781 19.7754 9.77539L19.7754 14.8926C19.7754 16.6895 18.7598 17.7246 16.9629 17.7246L16.582 17.7246L16.582 19.5898C16.582 19.9414 16.3867 20.166 16.0742 20.166Z", fillAlpha = 0.85f)
        }
        return _sFBubbleRightCircleFill!!
    }

private var _sFBubbleRightCircleFill: ImageVector? = null
