package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFolderCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFFolderCircle: ImageVector
    get() {
        if (_sFFolderCircle != null) {
            return _sFFolderCircle!!
        }
        _sFFolderCircle = sfIcon(
            name = "Monochrome.SFFolderCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M7.4707 18.4375L18.1152 18.4375C19.209 18.4375 19.8438 17.8027 19.8438 16.5918L19.8438 9.73633C19.8438 8.52539 19.1992 7.89062 17.959 7.89062L11.7871 7.89062C11.3184 7.89062 11.084 7.79297 10.7715 7.51953L10.3906 7.20703C9.9707 6.86523 9.6875 6.76758 9.08203 6.76758L7.22656 6.76758C6.18164 6.76758 5.5957 7.34375 5.5957 8.50586L5.5957 16.5918C5.5957 17.8125 6.24023 18.4375 7.4707 18.4375ZM6.65039 10.293L6.65039 10.0195C6.65039 9.61914 6.93359 9.3457 7.44141 9.3457L17.9883 9.3457C18.5059 9.3457 18.7695 9.61914 18.7695 10.0195L18.7695 10.293Z", fillAlpha = 0.85f)
        }
        return _sFFolderCircle!!
    }

private var _sFFolderCircle: ImageVector? = null
