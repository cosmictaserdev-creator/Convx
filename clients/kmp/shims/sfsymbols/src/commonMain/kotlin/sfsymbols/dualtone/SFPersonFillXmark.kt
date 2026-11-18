package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPersonFillXmark (dualtone)
 * Viewport: 30.9766 x 22.5293
 */
public val SfSymbols.Dualtone.SFPersonFillXmark: ImageVector
    get() {
        if (_sFPersonFillXmark != null) {
            return _sFPersonFillXmark!!
        }
        _sFPersonFillXmark = sfIcon(
            name = "Dualtone.SFPersonFillXmark",
            viewportWidth = 30.9766f,
            viewportHeight = 22.5293f
        ) {
            addSfPath("M2.06055 22.5195L19.0723 22.5195C20.3516 22.5195 21.1328 21.9043 21.1328 20.9082C21.1328 17.666 17.0801 13.1934 10.5664 13.1934C4.05273 13.1934 0 17.666 0 20.9082C0 21.9043 0.78125 22.5195 2.06055 22.5195ZM10.5762 10.9375C13.3008 10.9375 15.5859 8.51562 15.5859 5.39062C15.5859 2.33398 13.2812 0 10.5762 0C7.88086 0 5.57617 2.37305 5.57617 5.41016C5.57617 8.51562 7.86133 10.9375 10.5762 10.9375Z", fillAlpha = 0.425f)
            addSfPath("M21.0156 10.6445C21.2598 10.6445 21.4746 10.5469 21.6406 10.3711L30.3516 1.65039C30.5371 1.46484 30.6152 1.26953 30.6152 1.03516C30.6152 0.556641 30.2344 0.175781 29.7461 0.175781C29.5117 0.175781 29.3262 0.253906 29.1309 0.449219L20.4004 9.15039C20.2246 9.31641 20.1367 9.53125 20.1367 9.76562C20.1367 10.2441 20.5273 10.6445 21.0156 10.6445ZM29.7461 10.6445C30.2344 10.6445 30.6152 10.2441 30.6152 9.76562C30.6152 9.53125 30.5273 9.31641 30.3516 9.15039L21.6211 0.449219C21.4355 0.253906 21.2402 0.175781 21.0156 0.175781C20.5273 0.175781 20.1367 0.556641 20.1367 1.03516C20.1367 1.26953 20.2148 1.46484 20.4004 1.65039L29.1113 10.3711C29.2871 10.5469 29.4922 10.6445 29.7461 10.6445Z", fillAlpha = 0.85f)
        }
        return _sFPersonFillXmark!!
    }

private var _sFPersonFillXmark: ImageVector? = null
