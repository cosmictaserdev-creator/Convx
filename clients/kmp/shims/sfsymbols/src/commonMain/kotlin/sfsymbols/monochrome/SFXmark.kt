package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFXmark (monochrome)
 * Viewport: 20.1197 x 19.7779
 */
public val SfSymbols.Monochrome.SFXmark: ImageVector
    get() {
        if (_sFXmark != null) {
            return _sFXmark!!
        }
        _sFXmark = sfIcon(
            name = "Monochrome.SFXmark",
            viewportWidth = 20.1197f,
            viewportHeight = 19.7779f
        ) {
            addSfPath("M18.1262 0.303967L0.27467 18.1555C-0.0866577 18.5169-0.0964233 19.1321 0.27467 19.5032C0.65553 19.8645 1.261 19.8645 1.63209 19.5032L19.4739 1.65162C19.845 1.2903 19.8547 0.675061 19.4739 0.303967C19.1028-0.0573608 18.4973-0.0671265 18.1262 0.303967ZM19.4739 18.1555L1.63209 0.303967C1.261-0.0573608 0.645764-0.0671265 0.27467 0.303967C-0.0866577 0.684827-0.0866577 1.2903 0.27467 1.65162L18.1262 19.5032C18.4876 19.8645 19.1126 19.8743 19.4739 19.5032C19.845 19.1223 19.845 18.5169 19.4739 18.1555Z", fillAlpha = 0.85f)
        }
        return _sFXmark!!
    }

private var _sFXmark: ImageVector? = null
