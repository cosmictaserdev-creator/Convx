package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMappinCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFMappinCircle: ImageVector
    get() {
        if (_sFMappinCircle != null) {
            return _sFMappinCircle!!
        }
        _sFMappinCircle = sfIcon(
            name = "Dualtone.SFMappinCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M9.96094 7.88086C9.96094 9.13086 10.8105 10.1758 11.9531 10.498L11.9531 17.4316C11.9531 19.1309 12.2266 20.5371 12.7051 20.5371C13.1836 20.5371 13.4473 19.1602 13.4473 17.4316L13.4473 10.498C14.6094 10.1855 15.4492 9.14062 15.4492 7.88086C15.4492 6.36719 14.2188 5.13672 12.7051 5.13672C11.1914 5.13672 9.96094 6.36719 9.96094 7.88086ZM11.1133 7.17773C11.1035 6.68945 11.5332 6.2793 12.002 6.2793C12.4902 6.2793 12.8906 6.68945 12.8809 7.17773C12.8809 7.64648 12.4902 8.05664 12.002 8.05664C11.5332 8.05664 11.123 7.64648 11.1133 7.17773Z", fillAlpha = 0.85f)
        }
        return _sFMappinCircle!!
    }

private var _sFMappinCircle: ImageVector? = null
