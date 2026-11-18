package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowRightToLine (dualtone)
 * Viewport: 25.957 x 18.916
 */
public val SfSymbols.Dualtone.SFArrowRightToLine: ImageVector
    get() {
        if (_sFArrowRightToLine != null) {
            return _sFArrowRightToLine!!
        }
        _sFArrowRightToLine = sfIcon(
            name = "Dualtone.SFArrowRightToLine",
            viewportWidth = 25.957f,
            viewportHeight = 18.916f
        ) {
            addSfPath("M24.6387 18.9062C25.1855 18.9062 25.5957 18.5254 25.5957 17.9883L25.5957 0.947266C25.5957 0.390625 25.1855 0 24.6387 0C24.082 0 23.6816 0.390625 23.6816 0.947266L23.6816 17.9883C23.6816 18.5254 24.082 18.9062 24.6387 18.9062ZM13.3008 0.917969C13.3008 1.17188 13.3984 1.43555 13.5645 1.60156L16.9922 5.05859L21.7578 9.45312L16.9922 13.8477L13.5645 17.3145C13.3984 17.4805 13.3008 17.7344 13.3008 17.9883C13.3008 18.5156 13.7109 18.9062 14.2383 18.9062C14.4922 18.9062 14.7168 18.8184 14.9316 18.6133L23.3691 10.166C23.584 9.96094 23.6914 9.7168 23.6914 9.45312C23.6914 9.18945 23.584 8.94531 23.3691 8.75L14.9316 0.292969C14.7168 0.0976562 14.4922 0 14.2383 0C13.7109 0 13.3008 0.390625 13.3008 0.917969ZM21.8848 10.2832C22.334 10.2637 22.7051 9.91211 22.7051 9.45312C22.7051 9.00391 22.334 8.65234 21.8848 8.63281L18.125 8.50586L0.9375 8.50586C0.390625 8.50586 0 8.90625 0 9.45312C0 10.0098 0.390625 10.4004 0.9375 10.4004L18.125 10.4004Z", fillAlpha = 0.85f)
        }
        return _sFArrowRightToLine!!
    }

private var _sFArrowRightToLine: ImageVector? = null
