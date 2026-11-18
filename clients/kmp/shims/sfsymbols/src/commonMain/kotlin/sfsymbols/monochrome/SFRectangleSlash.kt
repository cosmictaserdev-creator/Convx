package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleSlash (monochrome)
 * Viewport: 29.9512 x 29.6607
 */
public val SfSymbols.Monochrome.SFRectangleSlash: ImageVector
    get() {
        if (_sFRectangleSlash != null) {
            return _sFRectangleSlash!!
        }
        _sFRectangleSlash = sfIcon(
            name = "Monochrome.SFRectangleSlash",
            viewportWidth = 29.9512f,
            viewportHeight = 29.6607f
        ) {
            addSfPath("M2.39847 5.51357C1.96515 5.86706 1.73828 6.43616 1.73828 7.20826L1.73828 22.4524C1.73828 23.8586 2.48047 24.5813 3.83789 24.5813L21.4809 24.5813L23.2108 26.3098L3.80859 26.3098C1.28906 26.3098 0 25.0501 0 22.5501L0 7.12037C0 5.79721 0.361089 4.81873 1.07825 4.19438ZM29.5898 7.12037L29.5898 22.5501C29.5898 23.8442 29.2442 24.8114 28.5511 25.4373L27.228 24.1147C27.6404 23.7617 27.8613 23.2049 27.8613 22.4524L27.8613 7.20826C27.8613 5.81177 27.0898 5.07935 25.752 5.07935L8.1853 5.07935L6.45612 3.35084L25.791 3.35084C28.3203 3.35084 29.5898 4.63013 29.5898 7.12037Z", fillAlpha = 0.85f)
            addSfPath("M26.875 28.0481C27.207 28.3801 27.7344 28.3801 28.0566 28.0481C28.3789 27.7161 28.3789 27.1985 28.0566 26.8762L2.79297 1.62232C2.4707 1.30005 1.94336 1.29029 1.61133 1.62232C1.28906 1.93482 1.28906 2.48169 1.61133 2.80396Z", fillAlpha = 0.85f)
        }
        return _sFRectangleSlash!!
    }

private var _sFRectangleSlash: ImageVector? = null
