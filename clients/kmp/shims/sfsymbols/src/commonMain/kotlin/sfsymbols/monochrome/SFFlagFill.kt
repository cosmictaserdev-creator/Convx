package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlagFill (monochrome)
 * Viewport: 22.9492 x 24.3457
 */
public val SfSymbols.Monochrome.SFFlagFill: ImageVector
    get() {
        if (_sFFlagFill != null) {
            return _sFFlagFill!!
        }
        _sFFlagFill = sfIcon(
            name = "Monochrome.SFFlagFill",
            viewportWidth = 22.9492f,
            viewportHeight = 24.3457f
        ) {
            addSfPath("M1.8457 24.3457C2.30469 24.3457 2.66602 23.9746 2.66602 23.5156L2.66602 16.3086C3.02734 16.2012 4.19922 15.7129 6.12305 15.7129C10.7422 15.7129 13.5938 17.9785 18.0078 17.9785C19.9121 17.9785 20.6836 17.7637 21.6016 17.3535C22.4121 16.9824 22.9492 16.3867 22.9492 15.3613L22.9492 2.73438C22.9492 2.09961 22.4414 1.74805 21.7871 1.74805C21.1816 1.74805 20.0195 2.29492 17.8516 2.29492C13.4277 2.29492 10.5762 0.0292969 5.9668 0.0292969C4.0625 0.0292969 3.30078 0.244141 2.38281 0.654297C1.5625 1.02539 1.02539 1.62109 1.02539 2.64648L1.02539 23.5156C1.02539 23.9648 1.40625 24.3457 1.8457 24.3457Z", fillAlpha = 0.85f)
        }
        return _sFFlagFill!!
    }

private var _sFFlagFill: ImageVector? = null
