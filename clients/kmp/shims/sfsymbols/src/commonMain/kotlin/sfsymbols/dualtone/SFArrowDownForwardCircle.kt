package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownForwardCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowDownForwardCircle: ImageVector
    get() {
        if (_sFArrowDownForwardCircle != null) {
            return _sFArrowDownForwardCircle!!
        }
        _sFArrowDownForwardCircle = sfIcon(
            name = "Dualtone.SFArrowDownForwardCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M15.4004 16.4355C16.2109 17.1875 17.207 16.25 16.4062 15.4102L14.4336 13.3105L9.28711 8.16406C9.13086 8.00781 8.93555 7.91016 8.67188 7.91016C8.18359 7.91016 7.87109 8.22266 7.87109 8.71094C7.87109 8.93555 7.97852 9.14062 8.13477 9.29688L13.291 14.4531ZM15.957 13.0566L16.2402 16.2793L13.252 15.9863L10.4785 15.9863C9.9707 15.9863 9.62891 16.2988 9.62891 16.7773C9.62891 17.2559 9.96094 17.5586 10.459 17.5586L16.6602 17.5586C17.1973 17.5586 17.5488 17.3047 17.5488 16.6797L17.5488 10.5176C17.5488 10.0098 17.2266 9.6582 16.7578 9.6582C16.2793 9.6582 15.957 9.98047 15.957 10.498Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownForwardCircle!!
    }

private var _sFArrowDownForwardCircle: ImageVector? = null
