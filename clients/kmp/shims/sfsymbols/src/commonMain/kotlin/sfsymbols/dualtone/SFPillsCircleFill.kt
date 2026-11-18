package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPillsCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFPillsCircleFill: ImageVector
    get() {
        if (_sFPillsCircleFill != null) {
            return _sFPillsCircleFill!!
        }
        _sFPillsCircleFill = sfIcon(
            name = "Dualtone.SFPillsCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M12.5195 12.8516L8.54492 8.87695L10.6934 6.70898C11.9336 5.46875 13.5254 5.44922 14.7363 6.66992C15.957 7.8418 15.9473 9.46289 14.6875 10.6934ZM11.8359 13.5547L9.64844 15.7324C8.41797 16.9727 6.81641 16.9922 5.60547 15.8008C4.41406 14.5996 4.44336 12.998 5.67383 11.748L7.85156 9.56055ZM20.2344 15.3809L13.2422 15.3809C13.457 13.6816 14.9414 12.334 16.7188 12.334C18.5352 12.334 20.0293 13.6816 20.2344 15.3809ZM20.2344 16.3477C20.0293 18.0273 18.5449 19.3848 16.7188 19.3848C14.9316 19.3848 13.457 18.0273 13.2422 16.3477Z", fillAlpha = 0.85f)
        }
        return _sFPillsCircleFill!!
    }

private var _sFPillsCircleFill: ImageVector? = null
