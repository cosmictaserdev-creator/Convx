package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFQuestionmark (dualtone)
 * Viewport: 13.7305 x 23.9941
 */
public val SfSymbols.Dualtone.SFQuestionmark: ImageVector
    get() {
        if (_sFQuestionmark != null) {
            return _sFQuestionmark!!
        }
        _sFQuestionmark = sfIcon(
            name = "Dualtone.SFQuestionmark",
            viewportWidth = 13.7305f,
            viewportHeight = 23.9941f
        ) {
            addSfPath("M6.09375 17.2266C6.81641 17.2266 7.16797 16.7285 7.16797 16.0449C7.16797 15.8301 7.16797 15.6152 7.16797 15.4102C7.1875 13.6816 7.7832 12.9199 9.85352 11.4844C12.0996 9.93164 13.3691 8.30078 13.3691 5.94727C13.3691 2.28516 10.4004 0.0683594 6.65039 0.0683594C3.78906 0.0683594 1.33789 1.52344 0.341797 3.97461C0.0976562 4.55078 0 5.08789 0 5.52734C0 6.13281 0.351562 6.55273 0.986328 6.55273C1.5332 6.55273 1.89453 6.23047 2.05078 5.69336C2.63672 3.19336 4.35547 2.04102 6.58203 2.04102C9.16016 2.04102 11.1914 3.58398 11.1914 5.9375C11.1914 7.73438 10.1172 8.81836 8.34961 10.0879C6.2793 11.5527 4.99023 12.9492 4.99023 15.1465C4.99023 15.459 4.99023 15.7617 4.99023 16.0742C4.99023 16.7578 5.36133 17.2266 6.09375 17.2266ZM6.10352 23.9941C6.98242 23.9941 7.67578 23.2812 7.67578 22.4219C7.67578 21.543 6.98242 20.8496 6.10352 20.8496C5.24414 20.8496 4.53125 21.543 4.53125 22.4219C4.53125 23.2812 5.24414 23.9941 6.10352 23.9941Z", fillAlpha = 0.85f)
        }
        return _sFQuestionmark!!
    }

private var _sFQuestionmark: ImageVector? = null
