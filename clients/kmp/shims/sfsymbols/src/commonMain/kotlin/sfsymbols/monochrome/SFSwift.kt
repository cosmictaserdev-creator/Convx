package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSwift (monochrome)
 * Viewport: 28.0762 x 24.8535
 */
public val SfSymbols.Monochrome.SFSwift: ImageVector
    get() {
        if (_sFSwift != null) {
            return _sFSwift!!
        }
        _sFSwift = sfIcon(
            name = "Monochrome.SFSwift",
            viewportWidth = 28.0762f,
            viewportHeight = 24.8535f
        ) {
            addSfPath("M0 15.8691C2.09961 19.2969 7.57812 24.5703 14.9609 24.5703C19.6582 24.5703 20.957 22.373 23.6816 22.373C25.957 22.373 26.8555 23.9062 27.207 24.502C27.2949 24.6973 27.4707 24.8145 27.5781 24.4531C27.6465 24.1895 27.7148 23.8379 27.7148 23.418C27.7148 22.2168 27.3438 19.9219 25.3418 17.4219C25.7812 16.1914 25.8301 14.834 25.8301 14.0234C25.8301 10.4004 23.7793 4.07227 17.0117 0C18.418 1.98242 20.3125 5.01953 20.3125 9.21875C20.3125 10.7422 19.8926 12.4219 19.5508 13.252C15.8789 11.123 8.82812 5.25391 6.00586 2.25586C8.27148 5.73242 10.9473 8.86719 13.916 12.168C9.92188 9.63867 6.20117 6.81641 2.73438 3.71094C6.05469 8.53516 10.2539 13.2812 15.8105 17.7637C14.2188 18.6523 12.5977 19.3457 9.80469 19.3457C6.40625 19.3457 2.25586 17.7051 0 15.8691Z", fillAlpha = 0.85f)
        }
        return _sFSwift!!
    }

private var _sFSwift: ImageVector? = null
