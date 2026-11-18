package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHeartSlashCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFHeartSlashCircleFill: ImageVector
    get() {
        if (_sFHeartSlashCircleFill != null) {
            return _sFHeartSlashCircleFill!!
        }
        _sFHeartSlashCircleFill = sfIcon(
            name = "Monochrome.SFHeartSlashCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM5.85938 10.8984C5.85938 14.3652 9.58008 17.5195 12.1191 19.1211C12.3242 19.2578 12.5879 19.4043 12.7344 19.4043C12.8906 19.4043 13.1152 19.2578 13.3105 19.1211C13.9838 18.6696 14.7506 18.1146 15.5108 17.4737L6.57096 8.53385C6.11728 9.17549 5.85938 9.98761 5.85938 10.8984ZM6.2793 6.2793C6.07422 6.49414 6.08398 6.83594 6.2793 7.03125L18.4082 19.1602C18.6035 19.3555 18.9551 19.3652 19.1602 19.1602C19.3652 18.9355 19.3652 18.6133 19.1602 18.4082L7.04102 6.2793C6.83594 6.07422 6.50391 6.07422 6.2793 6.2793ZM12.7148 8.94531C12.0801 7.80273 10.9961 6.99219 9.60938 6.99219C9.40433 6.99219 9.20452 7.00695 9.01279 7.04014L17.5031 15.5304C18.6935 14.1538 19.5703 12.5704 19.5703 10.8984C19.5703 8.64258 17.9883 6.99219 15.8203 6.99219C14.4531 6.99219 13.3496 7.80273 12.7148 8.94531Z", fillAlpha = 0.85f)
        }
        return _sFHeartSlashCircleFill!!
    }

private var _sFHeartSlashCircleFill: ImageVector? = null
