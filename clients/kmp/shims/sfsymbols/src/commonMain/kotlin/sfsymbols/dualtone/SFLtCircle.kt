package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLtCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFLtCircle: ImageVector
    get() {
        if (_sFLtCircle != null) {
            return _sFLtCircle!!
        }
        _sFLtCircle = sfIcon(
            name = "Dualtone.SFLtCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M8.32031 17.832L12.002 17.832C12.3633 17.832 12.6758 17.5195 12.6758 17.1484C12.6758 16.7871 12.3633 16.4746 12.002 16.4746L9.13086 16.4746L9.13086 8.28125C9.13086 7.85156 8.76953 7.48047 8.33984 7.48047C7.90039 7.48047 7.53906 7.85156 7.53906 8.28125L7.53906 16.9922C7.53906 17.5098 7.82227 17.832 8.32031 17.832ZM15.5566 17.9688C15.9961 17.9688 16.3574 17.6172 16.3574 17.168L16.3574 8.97461L17.9688 8.97461C18.3398 8.97461 18.6328 8.68164 18.6328 8.30078C18.6328 7.92969 18.3398 7.63672 17.9688 7.63672L13.0469 7.63672C12.666 7.63672 12.373 7.92969 12.373 8.30078C12.373 8.68164 12.666 8.97461 13.0469 8.97461L14.7559 8.97461L14.7559 17.168C14.7559 17.6172 15.1172 17.9688 15.5566 17.9688Z", fillAlpha = 0.85f)
        }
        return _sFLtCircle!!
    }

private var _sFLtCircle: ImageVector? = null
