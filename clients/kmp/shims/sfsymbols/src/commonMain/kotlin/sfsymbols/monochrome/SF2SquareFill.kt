package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF2SquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SF2SquareFill: ImageVector
    get() {
        if (_sF2SquareFill != null) {
            return _sF2SquareFill!!
        }
        _sF2SquareFill = sfIcon(
            name = "Monochrome.SF2SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM7.76367 7.95898C7.70508 8.13477 7.68555 8.28125 7.68555 8.44727C7.68555 8.87695 7.98828 9.16992 8.4375 9.16992C8.84766 9.16992 9.05273 8.92578 9.22852 8.58398C9.52148 7.68555 10.3027 6.82617 11.6211 6.82617C12.9297 6.82617 13.7891 7.58789 13.7891 8.7207C13.7891 9.6582 13.0762 10.459 12.2754 11.3184L8.06641 15.8691C7.87109 16.084 7.76367 16.3086 7.76367 16.543C7.76367 17.002 8.06641 17.2949 8.57422 17.2949L15.1953 17.2949C15.6348 17.2949 15.9473 17.002 15.9473 16.5527C15.9473 16.0938 15.6348 15.8105 15.1953 15.8105L10.2344 15.8105L10.2344 15.7324L13.4668 12.2363C14.6582 10.9668 15.3906 9.96094 15.3906 8.60352C15.3906 6.66992 13.8672 5.39062 11.5527 5.39062C9.73633 5.39062 8.13477 6.49414 7.76367 7.95898Z", fillAlpha = 0.85f)
        }
        return _sF2SquareFill!!
    }

private var _sF2SquareFill: ImageVector? = null
