package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMoonStarsCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFMoonStarsCircleFill: ImageVector
    get() {
        if (_sFMoonStarsCircleFill != null) {
            return _sFMoonStarsCircleFill!!
        }
        _sFMoonStarsCircleFill = sfIcon(
            name = "Monochrome.SFMoonStarsCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM9.73633 6.71875C7.1875 7.68555 5.50781 10.4688 5.50781 13.3398C5.50781 17.334 8.58398 20.2637 12.5684 20.2637C15.498 20.2637 18.125 18.4766 18.9746 16.2207C19.1797 15.7422 18.8965 15.3711 18.4277 15.5469C17.9004 15.7422 16.9922 15.9082 16.1133 15.9082C12.4023 15.9082 9.94141 13.4961 9.94141 9.77539C9.94141 8.91602 10.1562 7.91992 10.3711 7.29492C10.5664 6.8457 10.2441 6.52344 9.73633 6.71875ZM16.084 5.50781C15.7129 7.7832 15.6934 7.91992 13.291 8.28125C13.1543 8.30078 13.0664 8.38867 13.0664 8.52539C13.0664 8.68164 13.1543 8.76953 13.291 8.78906C15.6934 9.24805 15.7812 9.28711 16.084 11.5625C16.1035 11.6992 16.2012 11.8066 16.3281 11.8066C16.4551 11.8066 16.5625 11.6992 16.5723 11.5625C16.8652 9.26758 16.9922 9.15039 19.3457 8.78906C19.502 8.76953 19.5996 8.69141 19.5996 8.52539C19.5996 8.38867 19.502 8.30078 19.3652 8.28125C17.002 7.82227 16.9531 7.75391 16.5723 5.50781C16.5625 5.36133 16.4551 5.27344 16.3281 5.27344C16.2012 5.27344 16.1035 5.36133 16.084 5.50781Z", fillAlpha = 0.85f)
        }
        return _sFMoonStarsCircleFill!!
    }

private var _sFMoonStarsCircleFill: ImageVector? = null
