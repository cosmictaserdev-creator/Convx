package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEraserLineDashedFill (monochrome)
 * Viewport: 32.5553 x 27.8712
 */
public val SfSymbols.Monochrome.SFEraserLineDashedFill: ImageVector
    get() {
        if (_sFEraserLineDashedFill != null) {
            return _sFEraserLineDashedFill!!
        }
        _sFEraserLineDashedFill = sfIcon(
            name = "Monochrome.SFEraserLineDashedFill",
            viewportWidth = 32.5553f,
            viewportHeight = 27.8712f
        ) {
            addSfPath("M22.0198 24.7022C22.0198 25.1709 21.6194 25.5616 21.1507 25.5616L14.6764 25.5616L16.4063 23.8331L21.1507 23.8331C21.6194 23.8331 22.0198 24.2237 22.0198 24.7022Z", fillAlpha = 0.85f)
            addSfPath("M30.6331 24.7022C30.6331 25.1709 30.2425 25.5616 29.7737 25.5616L24.5882 25.5616C24.1194 25.5616 23.719 25.1709 23.719 24.7022C23.719 24.2237 24.1194 23.8331 24.5882 23.8331L29.7737 23.8331C30.2425 23.8331 30.6331 24.2237 30.6331 24.7022Z", fillAlpha = 0.85f)
            addSfPath("M5.19364 12.1241L15.4866 22.417L25.2229 12.6807C26.2874 11.626 26.2679 10.1221 25.1839 9.04789L18.5628 2.41703C17.4886 1.33305 15.9847 1.32328 14.9202 2.38774ZM2.90849 20.0245L7.57646 24.7022C9.34403 26.4502 11.3948 26.5088 13.0647 24.8389L14.3831 23.5206L4.09013 13.2276L2.762 14.5362C1.09208 16.2061 1.15067 18.2666 2.90849 20.0245Z", fillAlpha = 0.85f)
        }
        return _sFEraserLineDashedFill!!
    }

private var _sFEraserLineDashedFill: ImageVector? = null
