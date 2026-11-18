package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPhoneFill (monochrome)
 * Viewport: 23.6035 x 23.291
 */
public val SfSymbols.Monochrome.SFPhoneFill: ImageVector
    get() {
        if (_sFPhoneFill != null) {
            return _sFPhoneFill!!
        }
        _sFPhoneFill = sfIcon(
            name = "Monochrome.SFPhoneFill",
            viewportWidth = 23.6035f,
            viewportHeight = 23.291f
        ) {
            addSfPath("M6.37695 16.8066C10.0488 20.4785 14.4434 23.2812 17.9688 23.2812C19.6289 23.2812 21.0742 22.6172 22.0215 21.5527C22.9297 20.5273 23.2422 19.7754 23.2422 19.1016C23.2422 18.5645 22.9102 18.0859 22.0996 17.5098L19.0527 15.332C18.3008 14.7949 17.959 14.6973 17.5098 14.6973C17.1191 14.6973 16.7969 14.7656 16.1523 15.1172L14.1602 16.2207C13.9258 16.3477 13.8184 16.3672 13.6426 16.3672C13.4082 16.3672 13.252 16.3086 13.0176 16.2207C12.0605 15.7715 10.7227 14.7363 9.54102 13.5449C8.36914 12.3633 7.44141 11.1621 6.96289 10.2051C6.9043 10.0781 6.82617 9.88281 6.82617 9.6875C6.82617 9.53125 6.91406 9.38477 7.01172 9.20898L8.18359 7.20703C8.50586 6.66016 8.59375 6.36719 8.59375 5.9375C8.59375 5.44922 8.42773 4.93164 7.95898 4.27734L5.86914 1.34766C5.2832 0.507812 4.82422 0 4.15039 0C3.31055 0 2.30469 0.644531 1.5918 1.33789C0.546875 2.34375 0 3.73047 0 5.30273C0 8.85742 2.73438 13.1641 6.37695 16.8066Z", fillAlpha = 0.85f)
        }
        return _sFPhoneFill!!
    }

private var _sFPhoneFill: ImageVector? = null
