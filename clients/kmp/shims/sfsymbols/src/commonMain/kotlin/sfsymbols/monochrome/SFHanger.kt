package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHanger (monochrome)
 * Viewport: 31.582 x 26.9434
 */
public val SfSymbols.Monochrome.SFHanger: ImageVector
    get() {
        if (_sFHanger != null) {
            return _sFHanger!!
        }
        _sFHanger = sfIcon(
            name = "Monochrome.SFHanger",
            viewportWidth = 31.582f,
            viewportHeight = 26.9434f
        ) {
            addSfPath("M0 22.4121C0 23.7305 0.888672 24.873 2.51953 24.873L28.6914 24.873C30.332 24.873 31.2207 23.7305 31.2207 22.4121C31.2207 21.4453 30.6836 20.4395 29.6484 19.8633L15.6055 11.9922L1.5625 19.8633C0.527344 20.4395 0 21.4453 0 22.4121ZM1.73828 22.3242C1.73828 21.9336 1.95312 21.5918 2.43164 21.3184L15.6055 13.9648L28.7891 21.3184C29.2676 21.5918 29.4727 21.9336 29.4727 22.3242C29.4727 22.793 29.1602 23.1641 28.4961 23.1641L2.71484 23.1641C2.06055 23.1641 1.73828 22.793 1.73828 22.3242ZM14.7754 12.8223L16.4453 12.8223L16.4453 11.6309C16.4453 10.4102 17.0801 9.91211 18.0762 9.11133C19.1211 8.28125 20.625 7.16797 20.625 4.72656C20.625 1.89453 18.6426 0 15.6738 0C12.7344 0 10.7715 2.08008 10.7715 5.19531C10.7715 5.71289 11.1328 6.06445 11.5918 6.06445C12.0508 6.06445 12.4023 5.71289 12.4023 5.29297C12.4023 3.10547 13.7207 1.64062 15.6738 1.64062C17.6758 1.64062 19.0039 2.88086 19.0039 4.72656C19.0039 6.32812 18.1445 7.07031 17.2559 7.77344C16.1426 8.67188 14.7754 9.47266 14.7754 11.6309Z", fillAlpha = 0.85f)
        }
        return _sFHanger!!
    }

private var _sFHanger: ImageVector? = null
