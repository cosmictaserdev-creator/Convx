package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSpeakerSlashCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFSpeakerSlashCircle: ImageVector
    get() {
        if (_sFSpeakerSlashCircle != null) {
            return _sFSpeakerSlashCircle!!
        }
        _sFSpeakerSlashCircle = sfIcon(
            name = "Monochrome.SFSpeakerSlashCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7461 19.7363 25.4395 12.7148 25.4395C5.69336 25.4395 0 19.7461 0 12.7246C0 5.70312 5.69336 0 12.7148 0C19.7363 0 25.4395 5.70312 25.4395 12.7246ZM1.81641 12.7246C1.81641 18.75 6.68945 23.623 12.7148 23.623C18.7402 23.623 23.6133 18.75 23.6133 12.7246C23.6133 6.69922 18.7402 1.82617 12.7148 1.82617C6.68945 1.82617 1.81641 6.69922 1.81641 12.7246Z", fillAlpha = 0.85f)
            addSfPath("M15.752 17.666L15.752 18.0469C15.752 18.457 15.4688 18.7402 15.0684 18.7402C14.7656 18.7402 14.5215 18.6328 14.2383 18.3594L11.25 15.5176C11.2012 15.4688 11.1133 15.4395 11.0352 15.4395L9.00391 15.4395C8.13477 15.4395 7.64648 14.9414 7.64648 14.0332L7.64648 11.4355C7.64648 10.826 7.86642 10.4012 8.27476 10.1888ZM15.752 7.42188L15.752 13.8347L11.5687 9.64805L14.2383 7.10938C14.5508 6.81641 14.7559 6.68945 15.0586 6.68945C15.4688 6.68945 15.752 7.02148 15.752 7.42188Z", fillAlpha = 0.85f)
            addSfPath("M18.2812 18.9941C18.4766 19.1992 18.8086 19.1992 19.0137 18.9941C19.2188 18.7891 19.2188 18.4766 19.0137 18.2617L7.1875 6.43555C6.98242 6.23047 6.65039 6.23047 6.44531 6.43555C6.24023 6.64062 6.25 6.97266 6.44531 7.16797Z", fillAlpha = 0.85f)
        }
        return _sFSpeakerSlashCircle!!
    }

private var _sFSpeakerSlashCircle: ImageVector? = null
