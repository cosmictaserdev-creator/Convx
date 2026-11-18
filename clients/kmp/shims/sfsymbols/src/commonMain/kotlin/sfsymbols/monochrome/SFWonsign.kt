package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWonsign (monochrome)
 * Viewport: 28.3301 x 22.9395
 */
public val SfSymbols.Monochrome.SFWonsign: ImageVector
    get() {
        if (_sFWonsign != null) {
            return _sFWonsign!!
        }
        _sFWonsign = sfIcon(
            name = "Monochrome.SFWonsign",
            viewportWidth = 28.3301f,
            viewportHeight = 22.9395f
        ) {
            addSfPath("M7.40234 22.9199C8.0957 22.9199 8.63281 22.5098 8.81836 21.8164L13.8965 3.71094L14.043 3.71094L19.1309 21.8262C19.3164 22.5098 19.8438 22.9199 20.5273 22.9199C21.2891 22.9199 21.8164 22.4902 21.9629 21.7871L26.2891 1.55273C26.3379 1.34766 26.3574 1.17188 26.3574 0.966797C26.3574 0.361328 25.8789 0 25.293 0C24.7363 0 24.3262 0.322266 24.2285 0.888672L20.498 19.9414L20.3613 19.9414L15.0879 0.976562C14.9316 0.390625 14.5508 0.0878906 13.9941 0.0878906C13.3789 0.0878906 12.998 0.400391 12.8418 1.01562L7.56836 19.9414L7.44141 19.9414L3.69141 0.859375C3.59375 0.283203 3.16406 0 2.65625 0C2.05078 0 1.58203 0.371094 1.58203 0.947266C1.58203 1.11328 1.61133 1.37695 1.66016 1.57227L5.97656 21.7773C6.12305 22.4902 6.65039 22.9199 7.40234 22.9199ZM0 11.3086C0 11.7285 0.322266 12.0312 0.732422 12.0312L27.2363 12.0312C27.6465 12.0312 27.9688 11.7188 27.9688 11.3086C27.9688 10.8887 27.6465 10.5859 27.2363 10.5859L0.732422 10.5859C0.322266 10.5859 0 10.8887 0 11.3086Z", fillAlpha = 0.85f)
        }
        return _sFWonsign!!
    }

private var _sFWonsign: ImageVector? = null
