package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBookFill (monochrome)
 * Viewport: 28.8574 x 22.6855
 */
public val SfSymbols.Monochrome.SFBookFill: ImageVector
    get() {
        if (_sFBookFill != null) {
            return _sFBookFill!!
        }
        _sFBookFill = sfIcon(
            name = "Monochrome.SFBookFill",
            viewportWidth = 28.8574f,
            viewportHeight = 22.6855f
        ) {
            addSfPath("M7.08984 0C4.33594 0 1.44531 1.24023 0.146484 3.41797C0.00976562 3.66211 0 3.80859 0 4.22852L0 21.8359C0 22.334 0.322266 22.6562 0.859375 22.6562C1.14258 22.6562 1.38672 22.5586 1.60156 22.373C3.1543 20.9863 5.32227 20.293 7.48047 20.293C9.56055 20.293 11.377 21.0742 12.627 22.1484C12.7637 22.2656 12.9199 22.3242 13.0566 22.3242C13.3203 22.3242 13.5352 22.1289 13.5352 21.8262L13.5352 3.4668C13.5352 3.14453 13.5059 3.06641 13.2812 2.76367C11.9824 1.00586 9.62891 0 7.08984 0ZM21.416 0C18.8672 0 16.5137 1.00586 15.2148 2.76367C15 3.06641 14.9707 3.14453 14.9707 3.4668L14.9707 21.8262C14.9707 22.1289 15.1758 22.3242 15.4395 22.3242C15.5762 22.3242 15.7324 22.2656 15.8691 22.1484C17.1191 21.0742 18.9355 20.293 21.0156 20.293C23.1836 20.293 25.3418 20.9863 26.9043 22.373C27.1094 22.5586 27.3535 22.6562 27.6367 22.6562C28.1738 22.6562 28.4961 22.334 28.4961 21.8359L28.4961 4.22852C28.4961 3.80859 28.4863 3.65234 28.3496 3.41797C27.0508 1.24023 24.1699 0 21.416 0Z", fillAlpha = 0.85f)
        }
        return _sFBookFill!!
    }

private var _sFBookFill: ImageVector? = null
