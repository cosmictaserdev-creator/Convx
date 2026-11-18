package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCameraMacro (monochrome)
 * Viewport: 28.8118 x 27.041
 */
public val SfSymbols.Monochrome.SFCameraMacro: ImageVector
    get() {
        if (_sFCameraMacro != null) {
            return _sFCameraMacro!!
        }
        _sFCameraMacro = sfIcon(
            name = "Monochrome.SFCameraMacro",
            viewportWidth = 28.8118f,
            viewportHeight = 27.041f
        ) {
            addSfPath("M6.95471 0.859375C6.34925 2.25586 6.05628 3.66211 6.05628 5.0293C6.05628 9.60938 9.29846 13.2617 14.2399 13.2617C19.1813 13.2617 22.4235 9.60938 22.4235 5.0293C22.4235 3.66211 22.1305 2.25586 21.5348 0.859375C21.3004 0.351562 20.7047 0.273438 20.2848 0.703125L17.3453 3.69141L14.8453 0.439453C14.5231 0.0195312 13.9664 0.0195312 13.6344 0.439453L11.1344 3.69141L8.19495 0.703125C7.78479 0.273438 7.17932 0.351562 6.95471 0.859375ZM13.3805 26.2207C13.3805 26.7188 13.7516 27.041 14.2399 27.041C20.6657 27.041 26.4567 23.0273 28.3707 17.4609C28.5953 16.8066 28.3512 16.3965 27.6578 16.3965C21.3688 16.3965 16.6422 20.1562 14.8356 25.0977L15.109 25.4297L15.109 12.1289L13.3805 12.1289ZM11.818 21.8848C9.48401 18.5938 5.568 16.3965 0.792605 16.3965C0.109011 16.3965-0.144895 16.8066 0.0797145 17.4609C1.7594 22.3535 6.40784 26.0547 11.8961 26.8457C11.8571 26.6602 11.818 26.4355 11.818 26.2207Z", fillAlpha = 0.85f)
        }
        return _sFCameraMacro!!
    }

private var _sFCameraMacro: ImageVector? = null
