package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleRightCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowtriangleRightCircleFill: ImageVector
    get() {
        if (_sFArrowtriangleRightCircleFill != null) {
            return _sFArrowtriangleRightCircleFill!!
        }
        _sFArrowtriangleRightCircleFill = sfIcon(
            name = "Dualtone.SFArrowtriangleRightCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M10.3027 17.7734C9.74609 18.1152 9.0918 17.832 9.0918 17.2559L9.0918 8.20312C9.0918 7.62695 9.78516 7.38281 10.3027 7.69531L17.6758 12.0801C18.1738 12.3828 18.1934 13.0957 17.6758 13.3984Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleRightCircleFill!!
    }

private var _sFArrowtriangleRightCircleFill: ImageVector? = null
