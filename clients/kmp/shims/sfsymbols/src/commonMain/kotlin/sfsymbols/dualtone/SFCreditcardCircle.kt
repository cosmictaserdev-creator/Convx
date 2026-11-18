package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCreditcardCircle (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFCreditcardCircle: ImageVector
    get() {
        if (_sFCreditcardCircle != null) {
            return _sFCreditcardCircle!!
        }
        _sFCreditcardCircle = sfIcon(
            name = "Dualtone.SFCreditcardCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.425f)
            addSfPath("M7.40234 17.8809L18.0078 17.8809C19.2285 17.8809 19.8535 17.2656 19.8535 16.0547L19.8535 11.3184L5.56641 11.3184L5.56641 16.0547C5.56641 17.2656 6.18164 17.8809 7.40234 17.8809ZM8.02734 16.0254C7.67578 16.0254 7.43164 15.7812 7.43164 15.4395L7.43164 14.3066C7.43164 13.9551 7.67578 13.7207 8.02734 13.7207L9.53125 13.7207C9.89258 13.7207 10.127 13.9551 10.127 14.3066L10.127 15.4395C10.127 15.7812 9.89258 16.0254 9.53125 16.0254ZM5.56641 9.94141L19.8535 9.94141L19.8535 9.4043C19.8535 8.20312 19.2285 7.57812 18.0078 7.57812L7.40234 7.57812C6.18164 7.57812 5.56641 8.19336 5.56641 9.4043Z", fillAlpha = 0.85f)
        }
        return _sFCreditcardCircle!!
    }

private var _sFCreditcardCircle: ImageVector? = null
