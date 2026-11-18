package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBellBadgeCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFBellBadgeCircleFill: ImageVector
    get() {
        if (_sFBellBadgeCircleFill != null) {
            return _sFBellBadgeCircleFill!!
        }
        _sFBellBadgeCircleFill = sfIcon(
            name = "Monochrome.SFBellBadgeCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM10.5957 18.4277C10.6934 19.4922 11.543 20.3125 12.7246 20.3125C13.9062 20.3125 14.7656 19.4922 14.8633 18.4277ZM10.7227 6.9043C8.87695 7.54883 8.13477 9.33594 8.03711 11.875C7.94922 12.8809 7.88086 14.0723 7.33398 14.6582C6.73828 15.3223 6.02539 15.9277 6.02539 16.6504C6.02539 17.0996 6.35742 17.4023 6.93359 17.4023L18.5254 17.4023C19.0918 17.4023 19.4336 17.0996 19.4336 16.6504C19.4336 15.9277 18.7305 15.3125 18.1055 14.6582C17.5488 14.0723 17.5 12.8809 17.4023 11.875C17.3887 11.4813 17.3594 11.1057 17.3105 10.752C16.928 10.9102 16.5102 10.9961 16.0742 10.9961C14.2285 10.9961 12.6953 9.48242 12.6953 7.61719C12.6953 6.77277 13.0129 6.00319 13.5352 5.41732C13.2927 5.30556 13.0207 5.24414 12.7246 5.24414C11.7188 5.24414 10.9863 5.9668 10.7227 6.9043ZM13.6426 7.61719C13.6426 8.95508 14.7461 10.0488 16.0742 10.0488C17.3926 10.0488 18.4863 8.95508 18.4863 7.61719C18.4863 6.28906 17.3926 5.21484 16.0742 5.21484C14.7461 5.21484 13.6426 6.28906 13.6426 7.61719Z", fillAlpha = 0.85f)
        }
        return _sFBellBadgeCircleFill!!
    }

private var _sFBellBadgeCircleFill: ImageVector? = null
