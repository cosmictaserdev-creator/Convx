package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowDownSquareFill: ImageVector
    get() {
        if (_sFArrowDownSquareFill != null) {
            return _sFArrowDownSquareFill!!
        }
        _sFArrowDownSquareFill = sfIcon(
            name = "Monochrome.SFArrowDownSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM10.6055 5.86914L10.6055 13.5742L10.6636 15.4631L9.84375 14.5996L7.71484 12.3828C7.55859 12.1973 7.31445 12.1094 7.09961 12.1094C6.64062 12.1094 6.30859 12.4414 6.30859 12.8906C6.30859 13.125 6.39648 13.3105 6.57227 13.4766L10.8594 17.6367C11.0742 17.8613 11.2695 17.9395 11.4941 17.9395C11.7188 17.9395 11.9141 17.8613 12.1387 17.6367L16.416 13.4766C16.582 13.3105 16.6797 13.125 16.6797 12.8906C16.6797 12.4414 16.3281 12.1094 15.8789 12.1094C15.6543 12.1094 15.4199 12.1973 15.2637 12.3828L13.1445 14.5996L12.3247 15.4631L12.3828 13.5742L12.3828 5.86914C12.3828 5.40039 11.9824 5.00977 11.4941 5.00977C11.0156 5.00977 10.6055 5.40039 10.6055 5.86914Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownSquareFill!!
    }

private var _sFArrowDownSquareFill: ImageVector? = null
