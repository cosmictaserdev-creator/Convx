package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFForwardEndCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFForwardEndCircleFill: ImageVector
    get() {
        if (_sFForwardEndCircleFill != null) {
            return _sFForwardEndCircleFill!!
        }
        _sFForwardEndCircleFill = sfIcon(
            name = "Monochrome.SFForwardEndCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM16.1328 7.7832C15.6348 7.7832 15.3809 8.01758 15.3809 8.53516L15.3809 12.373C15.293 12.207 15.1465 12.0508 14.9219 11.9238L8.61328 8.21289C8.39844 8.07617 8.17383 8.00781 7.95898 8.00781C7.50977 8.00781 7.12891 8.33008 7.12891 8.96484L7.12891 16.5234C7.12891 17.168 7.50977 17.4902 7.95898 17.4902C8.17383 17.4902 8.39844 17.4121 8.61328 17.2852L14.9219 13.5645C15.1465 13.4375 15.293 13.291 15.3809 13.1152L15.3809 16.9141C15.3809 17.4219 15.6348 17.6758 16.1328 17.6758L17.5293 17.6758C18.0176 17.6758 18.2812 17.4219 18.2812 16.9141L18.2812 8.53516C18.2812 8.02734 18.0176 7.7832 17.5293 7.7832Z", fillAlpha = 0.85f)
        }
        return _sFForwardEndCircleFill!!
    }

private var _sFForwardEndCircleFill: ImageVector? = null
