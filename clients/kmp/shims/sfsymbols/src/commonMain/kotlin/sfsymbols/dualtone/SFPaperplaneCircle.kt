package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPaperplaneCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPaperplaneCircle: ImageVector
    get() {
        if (_sFPaperplaneCircle != null) {
            return _sFPaperplaneCircle!!
        }
        _sFPaperplaneCircle = sfIcon(
            name = "Dualtone.SFPaperplaneCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M12.9883 20.7422C13.4473 20.7422 13.7793 20.3711 14.0137 19.7461L18.75 7.35352C18.8574 7.07031 18.9258 6.82617 18.9258 6.61133C18.9258 6.20117 18.6719 5.94727 18.2617 5.94727C18.0469 5.94727 17.8027 6.01562 17.5195 6.12305L5.07812 10.8789C4.52148 11.084 4.13086 11.416 4.13086 11.8848C4.13086 12.4609 4.55078 12.666 5.19531 12.8711L9.31641 14.1113C9.7168 14.2383 9.9707 14.2188 10.2246 13.9648L17.9785 6.65039C18.0664 6.57227 18.1543 6.5918 18.2324 6.64062C18.3203 6.69922 18.3008 6.80664 18.2227 6.89453L10.9277 14.6777C10.6836 14.9316 10.6543 15.166 10.7617 15.5762L11.9922 19.6191C12.207 20.2832 12.3926 20.7422 12.9883 20.7422Z", fillAlpha = 0.85f)
        }
        return _sFPaperplaneCircle!!
    }

private var _sFPaperplaneCircle: ImageVector? = null
