package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPiSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFPiSquareFill: ImageVector
    get() {
        if (_sFPiSquareFill != null) {
            return _sFPiSquareFill!!
        }
        _sFPiSquareFill = sfIcon(
            name = "Monochrome.SFPiSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM5.72266 5.74219C5.26367 5.74219 4.95117 6.04492 4.95117 6.51367C4.95117 6.97266 5.27344 7.27539 5.72266 7.27539L7.39258 7.27539L7.39258 16.709C7.39258 17.1973 7.74414 17.5098 8.23242 17.5098C8.7207 17.5098 9.0625 17.1973 9.0625 16.709L9.0625 7.27539L13.75 7.27539L13.75 14.8242C13.75 16.543 14.4922 17.4609 16.1035 17.4609C17.0312 17.4609 17.5586 17.1582 17.5586 16.5918C17.5586 16.1328 17.2949 15.8887 16.7676 15.8887C16.6309 15.8887 16.4648 15.918 16.3086 15.918C15.7031 15.918 15.4102 15.5859 15.4102 14.7754L15.4102 7.27539L17.1387 7.27539C17.5684 7.27539 17.9004 6.97266 17.9004 6.51367C17.9004 6.04492 17.5781 5.74219 17.1387 5.74219Z", fillAlpha = 0.85f)
        }
        return _sFPiSquareFill!!
    }

private var _sFPiSquareFill: ImageVector? = null
