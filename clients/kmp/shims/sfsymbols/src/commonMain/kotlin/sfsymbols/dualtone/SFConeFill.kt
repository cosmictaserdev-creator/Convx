package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFConeFill (dualtone)
 * Viewport: 25.4395 x 27.3535
 */
public val SfSymbols.Dualtone.SFConeFill: ImageVector
    get() {
        if (_sFConeFill != null) {
            return _sFConeFill!!
        }
        _sFConeFill = sfIcon(
            name = "Dualtone.SFConeFill",
            viewportWidth = 25.4395f,
            viewportHeight = 27.3535f
        ) {
            addSfPath("M0 22.3047C0 25.1855 5.2832 27.3535 12.5391 27.3535C19.7949 27.3535 25.0781 25.1855 25.0781 22.3047C25.0781 21.9141 24.9121 21.377 24.6484 20.8887L13.6719 1.03516C13.3594 0.46875 12.9395 0.322266 12.5391 0.322266C12.1387 0.322266 11.7285 0.46875 11.416 1.03516L0.429688 20.8887C0.166016 21.377 0 21.9141 0 22.3047Z", fillAlpha = 0.85f)
        }
        return _sFConeFill!!
    }

private var _sFConeFill: ImageVector? = null
