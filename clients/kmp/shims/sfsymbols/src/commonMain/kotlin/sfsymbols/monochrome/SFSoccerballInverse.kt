package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSoccerballInverse (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFSoccerballInverse: ImageVector
    get() {
        if (_sFSoccerballInverse != null) {
            return _sFSoccerballInverse!!
        }
        _sFSoccerballInverse = sfIcon(
            name = "Monochrome.SFSoccerballInverse",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M9.62891 23.5254C6.97266 22.7539 4.70703 21.0254 3.24219 18.7305L7.73438 18.6719L9.42383 16.4941L8.03711 12.1777L4.86328 10.8984L1.46484 12.8027C1.47461 10.0098 2.44141 7.5293 4.0625 5.58594L5.32227 9.74609L8.38867 10.9961L12.0801 8.1543L12.0801 4.9707L8.71094 2.23633C9.95117 1.74805 11.3086 1.48438 12.7051 1.48438C14.1113 1.48438 15.459 1.74805 16.6992 2.23633L13.3398 4.96094L13.3398 8.1543L17.0312 10.9961L20.0879 9.74609L21.3574 5.58594C22.9688 7.5293 23.9453 10.0098 23.9551 12.8027L20.5469 10.8984L17.3828 12.1777L15.9863 16.4941L17.6855 18.6719L22.168 18.7305C20.7129 21.0254 18.4473 22.7539 15.7812 23.5254L16.7188 19.4434L15.1953 17.5L10.2246 17.5L8.69141 19.4434ZM12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395Z", fillAlpha = 0.85f)
        }
        return _sFSoccerballInverse!!
    }

private var _sFSoccerballInverse: ImageVector? = null
