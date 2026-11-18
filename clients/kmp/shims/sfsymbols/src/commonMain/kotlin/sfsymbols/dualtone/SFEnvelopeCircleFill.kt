package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEnvelopeCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFEnvelopeCircleFill: ImageVector
    get() {
        if (_sFEnvelopeCircleFill != null) {
            return _sFEnvelopeCircleFill!!
        }
        _sFEnvelopeCircleFill = sfIcon(
            name = "Dualtone.SFEnvelopeCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M12.7246 13.4668C12.5391 13.4668 12.3242 13.3984 12.1191 13.2129L5.76172 7.57812C6.09375 7.36328 6.46484 7.27539 6.98242 7.27539L18.4766 7.27539C18.9746 7.27539 19.3555 7.37305 19.6973 7.57812L13.3301 13.2129C13.125 13.3984 12.9199 13.4668 12.7246 13.4668ZM5.23438 17.2363C5.10742 16.9824 5.03906 16.6895 5.03906 16.2891L5.03906 9.18945C5.03906 8.7793 5.12695 8.4375 5.21484 8.28125L9.9707 12.4902ZM6.83594 18.2031C6.36719 18.2031 6.03516 18.0957 5.79102 17.9297L10.6445 13.0859L11.582 13.916C11.9434 14.2383 12.334 14.3945 12.7246 14.3945C13.1152 14.3945 13.5059 14.2383 13.8867 13.916L14.8047 13.0859L19.668 17.9297C19.4141 18.0859 19.0723 18.2031 18.6133 18.2031ZM20.2344 17.2266L15.4883 12.4902L20.2441 8.29102C20.3223 8.45703 20.4004 8.7793 20.4004 9.18945L20.4004 16.2891C20.4004 16.6797 20.3418 16.9824 20.2344 17.2266Z", fillAlpha = 0.85f)
        }
        return _sFEnvelopeCircleFill!!
    }

private var _sFEnvelopeCircleFill: ImageVector? = null
