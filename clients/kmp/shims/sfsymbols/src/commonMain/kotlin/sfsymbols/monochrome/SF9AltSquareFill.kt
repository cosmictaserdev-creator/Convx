package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF9AltSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SF9AltSquareFill: ImageVector
    get() {
        if (_sF9AltSquareFill != null) {
            return _sF9AltSquareFill!!
        }
        _sF9AltSquareFill = sfIcon(
            name = "Monochrome.SF9AltSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM7.14844 9.41406C7.14844 11.5625 8.7793 13.125 11.0156 13.125C11.8945 13.125 12.8125 12.8223 13.2812 12.3438L13.4375 12.3535C13.3105 12.5684 13.1152 12.8418 12.8223 13.2031C12.1582 14.0527 11.3184 15.0781 10.3906 16.2207C10.2148 16.4453 10.1172 16.6504 10.1172 16.8652C10.1172 17.207 10.4004 17.5293 10.8594 17.5293C11.2988 17.5293 11.4844 17.2559 11.8066 16.8457C12.4902 15.9961 13.5547 14.6289 14.1895 13.7793C15.3906 12.168 15.957 10.8789 15.957 9.50195C15.957 7.06055 14.1602 5.39062 11.543 5.39062C8.94531 5.39062 7.14844 7.05078 7.14844 9.41406ZM14.375 9.36523C14.375 10.8105 13.1738 11.8457 11.5625 11.8457C9.93164 11.8457 8.73047 10.8008 8.73047 9.3457C8.73047 7.90039 9.93164 6.82617 11.543 6.82617C13.1445 6.82617 14.375 7.91016 14.375 9.36523Z", fillAlpha = 0.85f)
        }
        return _sF9AltSquareFill!!
    }

private var _sF9AltSquareFill: ImageVector? = null
