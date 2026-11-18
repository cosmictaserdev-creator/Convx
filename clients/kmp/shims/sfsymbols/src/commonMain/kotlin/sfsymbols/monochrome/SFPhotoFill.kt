package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPhotoFill (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFPhotoFill: ImageVector
    get() {
        if (_sFPhotoFill != null) {
            return _sFPhotoFill!!
        }
        _sFPhotoFill = sfIcon(
            name = "Monochrome.SFPhotoFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M29.5898 3.76953L29.5898 19.1992C29.5898 21.6797 28.3105 22.959 25.7812 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L25.7812 0C28.3105 0 29.5898 1.2793 29.5898 3.76953ZM17.7637 11.7773L11.7383 17.0996L9.35547 14.9414C8.78906 14.4238 8.14453 14.1602 7.53906 14.1602C6.97266 14.1602 6.36719 14.4238 5.80078 14.9219L1.72852 18.4473L1.72852 19.1602C1.72852 20.4688 2.50977 21.2402 3.80859 21.2402L25.791 21.2402C27.0605 21.2402 27.8516 20.4688 27.8516 19.1797L27.8516 17.2754L21.8848 11.7969C21.2598 11.2305 20.5273 10.9375 19.8242 10.9375C19.1211 10.9375 18.4082 11.2207 17.7637 11.7773ZM6.13281 8.67188C6.13281 10.3711 7.51953 11.7578 9.21875 11.7578C10.9082 11.7578 12.2852 10.3711 12.2852 8.67188C12.2852 6.99219 10.9082 5.58594 9.21875 5.58594C7.51953 5.58594 6.13281 6.99219 6.13281 8.67188Z", fillAlpha = 0.85f)
        }
        return _sFPhotoFill!!
    }

private var _sFPhotoFill: ImageVector? = null
