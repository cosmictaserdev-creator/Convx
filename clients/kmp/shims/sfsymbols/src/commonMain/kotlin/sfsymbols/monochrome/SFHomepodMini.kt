package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHomepodMini (monochrome)
 * Viewport: 25.0293 x 22.6465
 */
public val SfSymbols.Monochrome.SFHomepodMini: ImageVector
    get() {
        if (_sFHomepodMini != null) {
            return _sFHomepodMini!!
        }
        _sFHomepodMini = sfIcon(
            name = "Monochrome.SFHomepodMini",
            viewportWidth = 25.0293f,
            viewportHeight = 22.6465f
        ) {
            addSfPath("M12.334 22.6465C17.0215 22.6465 18.8672 22.0312 20.625 20.3906C23.1641 18.0664 24.668 14.707 24.668 11.1035C24.668 8.0957 23.6328 5.25391 21.582 3.21289C21.1328 2.67578 20.4004 2.60742 19.834 3.00781C18.5254 4.21875 16.3184 5.01953 12.334 5.01953C8.34961 5.01953 6.14258 4.21875 4.84375 3.00781C4.27734 2.60742 3.53516 2.67578 3.0957 3.21289C1.03516 5.25391 0 8.0957 0 11.1035C0 14.707 1.50391 18.0664 4.04297 20.3906C5.80078 22.0312 7.65625 22.6465 12.334 22.6465ZM12.334 20.9082C8.26172 20.9082 6.68945 20.498 5.22461 19.1309C2.98828 17.0801 1.72852 14.1797 1.72852 11.1035C1.72852 8.58398 2.57812 6.16211 4.49219 4.24805L3.71094 4.44336C5.41016 6.02539 8.54492 6.74805 12.334 6.74805C16.1328 6.74805 19.2578 6.02539 20.9668 4.44336L20.1855 4.24805C22.0996 6.16211 22.9395 8.58398 22.9395 11.1035C22.9395 14.1797 21.6797 17.0801 19.4531 19.1309C17.9785 20.498 16.4062 20.9082 12.334 20.9082Z", fillAlpha = 0.85f)
            addSfPath("M12.334 3.45703C15.9277 3.45703 18.3496 2.76367 18.3496 1.80664C18.3496 0.839844 15.9277 0.146484 12.334 0.146484C8.74023 0.146484 6.32812 0.839844 6.32812 1.80664C6.32812 2.76367 8.74023 3.45703 12.334 3.45703Z", fillAlpha = 0.85f)
        }
        return _sFHomepodMini!!
    }

private var _sFHomepodMini: ImageVector? = null
