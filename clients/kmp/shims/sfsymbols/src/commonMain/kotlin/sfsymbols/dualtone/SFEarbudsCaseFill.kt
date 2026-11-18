package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEarbudsCaseFill (dualtone)
 * Viewport: 30.0488 x 19.7559
 */
public val SfSymbols.Dualtone.SFEarbudsCaseFill: ImageVector
    get() {
        if (_sFEarbudsCaseFill != null) {
            return _sFEarbudsCaseFill!!
        }
        _sFEarbudsCaseFill = sfIcon(
            name = "Dualtone.SFEarbudsCaseFill",
            viewportWidth = 30.0488f,
            viewportHeight = 19.7559f
        ) {
            addSfPath("M29.6875 14.4434L29.6875 8.29102L0 8.29102L0 14.4434C0 17.8516 2.05078 19.7559 5.73242 19.7559L23.9648 19.7559C27.6367 19.7559 29.6875 17.8516 29.6875 14.4434ZM12.4512 12.5195C11.9141 12.5195 11.4844 12.1191 11.4844 11.5527C11.4844 10.9961 11.9141 10.5957 12.4512 10.5957L17.1387 10.5957C17.7051 10.5957 18.1055 10.9961 18.1055 11.5527C18.1055 12.1191 17.7051 12.5195 17.1387 12.5195ZM0 7.1582L29.6875 7.1582L29.6875 6.75781C29.6875 2.24609 26.9824 0 21.9824 0L7.72461 0C2.73438 0 0 2.24609 0 6.75781Z", fillAlpha = 0.85f)
        }
        return _sFEarbudsCaseFill!!
    }

private var _sFEarbudsCaseFill: ImageVector? = null
