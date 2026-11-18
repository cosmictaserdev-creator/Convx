package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFLSquareFill: ImageVector
    get() {
        if (_sFLSquareFill != null) {
            return _sFLSquareFill!!
        }
        _sFLSquareFill = sfIcon(
            name = "Monochrome.SFLSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM8.13477 6.39648L8.13477 16.2207C8.13477 16.8164 8.44727 17.2266 9.01367 17.2266L15.0098 17.2266C15.459 17.2266 15.7617 16.9336 15.7617 16.4746C15.7617 16.0156 15.4492 15.7422 15.0098 15.7422L9.92188 15.7422L9.92188 6.39648C9.92188 5.78125 9.59961 5.39062 9.01367 5.39062C8.4375 5.39062 8.13477 5.80078 8.13477 6.39648Z", fillAlpha = 0.85f)
        }
        return _sFLSquareFill!!
    }

private var _sFLSquareFill: ImageVector? = null
