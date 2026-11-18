package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFYieldsign (monochrome)
 * Viewport: 26.6504 x 24.0723
 */
public val SfSymbols.Monochrome.SFYieldsign: ImageVector
    get() {
        if (_sFYieldsign != null) {
            return _sFYieldsign!!
        }
        _sFYieldsign = sfIcon(
            name = "Monochrome.SFYieldsign",
            viewportWidth = 26.6504f,
            viewportHeight = 24.0723f
        ) {
            addSfPath("M3.26172 0C1.23047 0 0 1.43555 0 3.24219C0 3.81836 0.15625 4.42383 0.488281 4.99023L10.3613 22.2559C10.9766 23.3398 12.0508 23.8672 13.1445 23.8672C14.2285 23.8672 15.3125 23.3398 15.9277 22.2559L25.8008 4.99023C26.123 4.43359 26.2891 3.81836 26.2891 3.24219C26.2891 1.43555 25.0586 0 23.0176 0ZM3.28125 1.69922L22.9883 1.69922C23.9551 1.69922 24.541 2.43164 24.541 3.25195C24.541 3.54492 24.4629 3.86719 24.2969 4.17969L14.4336 21.4355C14.1504 21.9531 13.6426 22.1582 13.1445 22.1582C12.6465 22.1582 12.1289 21.9531 11.8359 21.4355L1.98242 4.18945C1.81641 3.87695 1.73828 3.54492 1.73828 3.25195C1.73828 2.43164 2.31445 1.69922 3.28125 1.69922Z", fillAlpha = 0.85f)
            addSfPath("M4.81445 3.50586C4.28711 3.50586 3.97461 4.00391 4.25781 4.49219L12.5781 19.0723C12.8418 19.5605 13.4277 19.5605 13.7109 19.0723L22.0215 4.49219C22.2949 4.01367 21.9922 3.50586 21.4648 3.50586Z", fillAlpha = 0.85f)
        }
        return _sFYieldsign!!
    }

private var _sFYieldsign: ImageVector? = null
