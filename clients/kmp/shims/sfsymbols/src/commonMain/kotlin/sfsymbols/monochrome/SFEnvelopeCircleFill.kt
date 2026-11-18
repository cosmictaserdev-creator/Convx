package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEnvelopeCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFEnvelopeCircleFill: ImageVector
    get() {
        if (_sFEnvelopeCircleFill != null) {
            return _sFEnvelopeCircleFill!!
        }
        _sFEnvelopeCircleFill = sfIcon(
            name = "Monochrome.SFEnvelopeCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM13.8867 13.916C13.5059 14.2383 13.1152 14.3945 12.7246 14.3945C12.334 14.3945 11.9434 14.2383 11.582 13.916L10.6445 13.0859L5.79102 17.9297C6.03516 18.0957 6.36719 18.2031 6.83594 18.2031L18.6133 18.2031C19.0723 18.2031 19.4141 18.0859 19.668 17.9297L14.8047 13.0859ZM5.03906 9.18945L5.03906 16.2891C5.03906 16.6895 5.10742 16.9824 5.23438 17.2363L9.9707 12.4902L5.21484 8.28125C5.12695 8.4375 5.03906 8.7793 5.03906 9.18945ZM15.4883 12.4902L20.2344 17.2266C20.3418 16.9824 20.4004 16.6797 20.4004 16.2891L20.4004 9.18945C20.4004 8.7793 20.3223 8.45703 20.2441 8.29102ZM6.98242 7.27539C6.46484 7.27539 6.09375 7.36328 5.76172 7.57812L12.1191 13.2129C12.3242 13.3984 12.5391 13.4668 12.7246 13.4668C12.9199 13.4668 13.125 13.3984 13.3301 13.2129L19.6973 7.57812C19.3555 7.37305 18.9746 7.27539 18.4766 7.27539Z", fillAlpha = 0.85f)
        }
        return _sFEnvelopeCircleFill!!
    }

private var _sFEnvelopeCircleFill: ImageVector? = null
