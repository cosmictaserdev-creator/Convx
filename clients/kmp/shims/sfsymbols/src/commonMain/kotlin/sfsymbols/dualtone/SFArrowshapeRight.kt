package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeRight (dualtone)
 * Viewport: 27.5098 x 22.8223
 */
public val SfSymbols.Dualtone.SFArrowshapeRight: ImageVector
    get() {
        if (_sFArrowshapeRight != null) {
            return _sFArrowshapeRight!!
        }
        _sFArrowshapeRight = sfIcon(
            name = "Dualtone.SFArrowshapeRight",
            viewportWidth = 27.5098f,
            viewportHeight = 22.8223f
        ) {
            addSfPath("M27.5098 11.4062C27.5098 10.9961 27.3633 10.5566 26.8457 10.0586L16.9141 0.791016C16.3379 0.253906 15.9473 0 15.3906 0C14.5898 0 14.043 0.634766 14.043 1.40625L14.043 6.33789L3.64258 6.33789C1.74805 6.33789 0.673828 7.38281 0.673828 9.22852L0.673828 13.6133C0.673828 15.459 1.74805 16.5137 3.64258 16.5137L14.043 16.5137L14.043 21.4648C14.043 22.2363 14.5898 22.8027 15.3711 22.8027C15.9277 22.8027 16.3867 22.5684 16.9141 22.0703L26.8457 12.7344C27.3633 12.2461 27.5098 11.7969 27.5098 11.4062ZM25.5859 11.4062C25.5859 11.5137 25.5371 11.6113 25.3809 11.7578L16.2109 20.4004C16.1133 20.4883 16.0352 20.5176 15.957 20.5176C15.8496 20.5176 15.7715 20.4395 15.7715 20.3223L15.7715 15.2832C15.7715 15 15.6543 14.8828 15.3711 14.8828L3.66211 14.8828C2.79297 14.8828 2.30469 14.4043 2.30469 13.5352L2.30469 9.30664C2.30469 8.44727 2.79297 7.96875 3.66211 7.96875L15.3711 7.96875C15.6543 7.96875 15.7715 7.8418 15.7715 7.56836L15.7715 2.39258C15.7715 2.28516 15.8496 2.20703 15.9668 2.20703C16.0645 2.20703 16.1426 2.26562 16.2109 2.32422L25.3809 11.0547C25.5371 11.1914 25.5859 11.2988 25.5859 11.4062Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeRight!!
    }

private var _sFArrowshapeRight: ImageVector? = null
