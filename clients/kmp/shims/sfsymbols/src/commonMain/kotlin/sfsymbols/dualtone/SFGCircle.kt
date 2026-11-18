package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFGCircle: ImageVector
    get() {
        if (_sFGCircle != null) {
            return _sFGCircle!!
        }
        _sFGCircle = sfIcon(
            name = "Dualtone.SFGCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.7734 18.5938C15.7422 18.5938 17.5293 16.8066 17.5293 14.1797L17.5293 13.2715C17.5293 12.6953 17.1973 12.3438 16.6309 12.3438L13.252 12.3438C12.8516 12.3438 12.5977 12.6074 12.5977 12.9883C12.5977 13.3203 12.8516 13.623 13.252 13.623L15.8203 13.623L15.8203 14.3945C15.8203 16.0645 14.7656 17.1484 12.793 17.1484C10.6738 17.1484 9.24805 15.3418 9.24805 12.6367C9.24805 9.94141 10.6641 8.11523 12.7637 8.11523C14.2969 8.11523 15.4395 8.88672 15.8887 10.2246C16.0645 10.6055 16.2695 10.7715 16.6211 10.7715C17.0703 10.7715 17.3633 10.4883 17.3633 10.0195C17.3633 9.83398 17.3438 9.67773 17.2754 9.49219C16.7578 7.97852 15.1562 6.66992 12.7441 6.66992C9.56055 6.66992 7.5 9.01367 7.5 12.6367C7.5 16.2695 9.56055 18.5938 12.7734 18.5938Z", fillAlpha = 0.85f)
        }
        return _sFGCircle!!
    }

private var _sFGCircle: ImageVector? = null
