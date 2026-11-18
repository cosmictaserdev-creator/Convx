package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCreditcardCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFCreditcardCircleFill: ImageVector
    get() {
        if (_sFCreditcardCircleFill != null) {
            return _sFCreditcardCircleFill!!
        }
        _sFCreditcardCircleFill = sfIcon(
            name = "Dualtone.SFCreditcardCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M7.28516 18.0078C6.03516 18.0078 5.39062 17.3926 5.39062 16.1328L5.39062 11.2891L20.0391 11.2891L20.0391 16.1328C20.0391 17.3828 19.3945 18.0078 18.1445 18.0078ZM7.91992 16.1035L9.46289 16.1035C9.83398 16.1035 10.0781 15.8691 10.0781 15.5078L10.0781 14.3457C10.0781 13.9941 9.83398 13.75 9.46289 13.75L7.91992 13.75C7.56836 13.75 7.31445 13.9941 7.31445 14.3457L7.31445 15.5078C7.31445 15.8691 7.56836 16.1035 7.91992 16.1035ZM5.39062 9.87305L5.39062 9.32617C5.39062 8.07617 6.03516 7.45117 7.28516 7.45117L18.1445 7.45117C19.3945 7.45117 20.0391 8.07617 20.0391 9.32617L20.0391 9.87305Z", fillAlpha = 0.85f)
        }
        return _sFCreditcardCircleFill!!
    }

private var _sFCreditcardCircleFill: ImageVector? = null
