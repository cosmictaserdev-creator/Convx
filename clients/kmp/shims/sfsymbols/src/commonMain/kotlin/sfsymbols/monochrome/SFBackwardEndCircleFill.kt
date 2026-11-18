package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBackwardEndCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFBackwardEndCircleFill: ImageVector
    get() {
        if (_sFBackwardEndCircleFill != null) {
            return _sFBackwardEndCircleFill!!
        }
        _sFBackwardEndCircleFill = sfIcon(
            name = "Monochrome.SFBackwardEndCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM7.89062 7.7832C7.40234 7.7832 7.12891 8.02734 7.12891 8.53516L7.12891 16.9141C7.12891 17.4219 7.40234 17.6758 7.89062 17.6758L9.27734 17.6758C9.77539 17.6758 10.0391 17.4219 10.0391 16.9141L10.0391 13.1152C10.1172 13.291 10.2637 13.4375 10.4883 13.5645L16.8066 17.2852C17.0215 17.4121 17.2363 17.4902 17.4609 17.4902C17.9102 17.4902 18.2812 17.168 18.2812 16.5234L18.2812 8.96484C18.2812 8.33008 17.9102 8.00781 17.4609 8.00781C17.2363 8.00781 17.0215 8.07617 16.8066 8.21289L10.4883 11.9238C10.2637 12.0508 10.1172 12.207 10.0391 12.373L10.0391 8.53516C10.0391 8.01758 9.77539 7.7832 9.27734 7.7832Z", fillAlpha = 0.85f)
        }
        return _sFBackwardEndCircleFill!!
    }

private var _sFBackwardEndCircleFill: ImageVector? = null
