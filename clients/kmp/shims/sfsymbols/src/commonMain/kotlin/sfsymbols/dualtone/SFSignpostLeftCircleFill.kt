package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSignpostLeftCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSignpostLeftCircleFill: ImageVector
    get() {
        if (_sFSignpostLeftCircleFill != null) {
            return _sFSignpostLeftCircleFill!!
        }
        _sFSignpostLeftCircleFill = sfIcon(
            name = "Dualtone.SFSignpostLeftCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M17.4902 13.9941L13.3691 13.9844L13.3691 18.5742L15.2441 18.5742C15.5957 18.5742 15.8887 18.8574 15.8887 19.2188C15.8887 19.5605 15.5957 19.8535 15.2441 19.8535L10.2051 19.8535C9.86328 19.8535 9.57031 19.5605 9.57031 19.2188C9.57031 18.8574 9.86328 18.5742 10.2051 18.5742L12.0996 18.5742L12.0996 13.9844L8.83789 13.9844C8.28125 13.9844 7.99805 13.8086 7.69531 13.5156L5.9375 11.8652C5.26367 11.2305 5.24414 10.5566 5.94727 9.91211L7.69531 8.26172C7.99805 7.94922 8.28125 7.79297 8.83789 7.79297L12.0996 7.79297L12.0996 6.25977C12.0996 5.89844 12.3828 5.61523 12.7246 5.61523C13.0859 5.61523 13.3691 5.89844 13.3691 6.25977L13.3691 7.79297L17.4902 7.79297C18.3398 7.79297 18.8184 8.25195 18.8184 9.08203L18.8184 12.6855C18.8184 13.5156 18.3398 13.9941 17.4902 13.9941Z", fillAlpha = 0.85f)
        }
        return _sFSignpostLeftCircleFill!!
    }

private var _sFSignpostLeftCircleFill: ImageVector? = null
