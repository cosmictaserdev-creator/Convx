package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPiSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFPiSquareFill: ImageVector
    get() {
        if (_sFPiSquareFill != null) {
            return _sFPiSquareFill!!
        }
        _sFPiSquareFill = sfIcon(
            name = "Dualtone.SFPiSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.23242 17.5098C7.74414 17.5098 7.39258 17.1973 7.39258 16.709L7.39258 7.27539L5.72266 7.27539C5.27344 7.27539 4.95117 6.97266 4.95117 6.51367C4.95117 6.04492 5.26367 5.74219 5.72266 5.74219L17.1387 5.74219C17.5781 5.74219 17.9004 6.04492 17.9004 6.51367C17.9004 6.97266 17.5684 7.27539 17.1387 7.27539L15.4102 7.27539L15.4102 14.7754C15.4102 15.5859 15.7031 15.918 16.3086 15.918C16.4648 15.918 16.6309 15.8887 16.7676 15.8887C17.2949 15.8887 17.5586 16.1328 17.5586 16.5918C17.5586 17.1582 17.0312 17.4609 16.1035 17.4609C14.4922 17.4609 13.75 16.543 13.75 14.8242L13.75 7.27539L9.0625 7.27539L9.0625 16.709C9.0625 17.1973 8.7207 17.5098 8.23242 17.5098Z", fillAlpha = 0.85f)
        }
        return _sFPiSquareFill!!
    }

private var _sFPiSquareFill: ImageVector? = null
