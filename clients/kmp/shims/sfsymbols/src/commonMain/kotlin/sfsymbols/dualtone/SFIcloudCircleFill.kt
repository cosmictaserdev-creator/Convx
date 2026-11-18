package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIcloudCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFIcloudCircleFill: ImageVector
    get() {
        if (_sFIcloudCircleFill != null) {
            return _sFIcloudCircleFill!!
        }
        _sFIcloudCircleFill = sfIcon(
            name = "Dualtone.SFIcloudCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M8.07617 17.4609C6.15234 17.4609 4.76562 16.2793 4.76562 14.2285C4.76562 12.793 5.67383 11.6309 6.875 11.1621C7.01172 9.82422 8.125 8.86719 9.46289 8.86719C9.62891 8.86719 9.86328 8.89648 10.0488 8.93555C10.8984 7.71484 12.3145 6.95312 13.9062 6.95312C16.6211 6.95312 18.6621 9.07227 18.6621 11.6797C18.6621 11.8164 18.6621 11.9922 18.6426 12.1484C19.8047 12.3926 20.6934 13.457 20.6934 14.7656C20.6934 16.2695 19.5215 17.4609 17.9688 17.4609Z", fillAlpha = 0.85f)
        }
        return _sFIcloudCircleFill!!
    }

private var _sFIcloudCircleFill: ImageVector? = null
