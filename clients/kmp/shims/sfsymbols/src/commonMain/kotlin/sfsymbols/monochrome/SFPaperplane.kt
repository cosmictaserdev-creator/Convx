package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPaperplane (monochrome)
 * Viewport: 27.7539 x 27.5684
 */
public val SfSymbols.Monochrome.SFPaperplane: ImageVector
    get() {
        if (_sFPaperplane != null) {
            return _sFPaperplane!!
        }
        _sFPaperplane = sfIcon(
            name = "Monochrome.SFPaperplane",
            viewportWidth = 27.7539f,
            viewportHeight = 27.5684f
        ) {
            addSfPath("M15.625 27.5684C16.4551 27.5684 17.041 26.8848 17.4609 25.7715L25.8008 3.98438C25.9961 3.47656 26.1035 3.02734 26.1035 2.65625C26.1035 1.91406 25.6543 1.45508 24.9121 1.45508C24.5312 1.45508 24.0918 1.57227 23.584 1.76758L1.69922 10.1367C0.712891 10.5176 0 11.1035 0 11.9434C0 12.9688 0.761719 13.3398 1.91406 13.6914L11.1621 16.3965L13.8574 25.5664C14.209 26.7578 14.5801 27.5684 15.625 27.5684ZM11.6504 14.7559L2.71484 12.0703C2.40234 11.9727 2.33398 11.9043 2.33398 11.7969C2.33398 11.6797 2.40234 11.582 2.65625 11.4941L20.7715 4.61914C21.6309 4.28711 22.4609 3.86719 23.2324 3.51562C22.5293 4.08203 21.5918 4.81445 21.0254 5.38086ZM15.7812 25.2539C15.6641 25.2539 15.5957 25.1562 15.498 24.8535L12.8027 15.918L22.1875 6.5332C22.7441 5.97656 23.4961 5.0293 24.0625 4.31641C23.7109 5.08789 23.2812 5.92773 22.9492 6.78711L16.0742 24.9023C15.9766 25.166 15.8984 25.2539 15.7812 25.2539Z", fillAlpha = 0.85f)
        }
        return _sFPaperplane!!
    }

private var _sFPaperplane: ImageVector? = null
