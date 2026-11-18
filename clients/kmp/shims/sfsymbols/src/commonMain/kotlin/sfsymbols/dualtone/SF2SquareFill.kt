package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF2SquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SF2SquareFill: ImageVector
    get() {
        if (_sF2SquareFill != null) {
            return _sF2SquareFill!!
        }
        _sF2SquareFill = sfIcon(
            name = "Dualtone.SF2SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.57422 17.2949C8.06641 17.2949 7.76367 17.002 7.76367 16.543C7.76367 16.3086 7.87109 16.084 8.06641 15.8691L12.2754 11.3184C13.0762 10.459 13.7891 9.6582 13.7891 8.7207C13.7891 7.58789 12.9297 6.82617 11.6211 6.82617C10.3027 6.82617 9.52148 7.68555 9.22852 8.58398C9.05273 8.92578 8.84766 9.16992 8.4375 9.16992C7.98828 9.16992 7.68555 8.87695 7.68555 8.44727C7.68555 8.28125 7.70508 8.13477 7.76367 7.95898C8.13477 6.49414 9.73633 5.39062 11.5527 5.39062C13.8672 5.39062 15.3906 6.66992 15.3906 8.60352C15.3906 9.96094 14.6582 10.9668 13.4668 12.2363L10.2344 15.7324L10.2344 15.8105L15.1953 15.8105C15.6348 15.8105 15.9473 16.0938 15.9473 16.5527C15.9473 17.002 15.6348 17.2949 15.1953 17.2949Z", fillAlpha = 0.85f)
        }
        return _sF2SquareFill!!
    }

private var _sF2SquareFill: ImageVector? = null
