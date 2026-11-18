package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareTextSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFSquareTextSquareFill: ImageVector
    get() {
        if (_sFSquareTextSquareFill != null) {
            return _sFSquareTextSquareFill!!
        }
        _sFSquareTextSquareFill = sfIcon(
            name = "Monochrome.SFSquareTextSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM4.56055 17.7539C4.16016 17.7539 3.86719 18.0566 3.86719 18.4473C3.86719 18.8184 4.16016 19.1113 4.56055 19.1113L13.877 19.1113C14.2676 19.1113 14.5605 18.8184 14.5605 18.4473C14.5605 18.0566 14.2676 17.7539 13.877 17.7539ZM4.56055 13.5156C4.16016 13.5156 3.86719 13.8086 3.86719 14.1797C3.86719 14.5605 4.16016 14.8633 4.56055 14.8633L17.3047 14.8633C17.6953 14.8633 17.9883 14.5605 17.9883 14.1797C17.9883 13.8086 17.6953 13.5156 17.3047 13.5156ZM5.23438 3.4375C4.47266 3.4375 3.99414 3.90625 3.99414 4.6582L3.99414 7.99805C3.99414 8.75 4.47266 9.22852 5.23438 9.22852L8.55469 9.22852C9.31641 9.22852 9.79492 8.75 9.79492 7.99805L9.79492 4.6582C9.79492 3.90625 9.31641 3.4375 8.55469 3.4375Z", fillAlpha = 0.85f)
        }
        return _sFSquareTextSquareFill!!
    }

private var _sFSquareTextSquareFill: ImageVector? = null
