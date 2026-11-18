package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBellSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFBellSquareFill: ImageVector
    get() {
        if (_sFBellSquareFill != null) {
            return _sFBellSquareFill!!
        }
        _sFBellSquareFill = sfIcon(
            name = "Dualtone.SFBellSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M5.70312 16.1328C5.13672 16.1328 4.79492 15.8301 4.79492 15.3809C4.79492 14.6582 5.50781 14.043 6.11328 13.3984C6.65039 12.8027 6.71875 11.6113 6.80664 10.6055C6.91406 8.05664 7.64648 6.26953 9.49219 5.63477C9.75586 4.6875 10.4785 3.96484 11.4941 3.96484C12.5098 3.96484 13.2324 4.6875 13.4961 5.63477C15.3418 6.26953 16.084 8.05664 16.1816 10.6055C16.2695 11.6113 16.3184 12.8125 16.8848 13.3984C17.5 14.0332 18.1934 14.6582 18.1934 15.3809C18.1934 15.8301 17.8613 16.1328 17.2852 16.1328ZM11.4941 19.043C10.3125 19.043 9.46289 18.2129 9.36523 17.168L13.623 17.168C13.5254 18.2129 12.6758 19.043 11.4941 19.043Z", fillAlpha = 0.85f)
        }
        return _sFBellSquareFill!!
    }

private var _sFBellSquareFill: ImageVector? = null
