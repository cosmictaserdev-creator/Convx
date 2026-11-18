package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBarcode (monochrome)
 * Viewport: 24.6191 x 19.2871
 */
public val SfSymbols.Monochrome.SFBarcode: ImageVector
    get() {
        if (_sFBarcode != null) {
            return _sFBarcode!!
        }
        _sFBarcode = sfIcon(
            name = "Monochrome.SFBarcode",
            viewportWidth = 24.6191f,
            viewportHeight = 19.2871f
        ) {
            addSfPath("M0 19.1992L2.03125 19.1992L2.03125 0L0 0ZM4.01367 19.1992L5.17578 19.1992L5.17578 0L4.01367 0ZM7.11914 19.1992L10.4492 19.1992L10.4492 0L7.11914 0ZM11.6602 19.1992L13.7793 19.1992L13.7793 0L11.6602 0ZM15.0391 19.1992L18.3398 19.1992L18.3398 0L15.0391 0ZM19.3555 19.1992L21.9434 19.1992L21.9434 0L19.3555 0ZM22.5879 19.1992L24.2578 19.1992L24.2578 0L22.5879 0Z", fillAlpha = 0.85f)
        }
        return _sFBarcode!!
    }

private var _sFBarcode: ImageVector? = null
