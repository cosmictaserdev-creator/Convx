package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPillsCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFPillsCircleFill: ImageVector
    get() {
        if (_sFPillsCircleFill != null) {
            return _sFPillsCircleFill!!
        }
        _sFPillsCircleFill = sfIcon(
            name = "Monochrome.SFPillsCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM13.2422 16.3477C13.457 18.0273 14.9316 19.3848 16.7188 19.3848C18.5449 19.3848 20.0293 18.0273 20.2344 16.3477ZM5.67383 11.748C4.44336 12.998 4.41406 14.5996 5.60547 15.8008C6.81641 16.9922 8.41797 16.9727 9.64844 15.7324L11.8359 13.5547L7.85156 9.56055ZM13.2422 15.3809L20.2344 15.3809C20.0293 13.6816 18.5352 12.334 16.7188 12.334C14.9414 12.334 13.457 13.6816 13.2422 15.3809ZM10.6934 6.70898L8.54492 8.87695L12.5195 12.8516L14.6875 10.6934C15.9473 9.46289 15.957 7.8418 14.7363 6.66992C13.5254 5.44922 11.9336 5.46875 10.6934 6.70898Z", fillAlpha = 0.85f)
        }
        return _sFPillsCircleFill!!
    }

private var _sFPillsCircleFill: ImageVector? = null
