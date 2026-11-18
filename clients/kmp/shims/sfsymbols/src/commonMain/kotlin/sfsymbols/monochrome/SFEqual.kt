package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEqual (monochrome)
 * Viewport: 17.6367 x 9.85352
 */
public val SfSymbols.Monochrome.SFEqual: ImageVector
    get() {
        if (_sFEqual != null) {
            return _sFEqual!!
        }
        _sFEqual = sfIcon(
            name = "Monochrome.SFEqual",
            viewportWidth = 17.6367f,
            viewportHeight = 9.85352f
        ) {
            addSfPath("M0.957031 1.93359L16.3281 1.93359C16.8359 1.93359 17.2754 1.50391 17.2754 0.986328C17.2754 0.458984 16.8359 0.0292969 16.3281 0.0292969L0.957031 0.0292969C0.439453 0.0292969 0 0.458984 0 0.986328C0 1.50391 0.439453 1.93359 0.957031 1.93359ZM0.957031 9.85352L16.3281 9.85352C16.8359 9.85352 17.2754 9.42383 17.2754 8.89648C17.2754 8.37891 16.8359 7.94922 16.3281 7.94922L0.957031 7.94922C0.439453 7.94922 0 8.37891 0 8.89648C0 9.42383 0.439453 9.85352 0.957031 9.85352Z", fillAlpha = 0.85f)
        }
        return _sFEqual!!
    }

private var _sFEqual: ImageVector? = null
