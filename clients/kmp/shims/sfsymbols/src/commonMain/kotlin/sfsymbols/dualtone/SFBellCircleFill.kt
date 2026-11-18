package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBellCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFBellCircleFill: ImageVector
    get() {
        if (_sFBellCircleFill != null) {
            return _sFBellCircleFill!!
        }
        _sFBellCircleFill = sfIcon(
            name = "Dualtone.SFBellCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M6.93359 17.4023C6.36719 17.4023 6.02539 17.0996 6.02539 16.6504C6.02539 15.9277 6.73828 15.3125 7.34375 14.6582C7.88086 14.0723 7.94922 12.8809 8.04688 11.875C8.14453 9.32617 8.87695 7.53906 10.7324 6.89453C10.9863 5.95703 11.709 5.23438 12.7246 5.23438C13.7402 5.23438 14.4727 5.95703 14.7266 6.89453C16.5723 7.53906 17.3145 9.32617 17.4121 11.875C17.5 12.8809 17.5586 14.0723 18.1152 14.6582C18.7305 15.3027 19.4238 15.9277 19.4238 16.6504C19.4238 17.0996 19.0918 17.4023 18.5156 17.4023ZM12.7246 20.3125C11.543 20.3125 10.7031 19.4824 10.5957 18.4277L14.8535 18.4277C14.7559 19.4824 13.9062 20.3125 12.7246 20.3125Z", fillAlpha = 0.85f)
        }
        return _sFBellCircleFill!!
    }

private var _sFBellCircleFill: ImageVector? = null
