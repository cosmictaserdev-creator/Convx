package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLightbulbCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFLightbulbCircleFill: ImageVector
    get() {
        if (_sFLightbulbCircleFill != null) {
            return _sFLightbulbCircleFill!!
        }
        _sFLightbulbCircleFill = sfIcon(
            name = "Monochrome.SFLightbulbCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM10.9961 19.4922C11.0547 20.1465 11.7773 20.5859 12.7148 20.5859C13.6426 20.5859 14.3652 20.1465 14.4434 19.4922ZM10.7031 17.8418C10.4395 17.8418 10.2148 18.0664 10.2148 18.3301C10.2148 18.5938 10.4395 18.8086 10.7031 18.8086L14.7266 18.8086C14.9902 18.8086 15.2148 18.5938 15.2148 18.3301C15.2148 18.0664 14.9902 17.8418 14.7266 17.8418ZM8.20312 8.96484C8.20312 11.5137 9.92188 12.1875 10.1855 16.7676C10.2051 17.002 10.3613 17.1582 10.5957 17.1582L14.8242 17.1582C15.0684 17.1582 15.2246 17.002 15.2441 16.7676C15.4883 12.1875 17.2266 11.5625 17.2266 9.01367C17.2266 6.70898 15.2344 4.85352 12.7051 4.85352C10.1855 4.85352 8.20312 6.66016 8.20312 8.96484Z", fillAlpha = 0.85f)
        }
        return _sFLightbulbCircleFill!!
    }

private var _sFLightbulbCircleFill: ImageVector? = null
