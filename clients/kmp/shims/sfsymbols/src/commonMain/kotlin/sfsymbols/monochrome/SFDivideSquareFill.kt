package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDivideSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFDivideSquareFill: ImageVector
    get() {
        if (_sFDivideSquareFill != null) {
            return _sFDivideSquareFill!!
        }
        _sFDivideSquareFill = sfIcon(
            name = "Monochrome.SFDivideSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM10.0879 16.25C10.0879 16.9727 10.7031 17.5391 11.4648 17.5391C12.2852 17.5391 12.8809 16.9629 12.8809 16.25C12.8809 15.4395 12.2949 14.8633 11.4648 14.8633C10.7031 14.8633 10.0879 15.4395 10.0879 16.25ZM6.2793 10.5664C5.64453 10.5664 5.23438 10.8984 5.23438 11.4941C5.23438 12.0898 5.66406 12.4121 6.2793 12.4121L16.6992 12.4121C17.3145 12.4121 17.7246 12.0898 17.7246 11.4941C17.7246 10.8984 17.334 10.5664 16.6992 10.5664ZM10.0879 6.83594C10.0879 7.54883 10.7031 8.10547 11.4648 8.10547C12.2852 8.10547 12.8809 7.54883 12.8809 6.82617C12.8809 6.01562 12.2949 5.43945 11.4648 5.43945C10.7031 5.43945 10.0879 6.02539 10.0879 6.83594Z", fillAlpha = 0.85f)
        }
        return _sFDivideSquareFill!!
    }

private var _sFDivideSquareFill: ImageVector? = null
