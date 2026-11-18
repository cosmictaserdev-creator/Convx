package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMoonStarsCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMoonStarsCircleFill: ImageVector
    get() {
        if (_sFMoonStarsCircleFill != null) {
            return _sFMoonStarsCircleFill!!
        }
        _sFMoonStarsCircleFill = sfIcon(
            name = "Dualtone.SFMoonStarsCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M12.5684 20.2637C8.58398 20.2637 5.50781 17.334 5.50781 13.3398C5.50781 10.4688 7.1875 7.68555 9.73633 6.71875C10.2441 6.52344 10.5664 6.8457 10.3711 7.29492C10.1562 7.91992 9.94141 8.91602 9.94141 9.77539C9.94141 13.4961 12.4023 15.9082 16.1133 15.9082C16.9922 15.9082 17.9004 15.7422 18.4277 15.5469C18.8965 15.3711 19.1797 15.7422 18.9746 16.2207C18.125 18.4766 15.498 20.2637 12.5684 20.2637ZM16.3281 11.8066C16.2012 11.8066 16.1035 11.6992 16.084 11.5625C15.7812 9.28711 15.6934 9.24805 13.291 8.78906C13.1543 8.76953 13.0664 8.68164 13.0664 8.52539C13.0664 8.38867 13.1543 8.30078 13.291 8.28125C15.6934 7.91992 15.7129 7.7832 16.084 5.50781C16.1035 5.36133 16.2012 5.27344 16.3281 5.27344C16.4551 5.27344 16.5625 5.36133 16.5723 5.50781C16.9531 7.75391 17.002 7.82227 19.3652 8.28125C19.502 8.30078 19.5996 8.38867 19.5996 8.52539C19.5996 8.69141 19.502 8.76953 19.3457 8.78906C16.9922 9.15039 16.8652 9.26758 16.5723 11.5625C16.5625 11.6992 16.4551 11.8066 16.3281 11.8066Z", fillAlpha = 0.85f)
        }
        return _sFMoonStarsCircleFill!!
    }

private var _sFMoonStarsCircleFill: ImageVector? = null
