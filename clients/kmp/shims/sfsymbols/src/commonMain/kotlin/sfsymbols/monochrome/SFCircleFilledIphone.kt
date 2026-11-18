package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCircleFilledIphone (monochrome)
 * Viewport: 16.0254 x 26.2402
 */
public val SfSymbols.Monochrome.SFCircleFilledIphone: ImageVector
    get() {
        if (_sFCircleFilledIphone != null) {
            return _sFCircleFilledIphone!!
        }
        _sFCircleFilledIphone = sfIcon(
            name = "Monochrome.SFCircleFilledIphone",
            viewportWidth = 16.0254f,
            viewportHeight = 26.2402f
        ) {
            addSfPath("M0 22.998C0 24.9707 1.28906 26.2207 3.33008 26.2207L12.3242 26.2207C14.3652 26.2207 15.6641 24.9707 15.6641 22.998L15.6641 3.22266C15.6641 1.25 14.3652 0 12.3242 0L3.33008 0C1.28906 0 0 1.25 0 3.22266ZM1.72852 22.7441L1.72852 3.47656C1.72852 2.33398 2.35352 1.72852 3.54492 1.72852L12.1191 1.72852C13.3008 1.72852 13.9258 2.33398 13.9258 3.47656L13.9258 22.7441C13.9258 23.8867 13.3008 24.4922 12.1191 24.4922L3.54492 24.4922C2.35352 24.4922 1.72852 23.8867 1.72852 22.7441Z", fillAlpha = 0.85f)
            addSfPath("M7.83203 17.7832C10.4102 17.7832 12.5098 15.6836 12.5098 13.1055C12.5098 10.5273 10.4102 8.42773 7.83203 8.42773C5.25391 8.42773 3.1543 10.5273 3.1543 13.1055C3.1543 15.6836 5.25391 17.7832 7.83203 17.7832Z", fillAlpha = 0.85f)
        }
        return _sFCircleFilledIphone!!
    }

private var _sFCircleFilledIphone: ImageVector? = null
