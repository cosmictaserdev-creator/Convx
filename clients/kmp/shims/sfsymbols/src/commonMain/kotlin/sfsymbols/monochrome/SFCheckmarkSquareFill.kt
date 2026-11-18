package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFCheckmarkSquareFill: ImageVector
    get() {
        if (_sFCheckmarkSquareFill != null) {
            return _sFCheckmarkSquareFill!!
        }
        _sFCheckmarkSquareFill = sfIcon(
            name = "Monochrome.SFCheckmarkSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM15.6934 6.45508L10.0488 15.4297L7.23633 11.9141C6.97266 11.5723 6.72852 11.4648 6.42578 11.4648C5.9375 11.4648 5.55664 11.8652 5.55664 12.3535C5.55664 12.5977 5.6543 12.8418 5.82031 13.0566L9.15039 17.0801C9.43359 17.4414 9.7168 17.5977 10.0879 17.5977C10.459 17.5977 10.7617 17.4219 10.9863 17.0801L17.0996 7.48047C17.2363 7.27539 17.3633 7.02148 17.3633 6.78711C17.3633 6.28906 16.9238 5.95703 16.4648 5.95703C16.1719 5.95703 15.8887 6.13281 15.6934 6.45508Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkSquareFill!!
    }

private var _sFCheckmarkSquareFill: ImageVector? = null
