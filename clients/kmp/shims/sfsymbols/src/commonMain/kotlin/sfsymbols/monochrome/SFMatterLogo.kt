package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMatterLogo (monochrome)
 * Viewport: 29.1797 x 28.1934
 */
public val SfSymbols.Monochrome.SFMatterLogo: ImageVector
    get() {
        if (_sFMatterLogo != null) {
            return _sFMatterLogo!!
        }
        _sFMatterLogo = sfIcon(
            name = "Monochrome.SFMatterLogo",
            viewportWidth = 29.1797f,
            viewportHeight = 28.1934f
        ) {
            addSfPath("M8.54492 7.23633C9.72656 8.18359 11.1816 8.87695 12.7344 9.15039L12.7344 0.947266L14.4141 0L16.084 0.947266L16.084 9.15039C17.6465 8.87695 19.0918 8.18359 20.2637 7.23633L23.3105 8.97461C21.0254 11.25 17.8711 12.6465 14.4141 12.6465C10.9375 12.6465 7.80273 11.25 5.49805 8.97461ZM12.2363 28.1641C13.0371 25.0488 12.6855 21.6309 10.957 18.623C9.21875 15.6152 6.43555 13.6035 3.34961 12.7539L3.34961 16.2793C4.76562 16.8066 6.06445 17.7051 7.09961 18.9258L0 23.0273L0 24.9414L1.66992 25.9082L8.76953 21.8164C9.29688 23.3105 9.44336 24.8926 9.19922 26.4062ZM25.4785 12.7539C22.3926 13.6035 19.5898 15.6152 17.8711 18.623C16.1328 21.6309 15.7617 25.0488 16.5918 28.1641L19.6191 26.4062C19.3848 24.8926 19.5117 23.3105 20.0586 21.8164L27.1484 25.9082L28.8184 24.9414L28.8184 23.0273L21.7188 18.9258C22.7637 17.7051 24.0625 16.8066 25.4785 16.2793Z", fillAlpha = 0.85f)
        }
        return _sFMatterLogo!!
    }

private var _sFMatterLogo: ImageVector? = null
