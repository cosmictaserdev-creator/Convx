package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF11SquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SF11SquareFill: ImageVector
    get() {
        if (_sF11SquareFill != null) {
            return _sF11SquareFill!!
        }
        _sF11SquareFill = sfIcon(
            name = "Monochrome.SF11SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM7.73438 6.5625L6.23047 7.76367C6.02539 7.91992 5.87891 8.13477 5.87891 8.42773C5.87891 8.78906 6.12305 9.04297 6.47461 9.04297C6.65039 9.04297 6.76758 8.98438 6.875 8.88672L8.16406 7.86133L8.16406 15.9863C8.16406 16.4551 8.54492 16.8262 8.99414 16.8262C9.44336 16.8262 9.82422 16.4551 9.82422 15.9863L9.82422 7.02148C9.82422 6.44531 9.45312 6.09375 8.87695 6.09375C8.39844 6.09375 8.00781 6.35742 7.73438 6.5625ZM13.4668 6.5625L11.9629 7.76367C11.7676 7.91992 11.6113 8.13477 11.6113 8.42773C11.6113 8.78906 11.8457 9.04297 12.1973 9.04297C12.373 9.04297 12.4902 8.98438 12.6074 8.88672L13.8965 7.86133L13.8965 15.9863C13.8965 16.4551 14.2676 16.8262 14.7266 16.8262C15.1758 16.8262 15.5469 16.4551 15.5469 15.9863L15.5469 7.02148C15.5469 6.44531 15.1855 6.09375 14.6094 6.09375C14.1211 6.09375 13.7402 6.35742 13.4668 6.5625Z", fillAlpha = 0.85f)
        }
        return _sF11SquareFill!!
    }

private var _sF11SquareFill: ImageVector? = null
