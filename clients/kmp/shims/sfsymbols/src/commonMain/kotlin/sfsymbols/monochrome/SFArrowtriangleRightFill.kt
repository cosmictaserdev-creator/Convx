package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleRightFill (monochrome)
 * Viewport: 20.7812 x 19.9707
 */
public val SfSymbols.Monochrome.SFArrowtriangleRightFill: ImageVector
    get() {
        if (_sFArrowtriangleRightFill != null) {
            return _sFArrowtriangleRightFill!!
        }
        _sFArrowtriangleRightFill = sfIcon(
            name = "Monochrome.SFArrowtriangleRightFill",
            viewportWidth = 20.7812f,
            viewportHeight = 19.9707f
        ) {
            addSfPath("M1.65039 19.9707C2.10938 19.9707 2.46094 19.7754 2.94922 19.5312L19.5215 11.5039C20.4688 11.0449 20.7812 10.6055 20.7812 9.99023C20.7812 9.38477 20.4688 8.94531 19.5215 8.47656L2.94922 0.458984C2.45117 0.214844 2.09961 0.0195312 1.64062 0.0195312C0.800781 0.0195312 0.273438 0.654297 0.273438 1.66016L0.283203 18.3301C0.283203 19.3262 0.810547 19.9707 1.65039 19.9707Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleRightFill!!
    }

private var _sFArrowtriangleRightFill: ImageVector? = null
