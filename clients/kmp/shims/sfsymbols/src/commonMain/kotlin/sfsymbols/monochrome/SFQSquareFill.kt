package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFQSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFQSquareFill: ImageVector
    get() {
        if (_sFQSquareFill != null) {
            return _sFQSquareFill!!
        }
        _sFQSquareFill = sfIcon(
            name = "Monochrome.SFQSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM5.98633 11.3672C5.98633 14.9805 8.22266 17.4902 11.4844 17.4902C12.2461 17.4902 12.9492 17.3535 13.584 17.0996L14.5312 18.3887C14.7559 18.6816 14.9609 18.7988 15.2344 18.7988C15.6348 18.7988 15.9082 18.5156 15.9082 18.125C15.9082 17.9297 15.8496 17.7832 15.6348 17.5098L14.8047 16.377C16.1621 15.3223 16.9824 13.5547 16.9824 11.3672C16.9824 7.76367 14.7461 5.26367 11.4844 5.26367C8.22266 5.26367 5.98633 7.76367 5.98633 11.3672ZM15.1953 11.3672C15.1953 12.9883 14.7168 14.2871 13.8477 15.0879L12.9883 13.9258C12.8125 13.6914 12.6367 13.6035 12.373 13.6035C12.002 13.6035 11.6992 13.877 11.6992 14.248C11.6992 14.4336 11.7578 14.6094 11.9238 14.8145L12.6758 15.8691C12.3242 15.9961 11.9336 16.0645 11.4844 16.0645C9.24805 16.0645 7.77344 14.209 7.77344 11.3672C7.77344 8.55469 9.24805 6.69922 11.4844 6.69922C13.7207 6.69922 15.1953 8.55469 15.1953 11.3672Z", fillAlpha = 0.85f)
        }
        return _sFQSquareFill!!
    }

private var _sFQSquareFill: ImageVector? = null
