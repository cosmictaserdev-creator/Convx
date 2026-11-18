package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCapsuleOnRectangleFill (monochrome)
 * Viewport: 34.4238 x 28.3496
 */
public val SfSymbols.Monochrome.SFCapsuleOnRectangleFill: ImageVector
    get() {
        if (_sFCapsuleOnRectangleFill != null) {
            return _sFCapsuleOnRectangleFill!!
        }
        _sFCapsuleOnRectangleFill = sfIcon(
            name = "Monochrome.SFCapsuleOnRectangleFill",
            viewportWidth = 34.4238f,
            viewportHeight = 28.3496f
        ) {
            addSfPath("M26.1914 5.39062L26.1914 6.02429C25.1227 5.74683 23.9675 5.60547 22.7441 5.60547L16.6895 5.60547C9.75586 5.60547 5.00977 10.1465 5.00977 16.9727C5.00977 18.4704 5.23712 19.8579 5.67748 21.1035L5 21.1035C2.4707 21.1035 1.19141 19.8438 1.19141 17.3438L1.19141 5.39062C1.19141 2.90039 2.4707 1.63086 5 1.63086L22.3926 1.63086C24.9219 1.63086 26.1914 2.90039 26.1914 5.39062Z", fillAlpha = 0.85f)
            addSfPath("M16.6895 26.7871L22.7441 26.7871C28.8281 26.7871 32.8711 22.9395 32.8711 16.9727C32.8711 11.0254 28.8281 7.1582 22.7441 7.1582L16.6895 7.1582C10.5957 7.1582 6.5625 11.0254 6.5625 16.9727C6.5625 22.9395 10.5957 26.7871 16.6895 26.7871Z", fillAlpha = 0.85f)
        }
        return _sFCapsuleOnRectangleFill!!
    }

private var _sFCapsuleOnRectangleFill: ImageVector? = null
