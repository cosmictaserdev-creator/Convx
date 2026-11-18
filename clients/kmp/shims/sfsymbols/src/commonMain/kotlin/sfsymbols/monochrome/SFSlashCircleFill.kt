package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSlashCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFSlashCircleFill: ImageVector
    get() {
        if (_sFSlashCircleFill != null) {
            return _sFSlashCircleFill!!
        }
        _sFSlashCircleFill = sfIcon(
            name = "Monochrome.SFSlashCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM16.5918 7.48047L7.51953 16.6406C7.28516 16.8848 7.1582 17.1582 7.1582 17.4121C7.1582 17.9004 7.50977 18.3398 8.05664 18.3398C8.36914 18.3398 8.59375 18.2129 8.83789 17.9688L17.9102 8.79883C18.1445 8.56445 18.2715 8.29102 18.2715 8.03711C18.2715 7.51953 17.8516 7.10938 17.3438 7.10938C17.0801 7.10938 16.8164 7.23633 16.5918 7.48047Z", fillAlpha = 0.85f)
        }
        return _sFSlashCircleFill!!
    }

private var _sFSlashCircleFill: ImageVector? = null
