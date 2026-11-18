package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSoccerballCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSoccerballCircleFill: ImageVector
    get() {
        if (_sFSoccerballCircleFill != null) {
            return _sFSoccerballCircleFill!!
        }
        _sFSoccerballCircleFill = sfIcon(
            name = "Dualtone.SFSoccerballCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7246 19.4434C9.04297 19.4434 6.00586 16.4062 6.00586 12.7246C6.00586 9.04297 9.0332 6.00586 12.7148 6.00586C16.4062 6.00586 19.4434 9.04297 19.4434 12.7246C19.4434 16.4062 16.4062 19.4434 12.7246 19.4434ZM12.7246 6.93359C12.0117 6.93359 11.2988 7.07031 10.6543 7.32422L12.3438 8.67188L12.3438 10.4102L10.5469 11.7871L8.89648 11.123L8.26172 9.04297C7.43164 10.0488 6.92383 11.3281 6.92383 12.7637L8.60352 11.8262L10.3418 12.5293L11.0156 14.6191L10.0879 15.791L7.8418 15.8203C8.59375 17.0117 9.76562 17.9004 11.1328 18.291L10.6641 16.2695L11.5234 15.1855L13.9258 15.1855L14.7754 16.2793L14.3066 18.291C15.6836 17.9004 16.8457 17.0117 17.6074 15.8203L15.3613 15.791L14.4336 14.6094L15.1074 12.5293L16.8457 11.8262L18.5254 12.7637C18.5254 11.3281 18.0176 10.0488 17.1875 9.04297L16.543 11.1328L14.9023 11.7871L13.0957 10.4102L13.0957 8.67188L14.7949 7.32422C14.1406 7.06055 13.4473 6.93359 12.7246 6.93359Z", fillAlpha = 0.85f)
        }
        return _sFSoccerballCircleFill!!
    }

private var _sFSoccerballCircleFill: ImageVector? = null
