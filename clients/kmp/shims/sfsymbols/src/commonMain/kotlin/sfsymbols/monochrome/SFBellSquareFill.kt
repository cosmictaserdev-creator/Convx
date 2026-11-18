package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBellSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFBellSquareFill: ImageVector
    get() {
        if (_sFBellSquareFill != null) {
            return _sFBellSquareFill!!
        }
        _sFBellSquareFill = sfIcon(
            name = "Monochrome.SFBellSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM9.36523 17.168C9.46289 18.2129 10.3125 19.043 11.4941 19.043C12.6758 19.043 13.5254 18.2129 13.623 17.168ZM9.49219 5.63477C7.64648 6.26953 6.91406 8.05664 6.80664 10.6055C6.71875 11.6113 6.65039 12.8027 6.11328 13.3984C5.50781 14.043 4.79492 14.6582 4.79492 15.3809C4.79492 15.8301 5.13672 16.1328 5.70312 16.1328L17.2852 16.1328C17.8613 16.1328 18.1934 15.8301 18.1934 15.3809C18.1934 14.6582 17.5 14.0332 16.8848 13.3984C16.3184 12.8125 16.2695 11.6113 16.1816 10.6055C16.084 8.05664 15.3418 6.26953 13.4961 5.63477C13.2324 4.6875 12.5098 3.96484 11.4941 3.96484C10.4785 3.96484 9.75586 4.6875 9.49219 5.63477Z", fillAlpha = 0.85f)
        }
        return _sFBellSquareFill!!
    }

private var _sFBellSquareFill: ImageVector? = null
