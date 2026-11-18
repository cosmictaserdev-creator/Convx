package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInfinityCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFInfinityCircleFill: ImageVector
    get() {
        if (_sFInfinityCircleFill != null) {
            return _sFInfinityCircleFill!!
        }
        _sFInfinityCircleFill = sfIcon(
            name = "Dualtone.SFInfinityCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M4.07227 12.7246C4.07227 10.3418 5.61523 8.7207 7.86133 8.7207C9.10156 8.73047 10.1172 9.27734 11.2305 10.3418L12.7344 11.7578L14.2285 10.3418C15.4004 9.21875 16.3965 8.7207 17.5977 8.7207C19.8535 8.7207 21.3965 10.3418 21.3965 12.7246C21.3965 15.1074 19.8535 16.709 17.5977 16.709C16.4062 16.709 15.4102 16.2207 14.2285 15.1074L12.7344 13.6816L11.2305 15.1074C10.1074 16.1621 9.0918 16.709 7.86133 16.709C5.61523 16.709 4.07227 15.1074 4.07227 12.7246ZM5.47852 12.7246C5.47852 14.2773 6.43555 15.3125 7.86133 15.3125C8.67188 15.3125 9.3457 14.9219 10.2441 14.0918L11.7285 12.7246L10.2441 11.3477C9.29688 10.4688 8.63281 10.127 7.86133 10.1172C6.43555 10.1172 5.47852 11.1621 5.47852 12.7246ZM13.7305 12.7246L15.2246 14.0918C16.1133 14.9219 16.7871 15.3125 17.5977 15.3125C19.0332 15.3125 19.9902 14.2773 19.9902 12.7246C19.9902 11.1621 19.0234 10.1172 17.5977 10.1172C16.8262 10.127 16.1621 10.4688 15.2246 11.3477Z", fillAlpha = 0.85f)
        }
        return _sFInfinityCircleFill!!
    }

private var _sFInfinityCircleFill: ImageVector? = null
