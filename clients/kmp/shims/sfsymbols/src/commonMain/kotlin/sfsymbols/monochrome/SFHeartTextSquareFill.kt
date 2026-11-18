package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeartTextSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFHeartTextSquareFill: ImageVector
    get() {
        if (_sFHeartTextSquareFill != null) {
            return _sFHeartTextSquareFill!!
        }
        _sFHeartTextSquareFill = sfIcon(
            name = "Monochrome.SFHeartTextSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM4.56055 17.7539C4.16016 17.7539 3.86719 18.0566 3.86719 18.4473C3.86719 18.8184 4.16016 19.1113 4.56055 19.1113L13.877 19.1113C14.2676 19.1113 14.5605 18.8184 14.5605 18.4473C14.5605 18.0566 14.2676 17.7539 13.877 17.7539ZM4.56055 13.5156C4.16016 13.5156 3.86719 13.8086 3.86719 14.1797C3.86719 14.5605 4.16016 14.8633 4.56055 14.8633L17.3047 14.8633C17.6953 14.8633 17.9883 14.5605 17.9883 14.1797C17.9883 13.8086 17.6953 13.5156 17.3047 13.5156ZM7.2168 4.3457C6.9043 3.7793 6.36719 3.36914 5.68359 3.36914C4.63867 3.36914 3.86719 4.18945 3.86719 5.2832C3.86719 6.99219 5.69336 8.49609 6.93359 9.30664C7.04102 9.375 7.14844 9.43359 7.22656 9.43359C7.30469 9.43359 7.40234 9.375 7.50977 9.30664C8.75 8.54492 10.5664 6.99219 10.5664 5.2832C10.5664 4.18945 9.79492 3.36914 8.7207 3.36914C8.07617 3.36914 7.51953 3.7793 7.2168 4.3457Z", fillAlpha = 0.85f)
        }
        return _sFHeartTextSquareFill!!
    }

private var _sFHeartTextSquareFill: ImageVector? = null
