package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIphonePatternDiagonalline (monochrome)
 * Viewport: 16.0254 x 26.2402
 */
public val SfSymbols.Monochrome.SFIphonePatternDiagonalline: ImageVector
    get() {
        if (_sFIphonePatternDiagonalline != null) {
            return _sFIphonePatternDiagonalline!!
        }
        _sFIphonePatternDiagonalline = sfIcon(
            name = "Monochrome.SFIphonePatternDiagonalline",
            viewportWidth = 16.0254f,
            viewportHeight = 26.2402f
        ) {
            addSfPath("M13.9258 16.2743L4.13038 24.4922L3.54492 24.4922C3.21689 24.4922 2.93179 24.4463 2.69251 24.3543L13.9258 14.9172ZM13.9258 21.1871L9.99841 24.4922L8.36859 24.4922L13.9258 19.8291ZM13.9258 11.3658L1.72852 21.6041L1.72852 20.2372L13.9258 10.0082ZM13.9258 6.45372L1.72852 16.692L1.72852 15.3251L13.9258 5.09606ZM13.3184 2.05284L1.72852 11.7815L1.72852 10.414L12.0653 1.72852L12.1191 1.72852C12.6217 1.72852 13.0235 1.83802 13.3184 2.05284ZM1.72852 6.86925L1.72852 5.5025L6.22891 1.72852L7.85051 1.72852Z", fillAlpha = 0.85f)
            addSfPath("M0 22.998C0 24.9707 1.28906 26.2207 3.33008 26.2207L12.3242 26.2207C14.3652 26.2207 15.6641 24.9707 15.6641 22.998L15.6641 3.22266C15.6641 1.25 14.3652 0 12.3242 0L3.33008 0C1.28906 0 0 1.25 0 3.22266ZM1.72852 22.7441L1.72852 3.47656C1.72852 2.33398 2.35352 1.72852 3.54492 1.72852L12.1191 1.72852C13.3008 1.72852 13.9258 2.33398 13.9258 3.47656L13.9258 22.7441C13.9258 23.8867 13.3008 24.4922 12.1191 24.4922L3.54492 24.4922C2.35352 24.4922 1.72852 23.8867 1.72852 22.7441Z", fillAlpha = 0.85f)
        }
        return _sFIphonePatternDiagonalline!!
    }

private var _sFIphonePatternDiagonalline: ImageVector? = null
