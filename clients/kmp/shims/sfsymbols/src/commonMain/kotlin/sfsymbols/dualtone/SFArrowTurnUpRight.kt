package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowTurnUpRight (dualtone)
 * Viewport: 23.9648 x 19.5801
 */
public val SfSymbols.Dualtone.SFArrowTurnUpRight: ImageVector
    get() {
        if (_sFArrowTurnUpRight != null) {
            return _sFArrowTurnUpRight!!
        }
        _sFArrowTurnUpRight = sfIcon(
            name = "Dualtone.SFArrowTurnUpRight",
            viewportWidth = 23.9648f,
            viewportHeight = 19.5801f
        ) {
            addSfPath("M21.6309 9.47266L16.8359 13.9062L13.457 17.334C13.291 17.5 13.1934 17.7539 13.1934 18.0078C13.1934 18.5449 13.6035 18.9355 14.1309 18.9355C14.3848 18.9355 14.6094 18.8477 14.8242 18.6426L23.2617 10.1953C23.4863 9.98047 23.6035 9.73633 23.6035 9.47266C23.6035 9.20898 23.4863 8.95508 23.2617 8.74023L14.8828 0.351562C14.6191 0.0976562 14.3848 0 14.1309 0C13.6035 0 13.1934 0.400391 13.1934 0.927734C13.1934 1.18164 13.2812 1.43555 13.457 1.60156L16.8359 5.0293ZM21.6211 10.2637C22.0605 10.2441 22.4121 9.90234 22.4121 9.47266C22.4121 9.0332 22.0508 8.69141 21.6113 8.67188L17.793 8.52539L8.26172 8.51562C2.45117 8.51562 0 11.0449 0 16.9727C0 17.7051 0.0195312 18.3496 0.0878906 18.6914C0.185547 19.1309 0.439453 19.502 1.00586 19.502C1.5625 19.502 1.96289 19.1113 1.96289 18.5449C1.95312 18.1934 1.93359 17.6953 1.92383 17.0703C1.92383 12.3535 3.68164 10.4199 8.34961 10.4199L17.793 10.4199Z", fillAlpha = 0.85f)
        }
        return _sFArrowTurnUpRight!!
    }

private var _sFArrowTurnUpRight: ImageVector? = null
