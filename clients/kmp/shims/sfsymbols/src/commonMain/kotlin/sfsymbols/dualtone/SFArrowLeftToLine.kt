package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowLeftToLine (dualtone)
 * Viewport: 25.957 x 18.916
 */
public val SfSymbols.Dualtone.SFArrowLeftToLine: ImageVector
    get() {
        if (_sFArrowLeftToLine != null) {
            return _sFArrowLeftToLine!!
        }
        _sFArrowLeftToLine = sfIcon(
            name = "Dualtone.SFArrowLeftToLine",
            viewportWidth = 25.957f,
            viewportHeight = 18.916f
        ) {
            addSfPath("M0.957031 18.9062C1.50391 18.9062 1.9043 18.5254 1.9043 17.9883L1.9043 0.947266C1.9043 0.390625 1.50391 0 0.957031 0C0.400391 0 0 0.390625 0 0.947266L0 17.9883C0 18.5254 0.400391 18.9062 0.957031 18.9062ZM12.2852 0.917969C12.2852 0.390625 11.8848 0 11.3574 0C11.1035 0 10.8789 0.0976562 10.6641 0.292969L2.2168 8.75C2.01172 8.94531 1.89453 9.18945 1.89453 9.45312C1.89453 9.7168 2.01172 9.96094 2.2168 10.166L10.6641 18.6133C10.8789 18.8184 11.1035 18.9062 11.3574 18.9062C11.8848 18.9062 12.2852 18.5156 12.2852 17.9883C12.2852 17.7344 12.1875 17.4805 12.0312 17.3145L8.60352 13.8477L3.83789 9.45312L8.60352 5.05859L12.0312 1.60156C12.1875 1.43555 12.2852 1.17188 12.2852 0.917969ZM3.71094 10.2832L7.4707 10.4004L24.6582 10.4004C25.1953 10.4004 25.5957 10.0098 25.5957 9.45312C25.5957 8.90625 25.1953 8.50586 24.6582 8.50586L7.4707 8.50586L3.71094 8.63281C3.26172 8.65234 2.88086 9.00391 2.88086 9.45312C2.88086 9.91211 3.26172 10.2637 3.71094 10.2832Z", fillAlpha = 0.85f)
        }
        return _sFArrowLeftToLine!!
    }

private var _sFArrowLeftToLine: ImageVector? = null
