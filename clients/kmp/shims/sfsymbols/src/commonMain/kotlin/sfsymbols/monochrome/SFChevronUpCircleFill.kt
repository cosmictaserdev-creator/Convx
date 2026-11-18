package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChevronUpCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFChevronUpCircleFill: ImageVector
    get() {
        if (_sFChevronUpCircleFill != null) {
            return _sFChevronUpCircleFill!!
        }
        _sFChevronUpCircleFill = sfIcon(
            name = "Monochrome.SFChevronUpCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM11.6309 9.00391L6.39648 14.5117C6.04492 14.8633 6.04492 15.3906 6.35742 15.6934C6.69922 16.0449 7.23633 16.0449 7.55859 15.7031L12.7246 10.2832L17.8906 15.7031C18.2031 16.0449 18.75 16.0449 19.0918 15.6934C19.4043 15.3906 19.3945 14.8633 19.0527 14.5117L13.8184 9.00391C13.1152 8.27148 12.3242 8.27148 11.6309 9.00391Z", fillAlpha = 0.85f)
        }
        return _sFChevronUpCircleFill!!
    }

private var _sFChevronUpCircleFill: ImageVector? = null
