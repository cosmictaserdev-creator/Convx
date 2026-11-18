package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFComputermouseFill (dualtone)
 * Viewport: 17.6758 x 27.1191
 */
public val SfSymbols.Dualtone.SFComputermouseFill: ImageVector
    get() {
        if (_sFComputermouseFill != null) {
            return _sFComputermouseFill!!
        }
        _sFComputermouseFill = sfIcon(
            name = "Dualtone.SFComputermouseFill",
            viewportWidth = 17.6758f,
            viewportHeight = 27.1191f
        ) {
            addSfPath("M0.341797 7.7832L7.91992 7.7832L7.91992 0.0488281C2.97852 0.244141 0.859375 2.44141 0.341797 7.7832ZM9.39453 7.7832L16.9629 7.7832C16.4453 2.49023 14.375 0.244141 9.39453 0.0488281ZM8.65234 27.1191C14.3457 27.1191 17.3145 23.75 17.3145 17.3242C17.3145 14.2676 17.2266 11.4844 17.0703 9.26758L0.234375 9.26758C0.0878906 11.4844 0 14.2676 0 17.3242C0 23.75 2.96875 27.1191 8.65234 27.1191Z", fillAlpha = 0.85f)
        }
        return _sFComputermouseFill!!
    }

private var _sFComputermouseFill: ImageVector? = null
