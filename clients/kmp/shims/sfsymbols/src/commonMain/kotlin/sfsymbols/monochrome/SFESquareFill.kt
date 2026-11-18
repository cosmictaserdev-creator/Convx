package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFESquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFESquareFill: ImageVector
    get() {
        if (_sFESquareFill != null) {
            return _sFESquareFill!!
        }
        _sFESquareFill = sfIcon(
            name = "Monochrome.SFESquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM8.47656 5.54688C7.90039 5.54688 7.60742 5.94727 7.60742 6.54297L7.60742 16.2207C7.60742 16.8262 7.90039 17.2266 8.47656 17.2266L14.6191 17.2266C15.0684 17.2266 15.3711 16.9336 15.3711 16.4844C15.3711 16.0352 15.0684 15.752 14.6191 15.752L9.38477 15.752L9.38477 11.9922L14.2969 11.9922C14.7363 11.9922 15.0391 11.7285 15.0391 11.2891C15.0391 10.8301 14.7363 10.5762 14.2969 10.5762L9.38477 10.5762L9.38477 7.02148L14.6191 7.02148C15.0684 7.02148 15.3711 6.71875 15.3711 6.2793C15.3711 5.82031 15.0684 5.54688 14.6191 5.54688Z", fillAlpha = 0.85f)
        }
        return _sFESquareFill!!
    }

private var _sFESquareFill: ImageVector? = null
