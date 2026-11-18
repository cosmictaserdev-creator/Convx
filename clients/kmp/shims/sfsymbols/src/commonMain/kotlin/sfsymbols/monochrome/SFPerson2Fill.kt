package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPerson2Fill (monochrome)
 * Viewport: 37.0312 x 25.6348
 */
public val SfSymbols.Monochrome.SFPerson2Fill: ImageVector
    get() {
        if (_sFPerson2Fill != null) {
            return _sFPerson2Fill!!
        }
        _sFPerson2Fill = sfIcon(
            name = "Monochrome.SFPerson2Fill",
            viewportWidth = 37.0312f,
            viewportHeight = 25.6348f
        ) {
            addSfPath("M16.001 16.3402C13.9349 18.1299 12.7734 20.4168 12.7734 22.4609C12.7734 23.0477 12.897 23.6 13.1794 24.0723L3.29102 24.0723C1.80664 24.0723 1.19141 23.4863 1.19141 22.3633C1.19141 18.8574 4.72656 14.6973 10.3906 14.6973C12.622 14.6973 14.5227 15.3407 16.001 16.3402ZM14.7656 7.93945C14.7656 10.6641 12.7734 12.7734 10.4102 12.7734C8.04688 12.7734 6.04492 10.6641 6.04492 7.95898C6.04492 5.3125 8.06641 3.25195 10.4102 3.25195C12.7539 3.25195 14.7656 5.27344 14.7656 7.93945Z", fillAlpha = 0.85f)
            addSfPath("M24.9121 12.4902C27.6367 12.4902 29.9219 10.0684 29.9219 6.94336C29.9219 3.88672 27.6172 1.55273 24.9121 1.55273C22.2168 1.55273 19.9121 3.92578 19.9121 6.96289C19.9121 10.0684 22.1973 12.4902 24.9121 12.4902ZM16.8457 24.0723L32.959 24.0723C34.7656 24.0723 35.4688 23.5547 35.4688 22.4609C35.4688 19.2188 31.416 14.7461 24.9023 14.7461C18.3887 14.7461 14.3359 19.2188 14.3359 22.4609C14.3359 23.5547 15.0391 24.0723 16.8457 24.0723Z", fillAlpha = 0.85f)
        }
        return _sFPerson2Fill!!
    }

private var _sFPerson2Fill: ImageVector? = null
