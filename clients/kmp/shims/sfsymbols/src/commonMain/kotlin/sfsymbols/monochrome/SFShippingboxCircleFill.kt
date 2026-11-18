package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFShippingboxCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFShippingboxCircleFill: ImageVector
    get() {
        if (_sFShippingboxCircleFill != null) {
            return _sFShippingboxCircleFill!!
        }
        _sFShippingboxCircleFill = sfIcon(
            name = "Monochrome.SFShippingboxCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM13.1348 13.1348L13.1348 20.0781C13.1934 20.0684 13.2422 20.0391 13.2715 20.0098L18.7012 16.9531C19.2773 16.6113 19.5898 16.2695 19.5898 15.4004L19.5898 10C19.5898 9.80469 19.5801 9.6582 19.541 9.52148ZM5.84961 10L5.84961 15.4004C5.84961 16.2695 6.14258 16.6113 6.73828 16.9531L12.1484 20.0098C12.1973 20.0391 12.2363 20.0684 12.2949 20.0781L12.2949 13.1348L5.88867 9.52148C5.84961 9.6582 5.84961 9.80469 5.84961 10ZM6.63086 8.52539C6.47461 8.60352 6.35742 8.68164 6.2793 8.76953L12.7148 12.4023L15.6641 10.7422L9.18945 7.08008ZM11.6016 5.70312L10.0293 6.5918L16.5332 10.2539L19.1504 8.76953C19.0723 8.68164 18.9453 8.60352 18.7988 8.52539L13.8281 5.70312C13.4473 5.48828 13.0762 5.37109 12.7148 5.37109C12.3633 5.37109 11.9824 5.48828 11.6016 5.70312Z", fillAlpha = 0.85f)
        }
        return _sFShippingboxCircleFill!!
    }

private var _sFShippingboxCircleFill: ImageVector? = null
