package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFrancsignSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFFrancsignSquareFill: ImageVector
    get() {
        if (_sFFrancsignSquareFill != null) {
            return _sFFrancsignSquareFill!!
        }
        _sFFrancsignSquareFill = sfIcon(
            name = "Monochrome.SFFrancsignSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM9.375 5.83984C8.93555 5.83984 8.67188 6.10352 8.67188 6.55273L8.67188 14.4629L7.44141 14.4629C7.2168 14.4629 7.05078 14.6387 7.05078 14.8535C7.05078 15.0879 7.2168 15.2441 7.44141 15.2441L8.67188 15.2441L8.67188 16.709C8.67188 17.1191 8.95508 17.4121 9.36523 17.4121C9.75586 17.4121 10.0488 17.1191 10.0488 16.709L10.0488 15.2441L12.8809 15.2441C13.1055 15.2441 13.2715 15.0879 13.2715 14.8535C13.2715 14.6387 13.1055 14.4629 12.8809 14.4629L10.0488 14.4629L10.0488 12.0605L14.4043 12.0605C14.7559 12.0605 15.0195 11.8066 15.0195 11.4746C15.0195 11.1426 14.7559 10.8887 14.4043 10.8887L10.0488 10.8887L10.0488 7.03125L14.8535 7.03125C15.2148 7.03125 15.4785 6.77734 15.4785 6.44531C15.4785 6.09375 15.2148 5.83984 14.8535 5.83984Z", fillAlpha = 0.85f)
        }
        return _sFFrancsignSquareFill!!
    }

private var _sFFrancsignSquareFill: ImageVector? = null
