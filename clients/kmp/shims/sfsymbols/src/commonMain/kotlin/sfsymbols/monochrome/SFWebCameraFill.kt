package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWebCameraFill (monochrome)
 * Viewport: 20.2637 x 24.9707
 */
public val SfSymbols.Monochrome.SFWebCameraFill: ImageVector
    get() {
        if (_sFWebCameraFill != null) {
            return _sFWebCameraFill!!
        }
        _sFWebCameraFill = sfIcon(
            name = "Monochrome.SFWebCameraFill",
            viewportWidth = 20.2637f,
            viewportHeight = 24.9707f
        ) {
            addSfPath("M19.9023 9.95117C19.9023 15.1645 15.9126 19.433 10.8301 19.8682L10.8301 23.2227L14.2871 23.2227C14.7656 23.2227 15.1562 23.6133 15.1562 24.0918C15.1562 24.5605 14.7656 24.9512 14.2871 24.9512L5.625 24.9512C5.15625 24.9512 4.76562 24.5605 4.76562 24.0918C4.76562 23.6133 5.15625 23.2227 5.63477 23.2227L9.0918 23.2227L9.0918 19.8683C3.99927 19.4338 0 15.1651 0 9.95117C0 4.45312 4.46289 0 9.96094 0C15.4492 0 19.9023 4.45312 19.9023 9.95117ZM4.05273 9.95117C4.05273 13.2617 6.66016 15.8691 9.96094 15.8691C13.2617 15.8691 15.8496 13.2617 15.8496 9.95117C15.8496 6.66016 13.2617 4.04297 9.96094 4.04297C6.66016 4.04297 4.05273 6.66016 4.05273 9.95117ZM12.6953 9.95117C12.6953 11.4941 11.4941 12.7148 9.96094 12.7148C8.42773 12.7148 7.20703 11.4941 7.20703 9.95117C7.20703 8.41797 8.42773 7.20703 9.96094 7.20703C11.4941 7.20703 12.6953 8.41797 12.6953 9.95117Z", fillAlpha = 0.85f)
        }
        return _sFWebCameraFill!!
    }

private var _sFWebCameraFill: ImageVector? = null
