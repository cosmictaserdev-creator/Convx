package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTugriksign (monochrome)
 * Viewport: 17.832 x 22.6465
 */
public val SfSymbols.Monochrome.SFTugriksign: ImageVector
    get() {
        if (_sFTugriksign != null) {
            return _sFTugriksign!!
        }
        _sFTugriksign = sfIcon(
            name = "Monochrome.SFTugriksign",
            viewportWidth = 17.832f,
            viewportHeight = 22.6465f
        ) {
            addSfPath("M8.73047 22.6465C9.36523 22.6465 9.79492 22.207 9.79492 21.5723L9.79492 1.93359L16.4648 1.93359C17.0605 1.93359 17.4707 1.54297 17.4707 0.986328C17.4707 0.439453 17.0605 0.0390625 16.4648 0.0390625L1.02539 0.0390625C0.429688 0.0390625 0 0.439453 0 0.986328C0 1.5332 0.429688 1.93359 1.02539 1.93359L7.68555 1.93359L7.68555 21.5723C7.68555 22.207 8.11523 22.6465 8.73047 22.6465ZM3.44727 13.1445C3.57422 13.1445 3.71094 13.1152 3.85742 13.0469L14.209 8.75C14.5605 8.61328 14.7461 8.36914 14.7461 8.03711C14.7461 7.67578 14.4922 7.36328 14.0625 7.36328C13.9258 7.36328 13.7891 7.38281 13.6328 7.45117L3.28125 11.7383C2.92969 11.8848 2.76367 12.1191 2.76367 12.4609C2.76367 12.8516 3.04688 13.1445 3.44727 13.1445ZM3.44727 18.0762C3.57422 18.0762 3.71094 18.0469 3.85742 17.9785L14.209 13.6816C14.5605 13.5352 14.7461 13.3008 14.7461 12.9688C14.7461 12.6074 14.4922 12.2949 14.0625 12.2949C13.9258 12.2949 13.7891 12.3242 13.6328 12.3828L3.28125 16.6797C2.92969 16.8164 2.76367 17.0508 2.76367 17.3926C2.76367 17.7832 3.04688 18.0762 3.44727 18.0762Z", fillAlpha = 0.85f)
        }
        return _sFTugriksign!!
    }

private var _sFTugriksign: ImageVector? = null
