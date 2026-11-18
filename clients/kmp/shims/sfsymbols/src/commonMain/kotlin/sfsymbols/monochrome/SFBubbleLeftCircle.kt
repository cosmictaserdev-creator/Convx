package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBubbleLeftCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFBubbleLeftCircle: ImageVector
    get() {
        if (_sFBubbleLeftCircle != null) {
            return _sFBubbleLeftCircle!!
        }
        _sFBubbleLeftCircle = sfIcon(
            name = "Monochrome.SFBubbleLeftCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M9.42383 19.9902C9.64844 19.9902 9.79492 19.8926 10.0391 19.6582L12.334 17.6074L16.8652 17.6074C18.6133 17.6074 19.6094 16.6016 19.6094 14.8535L19.6094 9.85352C19.6094 8.125 18.6133 7.10938 16.8652 7.10938L8.56445 7.10938C6.81641 7.10938 5.82031 8.11523 5.82031 9.85352L5.82031 14.8535C5.82031 16.6016 6.81641 17.6074 8.56445 17.6074L8.94531 17.6074L8.94531 19.4434C8.94531 19.7754 9.12109 19.9902 9.42383 19.9902Z", fillAlpha = 0.85f)
        }
        return _sFBubbleLeftCircle!!
    }

private var _sFBubbleLeftCircle: ImageVector? = null
