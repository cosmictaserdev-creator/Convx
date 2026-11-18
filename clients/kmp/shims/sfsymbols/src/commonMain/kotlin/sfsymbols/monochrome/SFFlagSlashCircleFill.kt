package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlagSlashCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFFlagSlashCircleFill: ImageVector
    get() {
        if (_sFFlagSlashCircleFill != null) {
            return _sFFlagSlashCircleFill!!
        }
        _sFFlagSlashCircleFill = sfIcon(
            name = "Monochrome.SFFlagSlashCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM7.22656 19.1016C7.22656 19.3848 7.4707 19.6387 7.77344 19.6387C8.08594 19.6387 8.30078 19.3945 8.30078 19.1016L8.30078 15.2148C8.55469 15.1074 9.08203 14.9512 9.91211 14.9512C11.4205 14.9512 12.5556 15.4365 13.7911 15.7735L7.22656 9.20898ZM6.2793 6.29883C6.07422 6.51367 6.08398 6.85547 6.2793 7.05078L18.4082 19.1699C18.6035 19.375 18.9551 19.375 19.1602 19.1699C19.3652 18.9453 19.3652 18.623 19.1602 18.418L7.04102 6.29883C6.83594 6.08398 6.50391 6.08398 6.2793 6.29883ZM8.8113 6.84934L17.7468 15.7921C17.7621 15.7856 17.7775 15.7786 17.793 15.7715C18.3105 15.5176 18.5547 15.1855 18.5547 14.6484L18.5547 8.27148C18.5547 7.91016 18.3594 7.65625 17.8906 7.65625C17.5195 7.65625 16.9434 7.94922 15.8594 7.94922C13.6426 7.94922 12.1875 6.77734 9.84375 6.77734C9.40693 6.77734 9.07953 6.80274 8.8113 6.84934Z", fillAlpha = 0.85f)
        }
        return _sFFlagSlashCircleFill!!
    }

private var _sFFlagSlashCircleFill: ImageVector? = null
