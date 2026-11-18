package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWebCameraFill (dualtone)
 * Viewport: 20.2637 x 24.9707
 */
public val SfSymbols.Dualtone.SFWebCameraFill: ImageVector
    get() {
        if (_sFWebCameraFill != null) {
            return _sFWebCameraFill!!
        }
        _sFWebCameraFill = sfIcon(
            name = "Dualtone.SFWebCameraFill",
            viewportWidth = 20.2637f,
            viewportHeight = 24.9707f
        ) {
            addSfPath("M9.96094 19.9121C15.4492 19.9121 19.9023 15.459 19.9023 9.95117C19.9023 4.45312 15.4492 0 9.96094 0C4.46289 0 0 4.45312 0 9.95117C0 15.459 4.46289 19.9121 9.96094 19.9121ZM9.0918 24.0918L10.8301 24.0918L10.8301 19.1211L9.0918 19.1211ZM5.625 24.9512L14.2871 24.9512C14.7656 24.9512 15.1562 24.5605 15.1562 24.0918C15.1562 23.6133 14.7656 23.2227 14.2871 23.2227L5.63477 23.2227C5.15625 23.2227 4.76562 23.6133 4.76562 24.0918C4.76562 24.5605 5.15625 24.9512 5.625 24.9512Z", fillAlpha = 0.2125f)
            addSfPath("M9.96094 15.8691C6.66016 15.8691 4.05273 13.2617 4.05273 9.95117C4.05273 6.66016 6.66016 4.04297 9.96094 4.04297C13.2617 4.04297 15.8496 6.66016 15.8496 9.95117C15.8496 13.2617 13.2617 15.8691 9.96094 15.8691ZM9.96094 12.7148C11.4941 12.7148 12.6953 11.4941 12.6953 9.95117C12.6953 8.41797 11.4941 7.20703 9.96094 7.20703C8.42773 7.20703 7.20703 8.41797 7.20703 9.95117C7.20703 11.4941 8.42773 12.7148 9.96094 12.7148Z", fillAlpha = 0.85f)
        }
        return _sFWebCameraFill!!
    }

private var _sFWebCameraFill: ImageVector? = null
