package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlatterFilledBottomIphone (monochrome)
 * Viewport: 16.0254 x 26.2402
 */
public val SfSymbols.Monochrome.SFPlatterFilledBottomIphone: ImageVector
    get() {
        if (_sFPlatterFilledBottomIphone != null) {
            return _sFPlatterFilledBottomIphone!!
        }
        _sFPlatterFilledBottomIphone = sfIcon(
            name = "Monochrome.SFPlatterFilledBottomIphone",
            viewportWidth = 16.0254f,
            viewportHeight = 26.2402f
        ) {
            addSfPath("M0 22.998C0 24.9707 1.28906 26.2207 3.33008 26.2207L12.3242 26.2207C14.3652 26.2207 15.6641 24.9707 15.6641 22.998L15.6641 3.22266C15.6641 1.25 14.3652 0 12.3242 0L3.33008 0C1.28906 0 0 1.25 0 3.22266ZM1.72852 22.7441L1.72852 3.47656C1.72852 2.33398 2.35352 1.72852 3.54492 1.72852L12.1191 1.72852C13.3008 1.72852 13.9258 2.33398 13.9258 3.47656L13.9258 22.7441C13.9258 23.8867 13.3008 24.4922 12.1191 24.4922L3.54492 24.4922C2.35352 24.4922 1.72852 23.8867 1.72852 22.7441Z", fillAlpha = 0.85f)
            addSfPath("M4.46289 22.8809L11.1914 22.8809C11.875 22.8809 12.3242 22.4316 12.3242 21.7285L12.3242 20.293C12.3242 19.5898 11.875 19.1406 11.1914 19.1406L4.46289 19.1406C3.7793 19.1406 3.33984 19.5898 3.33984 20.293L3.33984 21.7285C3.33984 22.4316 3.7793 22.8809 4.46289 22.8809Z", fillAlpha = 0.85f)
        }
        return _sFPlatterFilledBottomIphone!!
    }

private var _sFPlatterFilledBottomIphone: ImageVector? = null
