package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpToLine (dualtone)
 * Viewport: 19.2676 x 25.6348
 */
public val SfSymbols.Dualtone.SFArrowUpToLine: ImageVector
    get() {
        if (_sFArrowUpToLine != null) {
            return _sFArrowUpToLine!!
        }
        _sFArrowUpToLine = sfIcon(
            name = "Dualtone.SFArrowUpToLine",
            viewportWidth = 19.2676f,
            viewportHeight = 25.6348f
        ) {
            addSfPath("M18.9062 1.00586C18.9062 0.449219 18.5254 0.0488281 17.9785 0.0488281L0.947266 0.0488281C0.390625 0.0488281 0 0.449219 0 1.00586C0 1.55273 0.390625 1.95312 0.947266 1.95312L17.9785 1.95312C18.5254 1.95312 18.9062 1.55273 18.9062 1.00586ZM0.917969 12.334C1.17188 12.334 1.43555 12.2363 1.5918 12.0801L5.05859 8.65234L9.45312 3.88672L13.8477 8.65234L17.3145 12.0801C17.4707 12.2363 17.7344 12.334 17.9883 12.334C18.5156 12.334 18.9062 11.9336 18.9062 11.3965C18.9062 11.1426 18.8184 10.9277 18.6133 10.7129L10.166 2.26562C9.96094 2.05078 9.7168 1.94336 9.45312 1.94336C9.18945 1.94336 8.94531 2.05078 8.74023 2.26562L0.292969 10.7129C0.0878906 10.9277 0 11.1426 0 11.3965C0 11.9336 0.390625 12.334 0.917969 12.334ZM10.2832 3.75977C10.2637 3.31055 9.90234 2.92969 9.45312 2.92969C9.00391 2.92969 8.64258 3.31055 8.62305 3.75977L8.50586 7.51953L8.50586 24.6973C8.50586 25.2441 8.89648 25.6348 9.45312 25.6348C10.0098 25.6348 10.4004 25.2441 10.4004 24.6973L10.4004 7.51953Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpToLine!!
    }

private var _sFArrowUpToLine: ImageVector? = null
