package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlag (monochrome)
 * Viewport: 22.9492 x 24.3457
 */
public val SfSymbols.Monochrome.SFFlag: ImageVector
    get() {
        if (_sFFlag != null) {
            return _sFFlag!!
        }
        _sFFlag = sfIcon(
            name = "Monochrome.SFFlag",
            viewportWidth = 22.9492f,
            viewportHeight = 24.3457f
        ) {
            addSfPath("M1.8457 24.3457C2.30469 24.3457 2.66602 23.9746 2.66602 23.5156L2.66602 16.3086C3.02734 16.2012 4.19922 15.7129 6.12305 15.7129C10.7422 15.7129 13.5938 17.9785 18.0078 17.9785C19.9121 17.9785 20.6836 17.7637 21.6016 17.3535C22.4121 16.9824 22.9492 16.3867 22.9492 15.3613L22.9492 2.73438C22.9492 2.09961 22.4414 1.74805 21.7871 1.74805C21.1816 1.74805 20.0195 2.29492 17.8516 2.29492C13.4277 2.29492 10.5762 0.0292969 5.9668 0.0292969C4.0625 0.0292969 3.30078 0.244141 2.38281 0.654297C1.5625 1.02539 1.02539 1.62109 1.02539 2.64648L1.02539 23.5156C1.02539 23.9648 1.40625 24.3457 1.8457 24.3457ZM18.0078 16.3379C13.7793 16.3379 10.8887 14.082 6.12305 14.082C4.80469 14.082 3.57422 14.2285 2.66602 14.5703L2.66602 2.66602C2.86133 2.23633 3.99414 1.66992 5.9668 1.66992C10.3906 1.66992 13.2812 3.92578 17.8516 3.92578C19.1797 3.92578 20.3027 3.7793 21.3184 3.48633L21.3184 15.3418C21.123 15.7715 19.9902 16.3379 18.0078 16.3379Z", fillAlpha = 0.85f)
        }
        return _sFFlag!!
    }

private var _sFFlag: ImageVector? = null
