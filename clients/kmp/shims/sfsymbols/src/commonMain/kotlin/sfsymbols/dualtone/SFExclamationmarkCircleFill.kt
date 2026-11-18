package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFExclamationmarkCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFExclamationmarkCircleFill: ImageVector
    get() {
        if (_sFExclamationmarkCircleFill != null) {
            return _sFExclamationmarkCircleFill!!
        }
        _sFExclamationmarkCircleFill = sfIcon(
            name = "Dualtone.SFExclamationmarkCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7148 15.0488C12.1582 15.0488 11.8359 14.7168 11.8262 14.1309L11.6797 7.25586C11.6699 6.66992 12.0898 6.25 12.7051 6.25C13.3105 6.25 13.7598 6.67969 13.75 7.26562L13.584 14.1309C13.5742 14.7266 13.2617 15.0488 12.7148 15.0488ZM12.7148 19.1211C12.0312 19.1211 11.4355 18.5645 11.4355 17.8906C11.4355 17.207 12.0215 16.6504 12.7148 16.6504C13.418 16.6504 13.9941 17.1973 13.9941 17.8906C13.9941 18.5742 13.4082 19.1211 12.7148 19.1211Z", fillAlpha = 0.85f)
        }
        return _sFExclamationmarkCircleFill!!
    }

private var _sFExclamationmarkCircleFill: ImageVector? = null
