package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCreditcardCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFCreditcardCircleFill: ImageVector
    get() {
        if (_sFCreditcardCircleFill != null) {
            return _sFCreditcardCircleFill!!
        }
        _sFCreditcardCircleFill = sfIcon(
            name = "Monochrome.SFCreditcardCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM5.39062 11.2891L5.39062 16.1328C5.39062 17.3926 6.03516 18.0078 7.28516 18.0078L18.1445 18.0078C19.3945 18.0078 20.0391 17.3828 20.0391 16.1328L20.0391 11.2891ZM10.0781 14.3457L10.0781 15.5078C10.0781 15.8691 9.83398 16.1035 9.46289 16.1035L7.91992 16.1035C7.56836 16.1035 7.31445 15.8691 7.31445 15.5078L7.31445 14.3457C7.31445 13.9941 7.56836 13.75 7.91992 13.75L9.46289 13.75C9.83398 13.75 10.0781 13.9941 10.0781 14.3457ZM7.28516 7.45117C6.03516 7.45117 5.39062 8.07617 5.39062 9.32617L5.39062 9.87305L20.0391 9.87305L20.0391 9.32617C20.0391 8.07617 19.3945 7.45117 18.1445 7.45117Z", fillAlpha = 0.85f)
        }
        return _sFCreditcardCircleFill!!
    }

private var _sFCreditcardCircleFill: ImageVector? = null
