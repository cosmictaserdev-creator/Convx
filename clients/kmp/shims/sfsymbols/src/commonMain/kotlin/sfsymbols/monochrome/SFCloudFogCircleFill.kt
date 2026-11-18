package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCloudFogCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFCloudFogCircleFill: ImageVector
    get() {
        if (_sFCloudFogCircleFill != null) {
            return _sFCloudFogCircleFill!!
        }
        _sFCloudFogCircleFill = sfIcon(
            name = "Monochrome.SFCloudFogCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM7.97852 18.6719C7.69531 18.6719 7.45117 18.9062 7.45117 19.1992C7.45117 19.502 7.69531 19.7266 7.97852 19.7266L17.4414 19.7266C17.7441 19.7266 17.9688 19.502 17.9688 19.1992C17.9688 18.8965 17.7441 18.6719 17.4414 18.6719ZM7.97852 16.0449C7.69531 16.0449 7.45117 16.2793 7.45117 16.582C7.45117 16.875 7.69531 17.0996 7.97852 17.0996L17.4414 17.0996C17.7441 17.0996 17.9688 16.875 17.9688 16.582C17.9688 16.2793 17.7441 16.0449 17.4414 16.0449ZM7.70508 9.13086C6.57227 9.46289 5.69336 10.4199 5.69336 11.7285C5.69336 13.1738 6.74805 14.3555 8.57422 14.3555L16.3574 14.3555C18.3594 14.3555 19.8926 12.8223 19.8926 10.9277C19.8926 8.96484 18.2812 7.5293 16.1914 7.5293C15.4004 5.97656 13.9844 4.95117 12.1484 4.95117C9.83398 4.95117 7.91016 6.80664 7.70508 9.13086Z", fillAlpha = 0.85f)
        }
        return _sFCloudFogCircleFill!!
    }

private var _sFCloudFogCircleFill: ImageVector? = null
