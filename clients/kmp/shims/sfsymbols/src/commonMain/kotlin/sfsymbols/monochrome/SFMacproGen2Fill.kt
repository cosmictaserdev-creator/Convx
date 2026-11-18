package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMacproGen2Fill (monochrome)
 * Viewport: 18.3887 x 29.3848
 */
public val SfSymbols.Monochrome.SFMacproGen2Fill: ImageVector
    get() {
        if (_sFMacproGen2Fill != null) {
            return _sFMacproGen2Fill!!
        }
        _sFMacproGen2Fill = sfIcon(
            name = "Monochrome.SFMacproGen2Fill",
            viewportWidth = 18.3887f,
            viewportHeight = 29.3848f
        ) {
            addSfPath("M0 23.5742C0 27.2266 3.34961 29.3848 9.01367 29.3848C14.6777 29.3848 18.0273 27.2266 18.0273 23.5742L18.0273 5.81055C18.0273 4.29688 17.4414 3.06641 16.4355 2.2168C16.0352 1.80664 15.5469 1.81641 15.3516 2.20703C14.834 3.71094 12.0508 4.56055 9.01367 4.56055C5.98633 4.56055 3.19336 3.71094 2.68555 2.20703C2.48047 1.81641 2.00195 1.80664 1.60156 2.2168C0.585938 3.06641 0 4.29688 0 5.81055ZM9.01367 3.19336C11.8359 3.19336 14.1406 2.49023 14.1406 1.60156C14.1406 0.732422 11.8359 0.0195312 9.01367 0.0195312C6.19141 0.0195312 3.89648 0.732422 3.89648 1.60156C3.89648 2.49023 6.19141 3.19336 9.01367 3.19336Z", fillAlpha = 0.85f)
        }
        return _sFMacproGen2Fill!!
    }

private var _sFMacproGen2Fill: ImageVector? = null
