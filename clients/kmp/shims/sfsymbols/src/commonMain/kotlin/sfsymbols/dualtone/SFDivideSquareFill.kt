package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDivideSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFDivideSquareFill: ImageVector
    get() {
        if (_sFDivideSquareFill != null) {
            return _sFDivideSquareFill!!
        }
        _sFDivideSquareFill = sfIcon(
            name = "Dualtone.SFDivideSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.4648 8.10547C10.7031 8.10547 10.0879 7.54883 10.0879 6.83594C10.0879 6.02539 10.7031 5.43945 11.4648 5.43945C12.2949 5.43945 12.8809 6.01562 12.8809 6.82617C12.8809 7.54883 12.2852 8.10547 11.4648 8.10547ZM6.2793 12.4121C5.66406 12.4121 5.23438 12.0898 5.23438 11.4941C5.23438 10.8984 5.64453 10.5664 6.2793 10.5664L16.6992 10.5664C17.334 10.5664 17.7246 10.8984 17.7246 11.4941C17.7246 12.0898 17.3145 12.4121 16.6992 12.4121ZM11.4648 17.5391C10.7031 17.5391 10.0879 16.9727 10.0879 16.25C10.0879 15.4395 10.7031 14.8633 11.4648 14.8633C12.2949 14.8633 12.8809 15.4395 12.8809 16.25C12.8809 16.9629 12.2852 17.5391 11.4648 17.5391Z", fillAlpha = 0.85f)
        }
        return _sFDivideSquareFill!!
    }

private var _sFDivideSquareFill: ImageVector? = null
