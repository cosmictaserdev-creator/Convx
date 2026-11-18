package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFXmarkCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFXmarkCircleFill: ImageVector
    get() {
        if (_sFXmarkCircleFill != null) {
            return _sFXmarkCircleFill!!
        }
        _sFXmarkCircleFill = sfIcon(
            name = "Monochrome.SFXmarkCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM16.5625 7.65625L12.7274 11.4746L8.89648 7.65625C8.71094 7.48047 8.50586 7.39258 8.26172 7.39258C7.75391 7.39258 7.35352 7.77344 7.35352 8.26172C7.35352 8.51562 7.45117 8.74023 7.62695 8.91602L11.4537 12.7427L7.62695 16.5527C7.45117 16.7383 7.35352 16.9531 7.35352 17.1973C7.35352 17.6953 7.75391 18.0957 8.26172 18.0957C8.51562 18.0957 8.73047 17.998 8.91602 17.8223L12.7246 14.0137L16.5332 17.8223C16.709 17.998 16.9238 18.0957 17.1875 18.0957C17.6855 18.0957 18.0859 17.6953 18.0859 17.1973C18.0859 16.9531 17.998 16.7383 17.8223 16.5527L13.9976 12.7407L17.8223 8.91602C17.998 8.74023 18.0859 8.51562 18.0859 8.26172C18.0859 7.77344 17.6855 7.39258 17.1875 7.39258C16.9434 7.39258 16.7285 7.48047 16.5625 7.65625Z", fillAlpha = 0.85f)
        }
        return _sFXmarkCircleFill!!
    }

private var _sFXmarkCircleFill: ImageVector? = null
