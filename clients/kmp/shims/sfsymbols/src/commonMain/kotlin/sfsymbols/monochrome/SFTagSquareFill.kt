package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTagSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFTagSquareFill: ImageVector
    get() {
        if (_sFTagSquareFill != null) {
            return _sFTagSquareFill!!
        }
        _sFTagSquareFill = sfIcon(
            name = "Monochrome.SFTagSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM12.8223 4.88281C12.0898 4.88281 11.748 5.21484 11.2695 5.69336L4.99023 12.041C4.25781 12.7734 4.26758 13.4766 4.9707 14.1797L8.97461 18.2031C9.6875 18.9062 10.4004 18.916 11.1328 18.1934L17.4902 11.8848C17.959 11.416 18.3008 11.0938 18.3008 10.3223L18.3008 7.88086C18.3008 7.30469 18.1543 6.99219 17.7734 6.61133L16.6211 5.48828C16.2402 5.11719 15.918 4.88281 15.3613 4.88281ZM15.127 8.02734C15.4785 8.36914 15.4688 8.92578 15.127 9.26758C14.7852 9.61914 14.2285 9.61914 13.8965 9.27734C13.5449 8.94531 13.5449 8.38867 13.8965 8.02734C14.2285 7.68555 14.7852 7.68555 15.127 8.02734Z", fillAlpha = 0.85f)
        }
        return _sFTagSquareFill!!
    }

private var _sFTagSquareFill: ImageVector? = null
