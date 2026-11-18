package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF8CircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SF8CircleFill: ImageVector
    get() {
        if (_sF8CircleFill != null) {
            return _sF8CircleFill!!
        }
        _sF8CircleFill = sfIcon(
            name = "Dualtone.SF8CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.6953 18.8086C10.0391 18.8086 8.25195 17.4121 8.25195 15.3711C8.25195 13.9355 9.28711 12.7344 10.8203 12.4316L10.8203 12.3535C9.53125 11.9727 8.74023 10.9668 8.74023 9.73633C8.74023 7.91016 10.3906 6.64062 12.7148 6.64062C15.0488 6.64062 16.6895 7.91016 16.6895 9.73633C16.6895 10.9668 15.8789 11.9922 14.6191 12.3535L14.6191 12.4316C16.1328 12.7344 17.1875 13.9258 17.1875 15.3613C17.1875 17.4023 15.3516 18.8086 12.6953 18.8086ZM12.7246 17.5488C14.3262 17.5488 15.498 16.6016 15.498 15.2734C15.498 13.9844 14.3555 13.0469 12.7246 13.0469C11.1328 13.0469 9.95117 13.9941 9.95117 15.2734C9.95117 16.6016 11.123 17.5488 12.7246 17.5488ZM12.7148 11.8359C14.0723 11.8359 15.0391 11.0156 15.0391 9.86328C15.0391 8.7207 14.082 7.91016 12.7148 7.91016C11.3477 7.91016 10.3906 8.7207 10.3906 9.86328C10.3906 11.0156 11.3574 11.8359 12.7148 11.8359Z", fillAlpha = 0.85f)
        }
        return _sF8CircleFill!!
    }

private var _sF8CircleFill: ImageVector? = null
