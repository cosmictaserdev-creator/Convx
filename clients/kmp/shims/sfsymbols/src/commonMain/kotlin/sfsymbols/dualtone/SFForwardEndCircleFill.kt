package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFForwardEndCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFForwardEndCircleFill: ImageVector
    get() {
        if (_sFForwardEndCircleFill != null) {
            return _sFForwardEndCircleFill!!
        }
        _sFForwardEndCircleFill = sfIcon(
            name = "Dualtone.SFForwardEndCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M18.2812 16.9141C18.2812 17.4219 18.0176 17.6758 17.5293 17.6758L16.1328 17.6758C15.6348 17.6758 15.3809 17.4219 15.3809 16.9141L15.3809 13.1152C15.293 13.291 15.1465 13.4375 14.9219 13.5645L8.61328 17.2852C8.39844 17.4121 8.17383 17.4902 7.95898 17.4902C7.50977 17.4902 7.12891 17.168 7.12891 16.5234L7.12891 8.96484C7.12891 8.33008 7.50977 8.00781 7.95898 8.00781C8.17383 8.00781 8.39844 8.07617 8.61328 8.21289L14.9219 11.9238C15.1465 12.0508 15.293 12.207 15.3809 12.373L15.3809 8.53516C15.3809 8.01758 15.6348 7.7832 16.1328 7.7832L17.5293 7.7832C18.0176 7.7832 18.2812 8.02734 18.2812 8.53516Z", fillAlpha = 0.85f)
        }
        return _sFForwardEndCircleFill!!
    }

private var _sFForwardEndCircleFill: ImageVector? = null
