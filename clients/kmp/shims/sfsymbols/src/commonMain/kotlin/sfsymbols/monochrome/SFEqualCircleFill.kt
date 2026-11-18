package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEqualCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFEqualCircleFill: ImageVector
    get() {
        if (_sFEqualCircleFill != null) {
            return _sFEqualCircleFill!!
        }
        _sFEqualCircleFill = sfIcon(
            name = "Monochrome.SFEqualCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM7.72461 14.248C7.13867 14.248 6.75781 14.541 6.75781 15.1074C6.75781 15.6641 7.1582 15.957 7.72461 15.957L17.6953 15.957C18.2617 15.957 18.6621 15.6641 18.6621 15.1074C18.6621 14.541 18.2812 14.248 17.6953 14.248ZM7.72461 9.52148C7.13867 9.52148 6.75781 9.81445 6.75781 10.3906C6.75781 10.9473 7.1582 11.2305 7.72461 11.2305L17.6953 11.2305C18.2617 11.2305 18.6621 10.9473 18.6621 10.3906C18.6621 9.81445 18.2812 9.52148 17.6953 9.52148Z", fillAlpha = 0.85f)
        }
        return _sFEqualCircleFill!!
    }

private var _sFEqualCircleFill: ImageVector? = null
