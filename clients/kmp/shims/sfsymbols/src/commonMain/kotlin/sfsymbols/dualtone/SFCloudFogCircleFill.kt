package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCloudFogCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFCloudFogCircleFill: ImageVector
    get() {
        if (_sFCloudFogCircleFill != null) {
            return _sFCloudFogCircleFill!!
        }
        _sFCloudFogCircleFill = sfIcon(
            name = "Dualtone.SFCloudFogCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M8.57422 14.3555C6.74805 14.3555 5.69336 13.1738 5.69336 11.7285C5.69336 10.4199 6.57227 9.46289 7.70508 9.13086C7.91016 6.80664 9.83398 4.95117 12.1484 4.95117C13.9844 4.95117 15.4004 5.97656 16.1914 7.5293C18.2812 7.5293 19.8926 8.96484 19.8926 10.9277C19.8926 12.8223 18.3594 14.3555 16.3574 14.3555ZM7.97852 17.0996C7.69531 17.0996 7.45117 16.875 7.45117 16.582C7.45117 16.2793 7.69531 16.0449 7.97852 16.0449L17.4414 16.0449C17.7441 16.0449 17.9688 16.2793 17.9688 16.582C17.9688 16.875 17.7441 17.0996 17.4414 17.0996ZM7.97852 19.7266C7.69531 19.7266 7.45117 19.502 7.45117 19.1992C7.45117 18.9062 7.69531 18.6719 7.97852 18.6719L17.4414 18.6719C17.7441 18.6719 17.9688 18.8965 17.9688 19.1992C17.9688 19.502 17.7441 19.7266 17.4414 19.7266Z", fillAlpha = 0.85f)
        }
        return _sFCloudFogCircleFill!!
    }

private var _sFCloudFogCircleFill: ImageVector? = null
