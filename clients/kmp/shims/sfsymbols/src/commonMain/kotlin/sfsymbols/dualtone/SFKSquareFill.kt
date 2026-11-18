package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFKSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFKSquareFill: ImageVector
    get() {
        if (_sFKSquareFill != null) {
            return _sFKSquareFill!!
        }
        _sFKSquareFill = sfIcon(
            name = "Dualtone.SFKSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.11523 17.3633C7.56836 17.3633 7.23633 16.9922 7.23633 16.3672L7.23633 6.40625C7.23633 5.77148 7.56836 5.40039 8.11523 5.40039C8.69141 5.40039 9.01367 5.77148 9.01367 6.40625L9.01367 11.123L9.08203 11.123L14.3652 5.76172C14.6191 5.50781 14.8145 5.40039 15.0977 5.40039C15.5566 5.40039 15.918 5.74219 15.918 6.17188C15.918 6.41602 15.8398 6.60156 15.6348 6.80664L11.9336 10.5957L15.9668 15.8203C16.1133 16.0449 16.2305 16.2793 16.2305 16.5137C16.2305 17.0117 15.8691 17.3535 15.3613 17.3535C15.0391 17.3535 14.8047 17.2168 14.5605 16.9043L10.6348 11.7578L9.01367 13.418L9.01367 16.3672C9.01367 16.9922 8.69141 17.3633 8.11523 17.3633Z", fillAlpha = 0.85f)
        }
        return _sFKSquareFill!!
    }

private var _sFKSquareFill: ImageVector? = null
