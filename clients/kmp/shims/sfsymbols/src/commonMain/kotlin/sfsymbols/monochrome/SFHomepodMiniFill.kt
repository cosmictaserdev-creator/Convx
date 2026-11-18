package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHomepodMiniFill (monochrome)
 * Viewport: 25.0293 x 22.6465
 */
public val SfSymbols.Monochrome.SFHomepodMiniFill: ImageVector
    get() {
        if (_sFHomepodMiniFill != null) {
            return _sFHomepodMiniFill!!
        }
        _sFHomepodMiniFill = sfIcon(
            name = "Monochrome.SFHomepodMiniFill",
            viewportWidth = 25.0293f,
            viewportHeight = 22.6465f
        ) {
            addSfPath("M12.334 22.6465C17.0215 22.6465 18.8672 22.0312 20.625 20.3906C23.1641 18.0664 24.668 14.707 24.668 11.1035C24.668 8.0957 23.6328 5.25391 21.4355 3.00781C21.0059 2.51953 20.5469 2.5 20.1367 2.8418C18.584 4.27734 16.3184 5.01953 12.334 5.01953C8.34961 5.01953 6.08398 4.27734 4.54102 2.8418C4.13086 2.5 3.67188 2.51953 3.24219 3.00781C1.03516 5.25391 0 8.0957 0 11.1035C0 14.707 1.50391 18.0664 4.04297 20.3906C5.80078 22.0312 7.65625 22.6465 12.334 22.6465Z", fillAlpha = 0.85f)
            addSfPath("M12.334 3.45703C15.9277 3.45703 18.3496 2.76367 18.3496 1.80664C18.3496 0.839844 15.9277 0.146484 12.334 0.146484C8.74023 0.146484 6.32812 0.839844 6.32812 1.80664C6.32812 2.76367 8.74023 3.45703 12.334 3.45703Z", fillAlpha = 0.85f)
        }
        return _sFHomepodMiniFill!!
    }

private var _sFHomepodMiniFill: ImageVector? = null
