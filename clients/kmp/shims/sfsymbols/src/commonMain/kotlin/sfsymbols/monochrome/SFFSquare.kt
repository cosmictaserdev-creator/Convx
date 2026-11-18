package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFFSquare: ImageVector
    get() {
        if (_sFFSquare != null) {
            return _sFFSquare!!
        }
        _sFFSquare = sfIcon(
            name = "Monochrome.SFFSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M8.70117 17.2168C9.26758 17.2168 9.57031 16.8457 9.57031 16.2598L9.57031 12.1191L14.2676 12.1191C14.6973 12.1191 15 11.8652 15 11.4453C15 10.9961 14.6973 10.7422 14.2676 10.7422L9.57031 10.7422L9.57031 7.11914L14.7461 7.11914C15.1855 7.11914 15.4785 6.82617 15.4785 6.39648C15.4785 5.95703 15.1855 5.68359 14.7461 5.68359L8.69141 5.68359C8.14453 5.68359 7.85156 6.07422 7.85156 6.65039L7.85156 16.2598C7.85156 16.8262 8.1543 17.2168 8.70117 17.2168Z", fillAlpha = 0.85f)
        }
        return _sFFSquare!!
    }

private var _sFFSquare: ImageVector? = null
