package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIphoneSizes (monochrome)
 * Viewport: 23.3008 x 28.916
 */
public val SfSymbols.Monochrome.SFIphoneSizes: ImageVector
    get() {
        if (_sFIphoneSizes != null) {
            return _sFIphoneSizes!!
        }
        _sFIphoneSizes = sfIcon(
            name = "Monochrome.SFIphoneSizes",
            viewportWidth = 23.3008f,
            viewportHeight = 28.916f
        ) {
            addSfPath("M16.6602 4.56055L16.6602 4.81445L14.9316 4.81445C14.9316 3.67188 14.3066 3.06641 13.1152 3.06641L4.54102 3.06641C3.35938 3.06641 2.73438 3.67188 2.73438 4.81445L2.73438 24.082C2.73438 25.2246 3.35938 25.8301 4.54102 25.8301L8.02938 25.8301C8.17594 26.5038 8.47184 27.0875 8.88956 27.5586L4.33594 27.5586C2.29492 27.5586 0.996094 26.3086 0.996094 24.3359L0.996094 4.56055C0.996094 2.58789 2.29492 1.33789 4.33594 1.33789L13.3301 1.33789C15.3711 1.33789 16.6602 2.58789 16.6602 4.56055Z", fillAlpha = 0.85f)
            addSfPath("M12.1582 27.5586L19.0527 27.5586C20.8301 27.5586 21.9336 26.4844 21.9336 24.7656L21.9336 8.96484C21.9336 7.24609 20.8301 6.17188 19.0527 6.17188L12.1582 6.17188C10.3809 6.17188 9.26758 7.24609 9.26758 8.96484L9.26758 24.7656C9.26758 26.4844 10.3809 27.5586 12.1582 27.5586ZM12.4512 25.9277C11.4453 25.9277 10.8984 25.3906 10.8984 24.4238L10.8984 9.29688C10.8984 8.33984 11.4453 7.80273 12.4512 7.80273L18.75 7.80273C19.7461 7.80273 20.3027 8.33984 20.3027 9.29688L20.3027 24.4238C20.3027 25.3906 19.7461 25.9277 18.75 25.9277Z", fillAlpha = 0.85f)
        }
        return _sFIphoneSizes!!
    }

private var _sFIphoneSizes: ImageVector? = null
