package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFShippingboxCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFShippingboxCircleFill: ImageVector
    get() {
        if (_sFShippingboxCircleFill != null) {
            return _sFShippingboxCircleFill!!
        }
        _sFShippingboxCircleFill = sfIcon(
            name = "Dualtone.SFShippingboxCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M13.1348 20.0781L13.1348 13.1348L19.541 9.52148C19.5801 9.6582 19.5898 9.80469 19.5898 10L19.5898 15.4004C19.5898 16.2695 19.2773 16.6113 18.7012 16.9531L13.2715 20.0098C13.2422 20.0391 13.1934 20.0684 13.1348 20.0781ZM12.2949 20.0781C12.2363 20.0684 12.1973 20.0391 12.1484 20.0098L6.73828 16.9531C6.14258 16.6113 5.84961 16.2695 5.84961 15.4004L5.84961 10C5.84961 9.80469 5.84961 9.6582 5.88867 9.52148L12.2949 13.1348ZM12.7148 12.4023L6.2793 8.76953C6.35742 8.68164 6.47461 8.60352 6.63086 8.52539L9.18945 7.08008L15.6641 10.7422ZM16.5332 10.2539L10.0293 6.5918L11.6016 5.70312C11.9824 5.48828 12.3633 5.37109 12.7148 5.37109C13.0762 5.37109 13.4473 5.48828 13.8281 5.70312L18.7988 8.52539C18.9453 8.60352 19.0723 8.68164 19.1504 8.76953Z", fillAlpha = 0.85f)
        }
        return _sFShippingboxCircleFill!!
    }

private var _sFShippingboxCircleFill: ImageVector? = null
