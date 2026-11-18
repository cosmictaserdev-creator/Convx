package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFExclamationmarkCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFExclamationmarkCircleFill: ImageVector
    get() {
        if (_sFExclamationmarkCircleFill != null) {
            return _sFExclamationmarkCircleFill!!
        }
        _sFExclamationmarkCircleFill = sfIcon(
            name = "Monochrome.SFExclamationmarkCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM11.4355 17.8906C11.4355 18.5645 12.0312 19.1211 12.7148 19.1211C13.4082 19.1211 13.9941 18.5742 13.9941 17.8906C13.9941 17.1973 13.418 16.6504 12.7148 16.6504C12.0215 16.6504 11.4355 17.207 11.4355 17.8906ZM11.6797 7.25586L11.8262 14.1309C11.8359 14.7168 12.1582 15.0488 12.7148 15.0488C13.2617 15.0488 13.5742 14.7266 13.584 14.1309L13.75 7.26562C13.7598 6.67969 13.3105 6.25 12.7051 6.25C12.0898 6.25 11.6699 6.66992 11.6797 7.25586Z", fillAlpha = 0.85f)
        }
        return _sFExclamationmarkCircleFill!!
    }

private var _sFExclamationmarkCircleFill: ImageVector? = null
