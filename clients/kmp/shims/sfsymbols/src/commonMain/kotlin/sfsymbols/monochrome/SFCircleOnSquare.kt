package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleOnSquare (monochrome)
 * Viewport: 27.9883 x 27.002
 */
public val SfSymbols.Monochrome.SFCircleOnSquare: ImageVector
    get() {
        if (_sFCircleOnSquare != null) {
            return _sFCircleOnSquare!!
        }
        _sFCircleOnSquare = sfIcon(
            name = "Monochrome.SFCircleOnSquare",
            viewportWidth = 27.9883f,
            viewportHeight = 27.002f
        ) {
            addSfPath("M20.2637 4.33594L20.2637 6.15267C19.7065 5.96245 19.126 5.8235 18.5254 5.74695L18.5254 4.43359C18.5254 3.02734 17.7637 2.30469 16.4258 2.30469L3.83789 2.30469C2.4707 2.30469 1.72852 3.02734 1.72852 4.43359L1.72852 16.9824C1.72852 18.3887 2.5 19.1113 3.83789 19.1113L6.62278 19.1113C6.77342 19.7153 6.98625 20.2935 7.25536 20.8398L3.79883 20.8398C1.26953 20.8398 0 19.5703 0 17.0703L0 4.33594C0 1.8457 1.26953 0.576172 3.79883 0.576172L16.4551 0.576172C18.9844 0.576172 20.2637 1.8457 20.2637 4.33594Z", fillAlpha = 0.85f)
            addSfPath("M16.9336 27.002C22.832 27.002 27.627 22.2266 27.627 16.3086C27.627 10.4102 22.832 5.61523 16.9336 5.61523C11.0254 5.61523 6.24023 10.4102 6.24023 16.3086C6.24023 22.2266 11.0254 27.002 16.9336 27.002ZM16.9336 25.2344C11.9629 25.2344 8.01758 21.2891 8.01758 16.3086C8.01758 11.3477 11.9629 7.40234 16.9336 7.40234C21.8945 7.40234 25.8496 11.3477 25.8496 16.3086C25.8496 21.2891 21.8945 25.2344 16.9336 25.2344Z", fillAlpha = 0.85f)
        }
        return _sFCircleOnSquare!!
    }

private var _sFCircleOnSquare: ImageVector? = null
