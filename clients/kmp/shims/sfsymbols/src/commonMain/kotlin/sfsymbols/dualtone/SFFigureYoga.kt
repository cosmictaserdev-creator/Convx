package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFigureYoga (dualtone)
 * Viewport: 25.9473 x 27.4902
 */
public val SfSymbols.Dualtone.SFFigureYoga: ImageVector
    get() {
        if (_sFFigureYoga != null) {
            return _sFFigureYoga!!
        }
        _sFFigureYoga = sfIcon(
            name = "Dualtone.SFFigureYoga",
            viewportWidth = 25.9473f,
            viewportHeight = 27.4902f
        ) {
            addSfPath("M17.3926 5.41016C18.8965 5.41016 20.0977 4.19922 20.0977 2.69531C20.0977 1.20117 18.8965 0 17.3926 0C15.8887 0 14.6777 1.20117 14.6777 2.69531C14.6777 4.19922 15.8887 5.41016 17.3926 5.41016ZM8.96484 27.4902C9.6582 27.4902 10.2441 26.9238 10.2441 26.2012L10.2441 14.5801C13.0762 14.1309 15.5176 12.2559 16.6992 9.70703C16.8066 9.44336 17.0996 9.31641 17.5977 9.49219L23.8574 11.5918C24.7266 11.8848 25.332 11.4062 25.5176 10.8105C25.7129 10.1855 25.4785 9.43359 24.6777 9.16016L5.9082 2.90039C5.03906 2.60742 4.43359 3.08594 4.24805 3.69141C4.05273 4.31641 4.27734 5.05859 5.08789 5.33203L11.8457 7.58789C12.1484 7.69531 12.3047 8.06641 12.0996 8.38867C11.3965 9.52148 10.332 10.2051 8.96484 10.2051L2.56836 10.2051L2.56836 3.76953C2.56836 3.00781 2.05078 2.5 1.2793 2.5C0.507812 2.5 0 3.00781 0 3.76953L0 11.377C0 12.2168 0.419922 12.6172 1.06445 12.7246L6.73828 13.6621L7.68555 19.2676L7.68555 26.2012C7.68555 26.9238 8.25195 27.4902 8.96484 27.4902Z", fillAlpha = 0.85f)
        }
        return _sFFigureYoga!!
    }

private var _sFFigureYoga: ImageVector? = null
