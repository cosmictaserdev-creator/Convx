package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSquareTextSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFSquareTextSquareFill: ImageVector
    get() {
        if (_sFSquareTextSquareFill != null) {
            return _sFSquareTextSquareFill!!
        }
        _sFSquareTextSquareFill = sfIcon(
            name = "Dualtone.SFSquareTextSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M3.99414 4.6582C3.99414 3.90625 4.47266 3.4375 5.23438 3.4375L8.55469 3.4375C9.31641 3.4375 9.79492 3.90625 9.79492 4.6582L9.79492 7.99805C9.79492 8.75 9.31641 9.22852 8.55469 9.22852L5.23438 9.22852C4.47266 9.22852 3.99414 8.75 3.99414 7.99805Z", fillAlpha = 0.85f)
            addSfPath("M4.56055 14.8633C4.16016 14.8633 3.86719 14.5605 3.86719 14.1797C3.86719 13.8086 4.16016 13.5156 4.56055 13.5156L17.3047 13.5156C17.6953 13.5156 17.9883 13.8086 17.9883 14.1797C17.9883 14.5605 17.6953 14.8633 17.3047 14.8633Z", fillAlpha = 0.85f)
            addSfPath("M4.56055 19.1113C4.16016 19.1113 3.86719 18.8184 3.86719 18.4473C3.86719 18.0566 4.16016 17.7539 4.56055 17.7539L13.877 17.7539C14.2676 17.7539 14.5605 18.0566 14.5605 18.4473C14.5605 18.8184 14.2676 19.1113 13.877 19.1113Z", fillAlpha = 0.85f)
        }
        return _sFSquareTextSquareFill!!
    }

private var _sFSquareTextSquareFill: ImageVector? = null
