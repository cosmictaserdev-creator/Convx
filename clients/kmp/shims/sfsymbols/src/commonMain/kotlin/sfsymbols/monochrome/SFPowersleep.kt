package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPowersleep (monochrome)
 * Viewport: 23.252 x 25.4199
 */
public val SfSymbols.Monochrome.SFPowersleep: ImageVector
    get() {
        if (_sFPowersleep != null) {
            return _sFPowersleep!!
        }
        _sFPowersleep = sfIcon(
            name = "Monochrome.SFPowersleep",
            viewportWidth = 23.252f,
            viewportHeight = 25.4199f
        ) {
            addSfPath("M5.81055 7.88086C5.81055 5.77148 6.31836 3.75977 7.26562 2.02148C7.48047 1.65039 7.16797 1.2793 6.70898 1.52344C2.70508 3.65234 0 7.90039 0 12.7051C0 19.7266 5.69336 25.4199 12.7148 25.4199C16.8066 25.4199 20.498 23.4473 22.793 20.3809C23.0078 20.127 22.8809 19.7559 22.4414 19.8828C21.2207 20.3418 19.9023 20.5957 18.5352 20.5957C11.5723 20.5957 5.81055 14.834 5.81055 7.88086Z", fillAlpha = 0.85f)
        }
        return _sFPowersleep!!
    }

private var _sFPowersleep: ImageVector? = null
