package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowtriangleRight (dualtone)
 * Viewport: 20.7812 x 19.9707
 */
public val SfSymbols.Dualtone.SFArrowtriangleRight: ImageVector
    get() {
        if (_sFArrowtriangleRight != null) {
            return _sFArrowtriangleRight!!
        }
        _sFArrowtriangleRight = sfIcon(
            name = "Dualtone.SFArrowtriangleRight",
            viewportWidth = 20.7812f,
            viewportHeight = 19.9707f
        ) {
            addSfPath("M1.65039 19.9707C2.10938 19.9707 2.46094 19.7754 2.94922 19.5312L19.5215 11.5039C20.4688 11.0449 20.7812 10.6055 20.7812 9.99023C20.7812 9.38477 20.4688 8.94531 19.5215 8.47656L2.94922 0.458984C2.45117 0.214844 2.09961 0.0195312 1.64062 0.0195312C0.800781 0.0195312 0.273438 0.654297 0.273438 1.66016L0.283203 18.3301C0.283203 19.3262 0.810547 19.9707 1.65039 19.9707ZM2.1582 17.9395C2.07031 17.9395 2.01172 17.8711 2.01172 17.7344L2.01172 2.24609C2.01172 2.10938 2.07031 2.05078 2.1582 2.05078C2.23633 2.05078 2.29492 2.08008 2.36328 2.11914L18.3398 9.79492C18.418 9.83398 18.5059 9.90234 18.5059 9.99023C18.5059 10.0879 18.418 10.1562 18.3398 10.1953L2.36328 17.8711C2.29492 17.9004 2.23633 17.9395 2.1582 17.9395Z", fillAlpha = 0.85f)
        }
        return _sFArrowtriangleRight!!
    }

private var _sFArrowtriangleRight: ImageVector? = null
