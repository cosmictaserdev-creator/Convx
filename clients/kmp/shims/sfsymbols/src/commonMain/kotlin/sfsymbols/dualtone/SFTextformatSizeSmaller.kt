package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextformatSizeSmaller (dualtone)
 * Viewport: 11.4648 x 13.291
 */
public val SfSymbols.Dualtone.SFTextformatSizeSmaller: ImageVector
    get() {
        if (_sFTextformatSizeSmaller != null) {
            return _sFTextformatSizeSmaller!!
        }
        _sFTextformatSizeSmaller = sfIcon(
            name = "Dualtone.SFTextformatSizeSmaller",
            viewportWidth = 11.4648f,
            viewportHeight = 13.291f
        ) {
            addSfPath("M0.888672 13.291C1.33789 13.291 1.63086 13.0566 1.79688 12.5391L2.99805 9.14062L8.08594 9.14062L9.28711 12.5391C9.47266 13.0859 9.74609 13.291 10.2148 13.291C10.7422 13.291 11.1035 12.959 11.1035 12.4707C11.1035 12.2656 11.0547 12.0508 10.9668 11.8262L6.85547 0.976562C6.61133 0.332031 6.16211 0 5.53711 0C4.92188 0 4.48242 0.3125 4.24805 0.976562L0.117188 11.8262C0.0390625 12.0312 0 12.2559 0 12.4707C0 12.9688 0.351562 13.291 0.888672 13.291ZM3.49609 7.71484L5.41992 2.1582L5.66406 2.1582L7.60742 7.71484Z", fillAlpha = 0.85f)
        }
        return _sFTextformatSizeSmaller!!
    }

private var _sFTextformatSizeSmaller: ImageVector? = null
