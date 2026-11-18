package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBookmarkCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFBookmarkCircleFill: ImageVector
    get() {
        if (_sFBookmarkCircleFill != null) {
            return _sFBookmarkCircleFill!!
        }
        _sFBookmarkCircleFill = sfIcon(
            name = "Dualtone.SFBookmarkCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M8.86719 19.5605C8.4668 19.5605 8.21289 19.2773 8.21289 18.8086L8.21289 7.88086C8.21289 6.65039 8.84766 6.00586 10.0684 6.00586L15.3613 6.00586C16.582 6.00586 17.2168 6.65039 17.2168 7.88086L17.2168 18.8086C17.2168 19.2773 16.9629 19.5605 16.5723 19.5605C16.2695 19.5605 16.0938 19.3945 15.5957 18.8965L12.7734 16.0938C12.7441 16.0547 12.6953 16.0547 12.6562 16.0938L9.84375 18.8965C9.33594 19.3945 9.16016 19.5605 8.86719 19.5605Z", fillAlpha = 0.85f)
        }
        return _sFBookmarkCircleFill!!
    }

private var _sFBookmarkCircleFill: ImageVector? = null
