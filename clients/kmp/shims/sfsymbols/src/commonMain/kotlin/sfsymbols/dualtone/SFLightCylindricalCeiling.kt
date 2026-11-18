package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightCylindricalCeiling (dualtone)
 * Viewport: 24.8242 x 25.8496
 */
public val SfSymbols.Dualtone.SFLightCylindricalCeiling: ImageVector
    get() {
        if (_sFLightCylindricalCeiling != null) {
            return _sFLightCylindricalCeiling!!
        }
        _sFLightCylindricalCeiling = sfIcon(
            name = "Dualtone.SFLightCylindricalCeiling",
            viewportWidth = 24.8242f,
            viewportHeight = 25.8496f
        ) {
            addSfPath("M12.2363 25.8301C19.5996 25.8301 24.4629 23.3301 24.4629 19.4824L24.4629 6.83594C24.4629 2.69531 19.5898 0 12.2363 0C4.87305 0 0 2.69531 0 6.83594L0 19.4824C0 23.3301 4.86328 25.8301 12.2363 25.8301ZM12.2363 24.1016C5.99609 24.1016 1.72852 22.1875 1.72852 19.4824L1.72852 6.83594C1.72852 3.82812 5.9668 1.72852 12.2363 1.72852C18.4961 1.72852 22.7344 3.82812 22.7344 6.83594L22.7344 19.4824C22.7344 22.1875 18.4766 24.1016 12.2363 24.1016ZM12.2363 14.8535C18.4766 14.8535 22.7344 16.7676 22.7344 19.4824L24.4629 19.4824C24.4629 15.6348 19.5996 13.125 12.2363 13.125C4.86328 13.125 0 15.6348 0 19.4824L1.72852 19.4824C1.72852 16.7676 5.99609 14.8535 12.2363 14.8535ZM12.2363 19.834C15.0293 19.834 17.2656 18.3496 17.3438 16.5625C15.8984 16.2207 14.1602 16.0156 12.2363 16.0156C10.3125 16.0156 8.56445 16.2207 7.12891 16.5625C7.19727 18.3496 9.42383 19.834 12.2363 19.834Z", fillAlpha = 0.85f)
        }
        return _sFLightCylindricalCeiling!!
    }

private var _sFLightCylindricalCeiling: ImageVector? = null
