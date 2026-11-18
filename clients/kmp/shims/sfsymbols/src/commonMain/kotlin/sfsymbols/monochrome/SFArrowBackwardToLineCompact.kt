package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowBackwardToLineCompact (monochrome)
 * Viewport: 22.4316 x 18.916
 */
public val SfSymbols.Monochrome.SFArrowBackwardToLineCompact: ImageVector
    get() {
        if (_sFArrowBackwardToLineCompact != null) {
            return _sFArrowBackwardToLineCompact!!
        }
        _sFArrowBackwardToLineCompact = sfIcon(
            name = "Monochrome.SFArrowBackwardToLineCompact",
            viewportWidth = 22.4316f,
            viewportHeight = 18.916f
        ) {
            addSfPath("M0.957031 18.9062C1.50391 18.9062 1.9043 18.5254 1.9043 17.9785L1.9043 0.9375C1.9043 0.390625 1.50391 0 0.957031 0C0.400391 0 0 0.390625 0 0.9375L0 17.9785C0 18.5254 0.400391 18.9062 0.957031 18.9062ZM12.2852 0.917969C12.2852 0.390625 11.8848 0 11.3574 0C11.1035 0 10.8789 0.0878906 10.6641 0.292969L2.2168 8.74023C2.01172 8.94531 1.89453 9.18945 1.89453 9.45312C1.89453 9.7168 2.01172 9.96094 2.2168 10.1562L10.6641 18.6133C10.8789 18.8086 11.1035 18.9062 11.3574 18.9062C11.8848 18.9062 12.2852 18.5156 12.2852 17.9883C12.2852 17.7344 12.1875 17.4707 12.0312 17.3047L8.60352 13.8477L3.83789 9.45312L8.60352 5.05859L12.0312 1.5918C12.1875 1.42578 12.2852 1.17188 12.2852 0.917969ZM3.71094 10.2734L7.4707 10.4004L21.1328 10.4004C21.6797 10.4004 22.0703 10 22.0703 9.45312C22.0703 8.89648 21.6797 8.50586 21.1328 8.50586L7.4707 8.50586L3.71094 8.62305C3.26172 8.64258 2.88086 8.99414 2.88086 9.45312C2.88086 9.90234 3.26172 10.2539 3.71094 10.2734Z", fillAlpha = 0.85f)
        }
        return _sFArrowBackwardToLineCompact!!
    }

private var _sFArrowBackwardToLineCompact: ImageVector? = null
