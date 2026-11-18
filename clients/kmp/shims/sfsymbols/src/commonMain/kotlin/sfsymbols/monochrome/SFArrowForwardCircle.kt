package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowForwardCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowForwardCircle: ImageVector
    get() {
        if (_sFArrowForwardCircle != null) {
            return _sFArrowForwardCircle!!
        }
        _sFArrowForwardCircle = sfIcon(
            name = "Monochrome.SFArrowForwardCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M7.27539 13.5645L15.1953 13.5645L17.1875 13.4961C17.6172 13.4766 17.9688 13.1348 17.9688 12.7148C17.9688 12.2852 17.6172 11.9531 17.1875 11.9336L15.1953 11.8652L7.27539 11.8555C6.81641 11.8555 6.43555 12.2461 6.43555 12.7148C6.43555 13.1738 6.81641 13.5645 7.27539 13.5645ZM14.1309 17.7734C14.3652 17.7734 14.5312 17.6855 14.6973 17.5195L18.7598 13.3301C18.9746 13.1152 19.0527 12.9297 19.0527 12.7148C19.0527 12.4902 18.9746 12.3047 18.7598 12.0898L14.6973 7.91016C14.5312 7.74414 14.3652 7.65625 14.1309 7.65625C13.6914 7.65625 13.3691 7.99805 13.3691 8.4375C13.3691 8.65234 13.457 8.87695 13.6328 9.02344L15.8105 11.1035L17.5 12.7148L17.5 12.7148L15.8105 14.3262L13.6328 16.4062C13.457 16.5527 13.3691 16.7871 13.3691 17.002C13.3691 17.4414 13.6914 17.7734 14.1309 17.7734Z", fillAlpha = 0.85f)
        }
        return _sFArrowForwardCircle!!
    }

private var _sFArrowForwardCircle: ImageVector? = null
