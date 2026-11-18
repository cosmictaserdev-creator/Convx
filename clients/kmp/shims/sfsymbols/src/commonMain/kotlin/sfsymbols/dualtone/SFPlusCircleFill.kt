package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPlusCircleFill: ImageVector
    get() {
        if (_sFPlusCircleFill != null) {
            return _sFPlusCircleFill!!
        }
        _sFPlusCircleFill = sfIcon(
            name = "Dualtone.SFPlusCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M13.6426 17.6758L13.6426 7.74414C13.6426 7.17773 13.2617 6.78711 12.7051 6.78711C12.168 6.78711 11.7871 7.1875 11.7871 7.74414L11.7871 17.6758C11.7871 18.2227 12.168 18.623 12.7051 18.623C13.2617 18.623 13.6426 18.2324 13.6426 17.6758ZM7.75391 13.6328L17.6953 13.6328C18.2324 13.6328 18.6328 13.2617 18.6328 12.7246C18.6328 12.1582 18.2422 11.7773 17.6953 11.7773L7.75391 11.7773C7.19727 11.7773 6.79688 12.1582 6.79688 12.7246C6.79688 13.2617 7.19727 13.6328 7.75391 13.6328Z", fillAlpha = 0.85f)
        }
        return _sFPlusCircleFill!!
    }

private var _sFPlusCircleFill: ImageVector? = null
