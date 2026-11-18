package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownToLine (monochrome)
 * Viewport: 19.2676 x 25.6348
 */
public val SfSymbols.Monochrome.SFArrowDownToLine: ImageVector
    get() {
        if (_sFArrowDownToLine != null) {
            return _sFArrowDownToLine!!
        }
        _sFArrowDownToLine = sfIcon(
            name = "Monochrome.SFArrowDownToLine",
            viewportWidth = 19.2676f,
            viewportHeight = 25.6348f
        ) {
            addSfPath("M18.9062 24.6875C18.9062 24.1309 18.5254 23.7305 17.9785 23.7305L0.947266 23.7305C0.390625 23.7305 0 24.1309 0 24.6875C0 25.2344 0.390625 25.6348 0.947266 25.6348L17.9785 25.6348C18.5254 25.6348 18.9062 25.2344 18.9062 24.6875ZM0.917969 13.3496C0.390625 13.3496 0 13.7598 0 14.2871C0 14.541 0.0878906 14.7656 0.292969 14.9707L8.74023 23.418C8.94531 23.6328 9.18945 23.7402 9.45312 23.7402C9.7168 23.7402 9.96094 23.6328 10.166 23.418L18.6133 14.9707C18.8184 14.7656 18.9062 14.541 18.9062 14.2871C18.9062 13.7598 18.5156 13.3496 17.9883 13.3496C17.7344 13.3496 17.4707 13.4473 17.3145 13.6133L13.8477 17.0312L9.45312 21.8066L5.05859 17.0312L1.5918 13.6133C1.43555 13.4473 1.17188 13.3496 0.917969 13.3496ZM10.2832 21.9336L10.4004 18.1738L10.4004 0.986328C10.4004 0.439453 10.0098 0.0488281 9.45312 0.0488281C8.89648 0.0488281 8.50586 0.439453 8.50586 0.986328L8.50586 18.1738L8.62305 21.9336C8.64258 22.3828 9.00391 22.7539 9.45312 22.7539C9.90234 22.7539 10.2637 22.3828 10.2832 21.9336Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownToLine!!
    }

private var _sFArrowDownToLine: ImageVector? = null
