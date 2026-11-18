package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSpeakerCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFSpeakerCircle: ImageVector
    get() {
        if (_sFSpeakerCircle != null) {
            return _sFSpeakerCircle!!
        }
        _sFSpeakerCircle = sfIcon(
            name = "Monochrome.SFSpeakerCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M15.0684 18.7402C15.4688 18.7402 15.752 18.457 15.752 18.0469L15.752 7.42188C15.752 7.02148 15.4688 6.68945 15.0586 6.68945C14.7559 6.68945 14.5508 6.81641 14.2383 7.10938L11.25 9.95117C11.2012 10 11.1133 10.0293 11.0352 10.0293L9.00391 10.0293C8.13477 10.0293 7.64648 10.5273 7.64648 11.4355L7.64648 14.0332C7.64648 14.9414 8.13477 15.4395 9.00391 15.4395L11.0352 15.4395C11.1133 15.4395 11.2012 15.4688 11.25 15.5176L14.2383 18.3594C14.5215 18.6328 14.7656 18.7402 15.0684 18.7402Z", fillAlpha = 0.85f)
        }
        return _sFSpeakerCircle!!
    }

private var _sFSpeakerCircle: ImageVector? = null
