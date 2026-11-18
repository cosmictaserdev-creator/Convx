package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFUSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFUSquare: ImageVector
    get() {
        if (_sFUSquare != null) {
            return _sFUSquare!!
        }
        _sFUSquare = sfIcon(
            name = "Monochrome.SFUSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M11.4844 17.3438C14.3164 17.3438 16.2598 15.6836 16.2598 13.0957L16.2598 6.51367C16.2598 5.9082 15.9375 5.55664 15.3809 5.55664C14.8535 5.55664 14.5312 5.9082 14.5312 6.51367L14.5312 12.9395C14.5312 14.7363 13.3008 15.8984 11.4844 15.8984C9.66797 15.8984 8.4375 14.7363 8.4375 12.9395L8.4375 6.51367C8.4375 5.9082 8.125 5.55664 7.56836 5.55664C7.04102 5.55664 6.71875 5.9082 6.71875 6.51367L6.71875 13.0957C6.71875 15.6836 8.65234 17.3438 11.4844 17.3438Z", fillAlpha = 0.85f)
        }
        return _sFUSquare!!
    }

private var _sFUSquare: ImageVector? = null
