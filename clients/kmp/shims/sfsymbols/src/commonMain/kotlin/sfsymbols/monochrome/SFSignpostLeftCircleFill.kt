package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSignpostLeftCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFSignpostLeftCircleFill: ImageVector
    get() {
        if (_sFSignpostLeftCircleFill != null) {
            return _sFSignpostLeftCircleFill!!
        }
        _sFSignpostLeftCircleFill = sfIcon(
            name = "Monochrome.SFSignpostLeftCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM12.0996 6.25977L12.0996 7.79297L8.83789 7.79297C8.28125 7.79297 7.99805 7.94922 7.69531 8.26172L5.94727 9.91211C5.24414 10.5566 5.26367 11.2305 5.9375 11.8652L7.69531 13.5156C7.99805 13.8086 8.28125 13.9844 8.83789 13.9844L12.0996 13.9844L12.0996 18.5742L10.2051 18.5742C9.86328 18.5742 9.57031 18.8574 9.57031 19.2188C9.57031 19.5605 9.86328 19.8535 10.2051 19.8535L15.2441 19.8535C15.5957 19.8535 15.8887 19.5605 15.8887 19.2188C15.8887 18.8574 15.5957 18.5742 15.2441 18.5742L13.3691 18.5742L13.3691 13.9844L17.4902 13.9941C18.3398 13.9941 18.8184 13.5156 18.8184 12.6855L18.8184 9.08203C18.8184 8.25195 18.3398 7.79297 17.4902 7.79297L13.3691 7.79297L13.3691 6.25977C13.3691 5.89844 13.0859 5.61523 12.7246 5.61523C12.3828 5.61523 12.0996 5.89844 12.0996 6.25977Z", fillAlpha = 0.85f)
        }
        return _sFSignpostLeftCircleFill!!
    }

private var _sFSignpostLeftCircleFill: ImageVector? = null
