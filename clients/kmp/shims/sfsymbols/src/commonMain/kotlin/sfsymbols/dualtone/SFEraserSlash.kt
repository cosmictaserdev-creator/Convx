package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEraserSlash (dualtone)
 * Viewport: 28.2886 x 27.7759
 */
public val SfSymbols.Dualtone.SFEraserSlash: ImageVector
    get() {
        if (_sFEraserSlash != null) {
            return _sFEraserSlash!!
        }
        _sFEraserSlash = sfIcon(
            name = "Dualtone.SFEraserSlash",
            viewportWidth = 28.2886f,
            viewportHeight = 27.7759f
        ) {
            addSfPath("M8.92457 10.6653L6.41968 13.1702L14.57 21.3205L17.0759 18.8167L18.2976 20.0383L13.3923 24.9475C11.7224 26.6272 9.67164 26.5588 7.90406 24.801L2.93335 19.8401C1.18531 18.0823 1.12671 16.0315 2.79664 14.3518L7.70386 9.44459ZM4.07593 15.5139C3.13843 16.4612 3.18726 17.6331 4.15406 18.6194L9.13453 23.5803C10.1111 24.5667 11.2927 24.6057 12.2205 23.6682L13.4811 22.4086L5.33538 14.2545ZM18.5877 2.24244L25.5115 9.14673C26.5955 10.221 26.6052 11.7444 25.5408 12.7893L21.3983 16.9351L20.1794 15.7157L24.2712 11.6272C24.6326 11.2561 24.6423 10.7385 24.281 10.3674L17.3767 3.47291C17.0154 3.10181 16.488 3.10181 16.1072 3.48267L12.0285 7.56132L10.8081 6.34035L14.9451 2.20337C16.0095 1.13892 17.5134 1.15845 18.5877 2.24244Z", fillAlpha = 0.425f)
            addSfPath("M25.5115 25.3284C25.8337 25.6506 26.3709 25.6506 26.6931 25.3284C27.0154 24.9963 27.0154 24.4788 26.6931 24.1467L4.15406 1.59791C3.83179 1.27564 3.29468 1.26587 2.96265 1.59791C2.65015 1.92017 2.65015 2.45728 2.96265 2.77955Z", fillAlpha = 0.85f)
        }
        return _sFEraserSlash!!
    }

private var _sFEraserSlash: ImageVector? = null
