package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlagSlashFill (dualtone)
 * Viewport: 27.3682 x 32.041
 */
public val SfSymbols.Dualtone.SFFlagSlashFill: ImageVector
    get() {
        if (_sFFlagSlashFill != null) {
            return _sFFlagSlashFill!!
        }
        _sFFlagSlashFill = sfIcon(
            name = "Dualtone.SFFlagSlashFill",
            viewportWidth = 27.3682f,
            viewportHeight = 32.041f
        ) {
            addSfPath("M19.9619 21.8181C15.6902 21.7378 12.8606 19.5605 8.33254 19.5605C6.40871 19.5605 5.23684 20.0488 4.87551 20.1562L4.87551 27.3633C4.87551 27.8223 4.51418 28.1934 4.0552 28.1934C3.61574 28.1934 3.23488 27.8125 3.23488 27.3633L3.23488 6.49414C3.23488 6.05024 3.33554 5.68687 3.51661 5.38766ZM20.0611 6.14258C22.229 6.14258 23.3911 5.5957 23.9966 5.5957C24.6509 5.5957 25.1587 5.94727 25.1587 6.58203L25.1587 19.209C25.1587 19.7219 25.0243 20.1274 24.7885 20.4509L8.208 3.87791C12.7991 3.88757 15.6476 6.14258 20.0611 6.14258Z", fillAlpha = 0.425f)
            addSfPath("M24.5825 24.5215C24.9146 24.8438 25.4517 24.8438 25.7642 24.5215C26.0864 24.1895 26.0864 23.6719 25.7642 23.3398L4.01613 1.60156C3.69387 1.2793 3.15676 1.26953 2.82473 1.60156C2.51223 1.92383 2.51223 2.46094 2.82473 2.7832Z", fillAlpha = 0.85f)
        }
        return _sFFlagSlashFill!!
    }

private var _sFFlagSlashFill: ImageVector? = null
