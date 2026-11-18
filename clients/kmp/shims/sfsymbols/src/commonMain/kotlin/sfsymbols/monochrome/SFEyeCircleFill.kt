package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEyeCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFEyeCircleFill: ImageVector
    get() {
        if (_sFEyeCircleFill != null) {
            return _sFEyeCircleFill!!
        }
        _sFEyeCircleFill = sfIcon(
            name = "Monochrome.SFEyeCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM4.17969 12.7441C4.17969 14.0137 7.74414 18.125 12.7148 18.125C17.7246 18.125 21.25 14.0137 21.25 12.7441C21.25 11.4648 17.7051 7.36328 12.7148 7.36328C7.69531 7.36328 4.17969 11.4648 4.17969 12.7441ZM16.25 12.7441C16.25 14.6582 14.6582 16.2695 12.7148 16.2695C10.752 16.2695 9.17969 14.6582 9.17969 12.7441C9.16992 10.7812 10.752 9.21875 12.7148 9.21875C14.6582 9.21875 16.25 10.7812 16.25 12.7441ZM11.2207 12.7441C11.2207 13.5352 11.9141 14.2285 12.7344 14.2285C13.5254 14.2285 14.209 13.5352 14.209 12.7441C14.209 11.9434 13.5254 11.25 12.7344 11.25C11.9141 11.25 11.2207 11.9434 11.2207 12.7441Z", fillAlpha = 0.85f)
        }
        return _sFEyeCircleFill!!
    }

private var _sFEyeCircleFill: ImageVector? = null
