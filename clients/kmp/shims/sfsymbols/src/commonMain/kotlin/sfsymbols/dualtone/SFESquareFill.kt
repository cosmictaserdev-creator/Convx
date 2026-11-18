package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFESquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFESquareFill: ImageVector
    get() {
        if (_sFESquareFill != null) {
            return _sFESquareFill!!
        }
        _sFESquareFill = sfIcon(
            name = "Dualtone.SFESquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.47656 17.2266C7.90039 17.2266 7.60742 16.8262 7.60742 16.2207L7.60742 6.54297C7.60742 5.94727 7.90039 5.54688 8.47656 5.54688L14.6191 5.54688C15.0684 5.54688 15.3711 5.82031 15.3711 6.2793C15.3711 6.71875 15.0684 7.02148 14.6191 7.02148L9.38477 7.02148L9.38477 10.5762L14.2969 10.5762C14.7363 10.5762 15.0391 10.8301 15.0391 11.2891C15.0391 11.7285 14.7363 11.9922 14.2969 11.9922L9.38477 11.9922L9.38477 15.752L14.6191 15.752C15.0684 15.752 15.3711 16.0352 15.3711 16.4844C15.3711 16.9336 15.0684 17.2266 14.6191 17.2266Z", fillAlpha = 0.85f)
        }
        return _sFESquareFill!!
    }

private var _sFESquareFill: ImageVector? = null
