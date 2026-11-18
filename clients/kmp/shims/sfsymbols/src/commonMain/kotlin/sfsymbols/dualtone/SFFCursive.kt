package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFCursive (dualtone)
 * Viewport: 15.8203 x 22.0801
 */
public val SfSymbols.Dualtone.SFFCursive: ImageVector
    get() {
        if (_sFFCursive != null) {
            return _sFFCursive!!
        }
        _sFFCursive = sfIcon(
            name = "Dualtone.SFFCursive",
            viewportWidth = 15.8203f,
            viewportHeight = 22.0801f
        ) {
            addSfPath("M2.71484 22.0801C5.50781 22.0801 7.32422 20.4883 7.77344 17.2754L9.4043 5.6543C9.73633 3.28125 10.7715 2.25586 12.5781 2.25586C13.8281 2.25586 14.2285 2.41211 14.6777 2.41211C15.1367 2.41211 15.459 2.12891 15.459 1.63086C15.459 0.78125 14.0527 0.546875 12.7441 0.546875C9.95117 0.546875 8.13477 2.13867 7.68555 5.35156L6.04492 16.9727C5.71289 19.3457 4.6875 20.3711 2.87109 20.3711C1.62109 20.3711 1.26953 20.1953 0.791016 20.1953C0.322266 20.1953 0 20.4883 0 20.9766C0 21.8457 1.40625 22.0801 2.71484 22.0801ZM2.09961 11.3086L14.375 11.3086C14.8926 11.3086 15.2539 10.957 15.2539 10.4492C15.2539 9.95117 14.8828 9.59961 14.3555 9.59961L2.08008 9.59961C1.5625 9.59961 1.19141 9.95117 1.19141 10.4492C1.19141 10.957 1.57227 11.3086 2.09961 11.3086Z", fillAlpha = 0.85f)
        }
        return _sFFCursive!!
    }

private var _sFFCursive: ImageVector? = null
