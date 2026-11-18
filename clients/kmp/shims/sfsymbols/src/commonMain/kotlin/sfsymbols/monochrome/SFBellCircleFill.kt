package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBellCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFBellCircleFill: ImageVector
    get() {
        if (_sFBellCircleFill != null) {
            return _sFBellCircleFill!!
        }
        _sFBellCircleFill = sfIcon(
            name = "Monochrome.SFBellCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM10.5957 18.4277C10.7031 19.4824 11.543 20.3125 12.7246 20.3125C13.9062 20.3125 14.7559 19.4824 14.8535 18.4277ZM10.7324 6.89453C8.87695 7.53906 8.14453 9.32617 8.04688 11.875C7.94922 12.8809 7.88086 14.0723 7.34375 14.6582C6.73828 15.3125 6.02539 15.9277 6.02539 16.6504C6.02539 17.0996 6.36719 17.4023 6.93359 17.4023L18.5156 17.4023C19.0918 17.4023 19.4238 17.0996 19.4238 16.6504C19.4238 15.9277 18.7305 15.3027 18.1152 14.6582C17.5586 14.0723 17.5 12.8809 17.4121 11.875C17.3145 9.32617 16.5723 7.53906 14.7266 6.89453C14.4727 5.95703 13.7402 5.23438 12.7246 5.23438C11.709 5.23438 10.9863 5.95703 10.7324 6.89453Z", fillAlpha = 0.85f)
        }
        return _sFBellCircleFill!!
    }

private var _sFBellCircleFill: ImageVector? = null
