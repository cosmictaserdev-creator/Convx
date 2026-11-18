package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTagSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFTagSquareFill: ImageVector
    get() {
        if (_sFTagSquareFill != null) {
            return _sFTagSquareFill!!
        }
        _sFTagSquareFill = sfIcon(
            name = "Dualtone.SFTagSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.97461 18.2031L4.9707 14.1797C4.26758 13.4766 4.25781 12.7734 4.99023 12.041L11.2695 5.69336C11.748 5.21484 12.0898 4.88281 12.8223 4.88281L15.3613 4.88281C15.918 4.88281 16.2402 5.11719 16.6211 5.48828L17.7734 6.61133C18.1543 6.99219 18.3008 7.30469 18.3008 7.88086L18.3008 10.3223C18.3008 11.0938 17.959 11.416 17.4902 11.8848L11.1328 18.1934C10.4004 18.916 9.6875 18.9062 8.97461 18.2031ZM13.8965 9.27734C14.2285 9.61914 14.7852 9.61914 15.127 9.26758C15.4688 8.92578 15.4785 8.36914 15.127 8.02734C14.7852 7.68555 14.2285 7.68555 13.8965 8.02734C13.5449 8.38867 13.5449 8.94531 13.8965 9.27734Z", fillAlpha = 0.85f)
        }
        return _sFTagSquareFill!!
    }

private var _sFTagSquareFill: ImageVector? = null
