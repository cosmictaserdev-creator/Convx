package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFLSquareFill: ImageVector
    get() {
        if (_sFLSquareFill != null) {
            return _sFLSquareFill!!
        }
        _sFLSquareFill = sfIcon(
            name = "Dualtone.SFLSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M9.01367 17.2266C8.44727 17.2266 8.13477 16.8164 8.13477 16.2207L8.13477 6.39648C8.13477 5.80078 8.4375 5.39062 9.01367 5.39062C9.59961 5.39062 9.92188 5.78125 9.92188 6.39648L9.92188 15.7422L15.0098 15.7422C15.4492 15.7422 15.7617 16.0156 15.7617 16.4746C15.7617 16.9336 15.459 17.2266 15.0098 17.2266Z", fillAlpha = 0.85f)
        }
        return _sFLSquareFill!!
    }

private var _sFLSquareFill: ImageVector? = null
