package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEraser (monochrome)
 * Viewport: 25.1122 x 24.9417
 */
public val SfSymbols.Monochrome.SFEraser: ImageVector
    get() {
        if (_sFEraser != null) {
            return _sFEraser!!
        }
        _sFEraser = sfIcon(
            name = "Monochrome.SFEraser",
            viewportWidth = 25.1122f,
            viewportHeight = 24.9417f
        ) {
            addSfPath("M1.35979 18.423L6.3305 23.3839C8.08831 25.1418 10.1391 25.2101 11.8188 23.5304L23.9574 11.3722C25.0219 10.3273 25.0121 8.80386 23.9282 7.72964L17.0141 0.825345C15.9301-0.258639 14.4262-0.278171 13.3715 0.786283L1.21331 12.9347C-0.456613 14.6144-0.39802 16.6652 1.35979 18.423ZM2.5805 17.2023C1.60393 16.216 1.56487 15.0441 2.49261 14.0968L14.5336 2.06558C14.9047 1.68472 15.4321 1.68472 15.8032 2.05581L22.6977 8.95035C23.0688 9.32144 23.059 9.83902 22.6879 10.2101L10.6469 22.2511C9.7094 23.1886 8.53753 23.1496 7.5512 22.1632ZM3.1469 12.2316L12.5219 21.6164L13.6059 20.5226L4.25042 11.1671Z", fillAlpha = 0.85f)
        }
        return _sFEraser!!
    }

private var _sFEraser: ImageVector? = null
