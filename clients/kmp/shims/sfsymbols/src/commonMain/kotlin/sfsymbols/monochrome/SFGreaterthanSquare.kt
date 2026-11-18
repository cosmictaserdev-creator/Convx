package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGreaterthanSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFGreaterthanSquare: ImageVector
    get() {
        if (_sFGreaterthanSquare != null) {
            return _sFGreaterthanSquare!!
        }
        _sFGreaterthanSquare = sfIcon(
            name = "Monochrome.SFGreaterthanSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M8.28125 16.4551C8.49609 16.4551 8.64258 16.416 8.80859 16.3281L16.2109 12.4316C16.6504 12.1973 16.8457 11.9238 16.8457 11.4746C16.8457 11.0645 16.6309 10.7617 16.2109 10.5176L8.80859 6.50391C8.63281 6.41602 8.49609 6.37695 8.26172 6.37695C7.75391 6.37695 7.38281 6.74805 7.38281 7.22656C7.38281 7.62695 7.57812 7.89062 8.01758 8.11523L14.4629 11.4062L14.4629 11.4844L8.01758 14.7266C7.56836 14.9414 7.38281 15.1855 7.38281 15.5957C7.38281 16.0938 7.76367 16.4551 8.28125 16.4551Z", fillAlpha = 0.85f)
        }
        return _sFGreaterthanSquare!!
    }

private var _sFGreaterthanSquare: ImageVector? = null
