package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCheckmarkSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFCheckmarkSquareFill: ImageVector
    get() {
        if (_sFCheckmarkSquareFill != null) {
            return _sFCheckmarkSquareFill!!
        }
        _sFCheckmarkSquareFill = sfIcon(
            name = "Dualtone.SFCheckmarkSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M10.0879 17.5977C9.7168 17.5977 9.43359 17.4414 9.15039 17.0801L5.82031 13.0566C5.6543 12.8418 5.55664 12.5977 5.55664 12.3535C5.55664 11.8652 5.9375 11.4648 6.42578 11.4648C6.72852 11.4648 6.97266 11.5723 7.23633 11.9141L10.0488 15.4297L15.6934 6.45508C15.8887 6.13281 16.1719 5.95703 16.4648 5.95703C16.9238 5.95703 17.3633 6.28906 17.3633 6.78711C17.3633 7.02148 17.2363 7.27539 17.0996 7.48047L10.9863 17.0801C10.7617 17.4219 10.459 17.5977 10.0879 17.5977Z", fillAlpha = 0.85f)
        }
        return _sFCheckmarkSquareFill!!
    }

private var _sFCheckmarkSquareFill: ImageVector? = null
