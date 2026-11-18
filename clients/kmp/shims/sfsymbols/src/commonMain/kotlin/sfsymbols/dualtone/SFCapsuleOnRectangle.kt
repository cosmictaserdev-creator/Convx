package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCapsuleOnRectangle (dualtone)
 * Viewport: 32.0312 x 25.2246
 */
public val SfSymbols.Dualtone.SFCapsuleOnRectangle: ImageVector
    get() {
        if (_sFCapsuleOnRectangle != null) {
            return _sFCapsuleOnRectangle!!
        }
        _sFCapsuleOnRectangle = sfIcon(
            name = "Dualtone.SFCapsuleOnRectangle",
            viewportWidth = 32.0312f,
            viewportHeight = 25.2246f
        ) {
            addSfPath("M25 3.82812L25 6.07358C24.4568 5.90547 23.8801 5.78494 23.2715 5.71665L23.2715 3.92578C23.2715 2.51953 22.5 1.79688 21.1621 1.79688L3.83789 1.79688C2.4707 1.79688 1.72852 2.51953 1.72852 3.92578L1.72852 15.6836C1.72852 17.0898 2.5 17.8125 3.83789 17.8125L5.61737 17.8125C5.73428 18.4247 5.90837 19.0007 6.13155 19.541L3.79883 19.541C1.26953 19.541 0 18.2812 0 15.7812L0 3.82812C0 1.33789 1.26953 0.0683594 3.79883 0.0683594L21.2012 0.0683594C23.7207 0.0683594 25 1.33789 25 3.82812Z", fillAlpha = 0.425f)
            addSfPath("M15.498 25.2246L21.543 25.2246C27.6367 25.2246 31.6699 21.377 31.6699 15.4102C31.6699 9.46289 27.6367 5.5957 21.543 5.5957L15.498 5.5957C9.39453 5.5957 5.37109 9.46289 5.37109 15.4102C5.37109 21.377 9.39453 25.2246 15.498 25.2246ZM15.498 23.4961C10.4102 23.4961 7.09961 20.3418 7.09961 15.4102C7.09961 10.498 10.4102 7.32422 15.498 7.32422L21.543 7.32422C26.6211 7.32422 29.9414 10.498 29.9414 15.4102C29.9414 20.3418 26.6211 23.4961 21.543 23.4961Z", fillAlpha = 0.85f)
        }
        return _sFCapsuleOnRectangle!!
    }

private var _sFCapsuleOnRectangle: ImageVector? = null
