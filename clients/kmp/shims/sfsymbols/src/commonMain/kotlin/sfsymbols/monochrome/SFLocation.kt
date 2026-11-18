package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLocation (monochrome)
 * Viewport: 25.9205 x 23.7682
 */
public val SfSymbols.Monochrome.SFLocation: ImageVector
    get() {
        if (_sFLocation != null) {
            return _sFLocation!!
        }
        _sFLocation = sfIcon(
            name = "Monochrome.SFLocation",
            viewportWidth = 25.9205f,
            viewportHeight = 23.7682f
        ) {
            addSfPath("M0.938787 9.82841C-0.58465 10.5315-0.154963 12.5628 1.51496 12.5726L11.0657 12.6116C11.2025 12.6116 11.2415 12.6604 11.2415 12.7972L11.2708 22.2796C11.2806 24.0179 13.3607 24.3108 14.0931 22.7483L23.6146 2.33818C24.3763 0.678023 23.0775-0.474321 21.4564 0.287398ZM2.75519 10.9124C2.68683 10.9124 2.67707 10.8343 2.73566 10.8147L21.6517 2.09404C21.7591 2.04521 21.8275 2.09404 21.7689 2.21123L13.0091 21.0979C12.9798 21.1565 12.9212 21.1468 12.9212 21.0882L12.9603 11.7132C12.9603 11.2151 12.6282 10.8636 12.0911 10.8636Z", fillAlpha = 0.85f)
        }
        return _sFLocation!!
    }

private var _sFLocation: ImageVector? = null
