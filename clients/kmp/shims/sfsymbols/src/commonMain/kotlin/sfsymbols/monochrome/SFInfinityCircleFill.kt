package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInfinityCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFInfinityCircleFill: ImageVector
    get() {
        if (_sFInfinityCircleFill != null) {
            return _sFInfinityCircleFill!!
        }
        _sFInfinityCircleFill = sfIcon(
            name = "Monochrome.SFInfinityCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM14.2285 10.3418L12.7344 11.7578L11.2305 10.3418C10.1172 9.27734 9.10156 8.73047 7.86133 8.7207C5.61523 8.7207 4.07227 10.3418 4.07227 12.7246C4.07227 15.1074 5.61523 16.709 7.86133 16.709C9.0918 16.709 10.1074 16.1621 11.2305 15.1074L12.7344 13.6816L14.2285 15.1074C15.4102 16.2207 16.4062 16.709 17.5977 16.709C19.8535 16.709 21.3965 15.1074 21.3965 12.7246C21.3965 10.3418 19.8535 8.7207 17.5977 8.7207C16.3965 8.7207 15.4004 9.21875 14.2285 10.3418ZM10.2441 11.3477L11.7285 12.7246L10.2441 14.0918C9.3457 14.9219 8.67188 15.3125 7.86133 15.3125C6.43555 15.3125 5.47852 14.2773 5.47852 12.7246C5.47852 11.1621 6.43555 10.1172 7.86133 10.1172C8.63281 10.127 9.29688 10.4688 10.2441 11.3477ZM19.9902 12.7246C19.9902 14.2773 19.0332 15.3125 17.5977 15.3125C16.7871 15.3125 16.1133 14.9219 15.2246 14.0918L13.7305 12.7246L15.2246 11.3477C16.1621 10.4688 16.8262 10.127 17.5977 10.1172C19.0234 10.1172 19.9902 11.1621 19.9902 12.7246Z", fillAlpha = 0.85f)
        }
        return _sFInfinityCircleFill!!
    }

private var _sFInfinityCircleFill: ImageVector? = null
