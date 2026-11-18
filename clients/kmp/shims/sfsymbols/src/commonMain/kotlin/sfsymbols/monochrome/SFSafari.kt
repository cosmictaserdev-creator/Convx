package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSafari (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFSafari: ImageVector
    get() {
        if (_sFSafari != null) {
            return _sFSafari!!
        }
        _sFSafari = sfIcon(
            name = "Monochrome.SFSafari",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M7.30469 19.1016L14.6387 15.459C15.0195 15.2832 15.2832 15.0098 15.4688 14.6387L19.0918 7.31445C19.4824 6.52344 18.9355 5.92773 18.1055 6.32812L10.791 9.95117C10.4297 10.127 10.1758 10.3711 9.96094 10.791L6.31836 18.125C5.94727 18.8965 6.52344 19.4824 7.30469 19.1016ZM12.7148 14.2676C11.8652 14.2676 11.1719 13.5742 11.1719 12.7246C11.1719 11.875 11.8652 11.1816 12.7148 11.1816C13.5645 11.1816 14.2578 11.875 14.2578 12.7246C14.2578 13.5742 13.5645 14.2676 12.7148 14.2676Z", fillAlpha = 0.85f)
        }
        return _sFSafari!!
    }

private var _sFSafari: ImageVector? = null
