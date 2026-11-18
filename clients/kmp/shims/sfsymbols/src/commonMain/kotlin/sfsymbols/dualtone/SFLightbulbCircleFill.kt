package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightbulbCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFLightbulbCircleFill: ImageVector
    get() {
        if (_sFLightbulbCircleFill != null) {
            return _sFLightbulbCircleFill!!
        }
        _sFLightbulbCircleFill = sfIcon(
            name = "Dualtone.SFLightbulbCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M8.20312 8.96484C8.20312 6.66016 10.1855 4.85352 12.7051 4.85352C15.2344 4.85352 17.2266 6.70898 17.2266 9.01367C17.2266 11.5625 15.4883 12.1875 15.2441 16.7676C15.2246 17.002 15.0684 17.1582 14.8242 17.1582L10.5957 17.1582C10.3613 17.1582 10.2051 17.002 10.1855 16.7676C9.92188 12.1875 8.20312 11.5137 8.20312 8.96484ZM10.7031 18.8086C10.4395 18.8086 10.2148 18.5938 10.2148 18.3301C10.2148 18.0664 10.4395 17.8418 10.7031 17.8418L14.7266 17.8418C14.9902 17.8418 15.2148 18.0664 15.2148 18.3301C15.2148 18.5938 14.9902 18.8086 14.7266 18.8086ZM12.7148 20.5859C11.7773 20.5859 11.0547 20.1465 10.9961 19.4922L14.4434 19.4922C14.3652 20.1465 13.6426 20.5859 12.7148 20.5859Z", fillAlpha = 0.85f)
        }
        return _sFLightbulbCircleFill!!
    }

private var _sFLightbulbCircleFill: ImageVector? = null
