package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlowchartFill (monochrome)
 * Viewport: 27.207 x 29.2749
 */
public val SfSymbols.Monochrome.SFFlowchartFill: ImageVector
    get() {
        if (_sFFlowchartFill != null) {
            return _sFFlowchartFill!!
        }
        _sFFlowchartFill = sfIcon(
            name = "Monochrome.SFFlowchartFill",
            viewportWidth = 27.207f,
            viewportHeight = 29.2749f
        ) {
            addSfPath("M2.71484 10.4724L24.1309 10.4724C25.8203 10.4724 26.8457 9.4568 26.8457 7.79664L26.8457 3.16773C26.8457 1.49781 25.8203 0.482188 24.1309 0.482188L2.71484 0.482188C1.02539 0.482188 0 1.49781 0 3.16773L0 7.79664C0 9.4568 1.02539 10.4724 2.71484 10.4724ZM12.6367 13.2752L14.2188 13.2752L14.2188 9.69117L12.6367 9.69117ZM2.71484 22.3181L24.1309 22.3181C25.8203 22.3181 26.8457 21.3025 26.8457 19.6326L26.8457 15.0037C26.8457 13.3435 25.8203 12.3279 24.1309 12.3279L2.71484 12.3279C1.02539 12.3279 0 13.3435 0 15.0037L0 19.6326C0 21.3025 1.02539 22.3181 2.71484 22.3181ZM12.793 28.949C13.1543 29.3884 13.7402 29.3787 14.0918 28.949L16.8457 25.4822C17.334 24.8572 17.041 24.0369 16.2402 24.0369L10.6348 24.0369C9.82422 24.0369 9.51172 24.8474 10.0195 25.4822ZM12.6367 24.8963C12.6367 25.3357 12.9883 25.6873 13.4375 25.6873C13.877 25.6873 14.2285 25.3357 14.2285 24.8963L14.2285 21.2341L12.6367 21.2341Z", fillAlpha = 0.85f)
        }
        return _sFFlowchartFill!!
    }

private var _sFFlowchartFill: ImageVector? = null
