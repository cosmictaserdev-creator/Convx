package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleFilledIpad (dualtone)
 * Viewport: 21.084 x 27.998
 */
public val SfSymbols.Dualtone.SFCircleFilledIpad: ImageVector
    get() {
        if (_sFCircleFilledIpad != null) {
            return _sFCircleFilledIpad!!
        }
        _sFCircleFilledIpad = sfIcon(
            name = "Dualtone.SFCircleFilledIpad",
            viewportWidth = 21.084f,
            viewportHeight = 27.998f
        ) {
            addSfPath("M0 24.6777C0 26.6992 1.34766 27.998 3.4375 27.998L17.2852 27.998C19.375 27.998 20.7227 26.6992 20.7227 24.6777L20.7227 3.32031C20.7227 1.29883 19.375 0 17.2852 0L3.4375 0C1.34766 0 0 1.29883 0 3.32031ZM1.72852 24.4043L1.72852 3.59375C1.72852 2.40234 2.42188 1.72852 3.64258 1.72852L17.0703 1.72852C18.291 1.72852 18.9844 2.40234 18.9844 3.59375L18.9844 24.4043C18.9844 25.5859 18.291 26.2598 17.0703 26.2598L3.64258 26.2598C2.42188 26.2598 1.72852 25.5859 1.72852 24.4043Z", fillAlpha = 0.425f)
            addSfPath("M10.3613 20.2441C13.7988 20.2441 16.6113 17.4414 16.6113 13.9844C16.6113 10.5469 13.7988 7.74414 10.3613 7.74414C6.91406 7.74414 4.10156 10.5469 4.10156 13.9844C4.10156 17.4414 6.91406 20.2441 10.3613 20.2441Z", fillAlpha = 0.85f)
        }
        return _sFCircleFilledIpad!!
    }

private var _sFCircleFilledIpad: ImageVector? = null
