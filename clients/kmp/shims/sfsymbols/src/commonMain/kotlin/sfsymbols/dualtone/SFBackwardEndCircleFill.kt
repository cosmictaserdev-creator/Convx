package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBackwardEndCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFBackwardEndCircleFill: ImageVector
    get() {
        if (_sFBackwardEndCircleFill != null) {
            return _sFBackwardEndCircleFill!!
        }
        _sFBackwardEndCircleFill = sfIcon(
            name = "Dualtone.SFBackwardEndCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M7.12891 16.9141L7.12891 8.53516C7.12891 8.02734 7.40234 7.7832 7.89062 7.7832L9.27734 7.7832C9.77539 7.7832 10.0391 8.01758 10.0391 8.53516L10.0391 12.373C10.1172 12.207 10.2637 12.0508 10.4883 11.9238L16.8066 8.21289C17.0215 8.07617 17.2363 8.00781 17.4609 8.00781C17.9102 8.00781 18.2812 8.33008 18.2812 8.96484L18.2812 16.5234C18.2812 17.168 17.9102 17.4902 17.4609 17.4902C17.2363 17.4902 17.0215 17.4121 16.8066 17.2852L10.4883 13.5645C10.2637 13.4375 10.1172 13.291 10.0391 13.1152L10.0391 16.9141C10.0391 17.4219 9.77539 17.6758 9.27734 17.6758L7.89062 17.6758C7.40234 17.6758 7.12891 17.4219 7.12891 16.9141Z", fillAlpha = 0.85f)
        }
        return _sFBackwardEndCircleFill!!
    }

private var _sFBackwardEndCircleFill: ImageVector? = null
