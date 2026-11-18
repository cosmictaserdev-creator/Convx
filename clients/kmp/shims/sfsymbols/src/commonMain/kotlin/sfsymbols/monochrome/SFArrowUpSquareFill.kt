package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowUpSquareFill: ImageVector
    get() {
        if (_sFArrowUpSquareFill != null) {
            return _sFArrowUpSquareFill!!
        }
        _sFArrowUpSquareFill = sfIcon(
            name = "Monochrome.SFArrowUpSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM10.8594 5.30273L6.57227 9.46289C6.39648 9.62891 6.30859 9.81445 6.30859 10.0488C6.30859 10.498 6.64062 10.8301 7.09961 10.8301C7.31445 10.8301 7.55859 10.7422 7.71484 10.5566L9.84375 8.33984L10.6639 7.47602L10.6055 9.375L10.6055 17.0703C10.6055 17.5391 11.0156 17.9395 11.4941 17.9395C11.9824 17.9395 12.3828 17.5391 12.3828 17.0703L12.3828 9.375L12.3244 7.47602L13.1445 8.33984L15.2637 10.5566C15.4199 10.7422 15.6543 10.8301 15.8789 10.8301C16.3281 10.8301 16.6797 10.498 16.6797 10.0488C16.6797 9.81445 16.582 9.62891 16.416 9.46289L12.1387 5.30273C11.9141 5.07812 11.7188 5.00977 11.4941 5.00977C11.2695 5.00977 11.0742 5.07812 10.8594 5.30273Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpSquareFill!!
    }

private var _sFArrowUpSquareFill: ImageVector? = null
