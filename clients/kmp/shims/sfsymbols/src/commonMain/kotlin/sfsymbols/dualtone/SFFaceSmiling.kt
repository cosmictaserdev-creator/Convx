package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFaceSmiling (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFFaceSmiling: ImageVector
    get() {
        if (_sFFaceSmiling != null) {
            return _sFFaceSmiling!!
        }
        _sFFaceSmiling = sfIcon(
            name = "Dualtone.SFFaceSmiling",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.7051 18.9648C15.2832 18.9648 17.0312 17.2266 17.0312 16.4258C17.0312 16.1621 16.7676 16.0449 16.543 16.1621C15.6738 16.709 14.5215 17.2754 12.7051 17.2754C10.8887 17.2754 9.74609 16.6992 8.87695 16.1621C8.64258 16.0449 8.37891 16.1621 8.37891 16.4258C8.37891 17.2266 10.1367 18.9648 12.7051 18.9648ZM8.95508 11.5723C9.7168 11.5723 10.4004 10.8691 10.4004 9.90234C10.4004 8.93555 9.72656 8.22266 8.95508 8.22266C8.19336 8.22266 7.53906 8.93555 7.53906 9.90234C7.53906 10.8691 8.20312 11.5723 8.95508 11.5723ZM16.4551 11.5723C17.2266 11.5723 17.9004 10.8691 17.9004 9.90234C17.9004 8.93555 17.2266 8.22266 16.4551 8.22266C15.6934 8.22266 15.0391 8.93555 15.0391 9.90234C15.0391 10.8691 15.7031 11.5723 16.4551 11.5723Z", fillAlpha = 0.85f)
        }
        return _sFFaceSmiling!!
    }

private var _sFFaceSmiling: ImageVector? = null
