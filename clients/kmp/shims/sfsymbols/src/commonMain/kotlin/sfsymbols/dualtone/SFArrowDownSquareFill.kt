package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFArrowDownSquareFill: ImageVector
    get() {
        if (_sFArrowDownSquareFill != null) {
            return _sFArrowDownSquareFill!!
        }
        _sFArrowDownSquareFill = sfIcon(
            name = "Dualtone.SFArrowDownSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.4941 5.00977C11.0156 5.00977 10.6055 5.40039 10.6055 5.86914L10.6055 13.5742L10.6836 16.1133C10.7031 16.6113 11.0938 16.8945 11.4941 16.8945C11.8945 16.8945 12.2949 16.6113 12.3047 16.1133L12.3828 13.5742L12.3828 5.86914C12.3828 5.40039 11.9824 5.00977 11.4941 5.00977ZM11.4941 17.9395C11.7188 17.9395 11.9141 17.8613 12.1387 17.6367L16.416 13.4766C16.582 13.3105 16.6797 13.125 16.6797 12.8906C16.6797 12.4414 16.3281 12.1094 15.8789 12.1094C15.6543 12.1094 15.4199 12.1973 15.2637 12.3828L13.1445 14.5996L11.4941 16.3379L11.4941 16.3379L9.84375 14.5996L7.71484 12.3828C7.55859 12.1973 7.31445 12.1094 7.09961 12.1094C6.64062 12.1094 6.30859 12.4414 6.30859 12.8906C6.30859 13.125 6.39648 13.3105 6.57227 13.4766L10.8594 17.6367C11.0742 17.8613 11.2695 17.9395 11.4941 17.9395Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownSquareFill!!
    }

private var _sFArrowDownSquareFill: ImageVector? = null
