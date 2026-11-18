package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPowerCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPowerCircle: ImageVector
    get() {
        if (_sFPowerCircle != null) {
            return _sFPowerCircle!!
        }
        _sFPowerCircle = sfIcon(
            name = "Monochrome.SFPowerCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M12.7148 19.2285C16.2891 19.2285 19.248 16.2695 19.248 12.7051C19.248 10.8887 18.4375 9.10156 17.0605 7.87109C16.3086 7.17773 15.3027 8.24219 16.0645 8.96484C17.1289 9.92188 17.7344 11.25 17.7441 12.7051C17.7441 15.5078 15.5078 17.7344 12.7148 17.7344C9.92188 17.7344 7.69531 15.5078 7.69531 12.7051C7.69531 11.25 8.29102 9.92188 9.36523 8.95508C10.1367 8.23242 9.12109 7.16797 8.36914 7.87109C6.99219 9.08203 6.19141 10.8691 6.19141 12.7051C6.19141 16.2695 9.15039 19.2285 12.7148 19.2285ZM12.7148 12.9297C13.1445 12.9297 13.4473 12.627 13.4473 12.1875L13.4473 6.5625C13.4473 6.12305 13.1445 5.82031 12.7148 5.82031C12.2852 5.82031 11.9922 6.12305 11.9922 6.5625L11.9922 12.1875C11.9922 12.627 12.2852 12.9297 12.7148 12.9297Z", fillAlpha = 0.85f)
        }
        return _sFPowerCircle!!
    }

private var _sFPowerCircle: ImageVector? = null
