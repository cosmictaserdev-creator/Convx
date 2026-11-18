package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusminusCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPlusminusCircle: ImageVector
    get() {
        if (_sFPlusminusCircle != null) {
            return _sFPlusminusCircle!!
        }
        _sFPlusminusCircle = sfIcon(
            name = "Dualtone.SFPlusminusCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M9.17969 18.6133L16.2598 18.6133C16.6895 18.6133 17.0312 18.2422 17.0312 17.8516C17.0312 17.4512 16.6895 17.0996 16.2598 17.0996L9.17969 17.0996C8.75 17.0996 8.4082 17.4512 8.4082 17.8516C8.4082 18.2422 8.75 18.6133 9.17969 18.6133ZM9.16992 10.9961L16.2695 10.9961C16.6895 10.9961 17.0312 10.6543 17.0312 10.2441C17.0312 9.82422 16.6895 9.48242 16.2695 9.48242L9.16992 9.48242C8.75 9.48242 8.4082 9.82422 8.4082 10.2441C8.4082 10.6543 8.75 10.9961 9.16992 10.9961ZM12.7246 14.5508C13.1445 14.5508 13.4863 14.209 13.4863 13.7988L13.4863 6.67969C13.4863 6.26953 13.1348 5.92773 12.7246 5.92773C12.3145 5.92773 11.9727 6.26953 11.9727 6.67969L11.9727 13.7988C11.9727 14.1992 12.3145 14.5508 12.7246 14.5508Z", fillAlpha = 0.85f)
        }
        return _sFPlusminusCircle!!
    }

private var _sFPlusminusCircle: ImageVector? = null
