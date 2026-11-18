package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEqualCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFEqualCircleFill: ImageVector
    get() {
        if (_sFEqualCircleFill != null) {
            return _sFEqualCircleFill!!
        }
        _sFEqualCircleFill = sfIcon(
            name = "Dualtone.SFEqualCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M7.72461 15.957C7.1582 15.957 6.75781 15.6641 6.75781 15.1074C6.75781 14.541 7.13867 14.248 7.72461 14.248L17.6953 14.248C18.2812 14.248 18.6621 14.541 18.6621 15.1074C18.6621 15.6641 18.2617 15.957 17.6953 15.957ZM7.72461 11.2305C7.1582 11.2305 6.75781 10.9473 6.75781 10.3906C6.75781 9.81445 7.13867 9.52148 7.72461 9.52148L17.6953 9.52148C18.2812 9.52148 18.6621 9.81445 18.6621 10.3906C18.6621 10.9473 18.2617 11.2305 17.6953 11.2305Z", fillAlpha = 0.85f)
        }
        return _sFEqualCircleFill!!
    }

private var _sFEqualCircleFill: ImageVector? = null
