package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLessthanSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFLessthanSquare: ImageVector
    get() {
        if (_sFLessthanSquare != null) {
            return _sFLessthanSquare!!
        }
        _sFLessthanSquare = sfIcon(
            name = "Monochrome.SFLessthanSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M14.6777 16.4551C15.1953 16.4551 15.5762 16.0938 15.5762 15.5957C15.5762 15.1855 15.4004 14.9414 14.9414 14.7266L8.49609 11.4844L8.49609 11.4062L14.9414 8.11523C15.3906 7.89062 15.5762 7.62695 15.5762 7.22656C15.5762 6.74805 15.2051 6.37695 14.6973 6.37695C14.4629 6.37695 14.3262 6.41602 14.1504 6.50391L6.75781 10.5176C6.32812 10.7617 6.12305 11.0645 6.12305 11.4746C6.12305 11.9238 6.30859 12.1973 6.75781 12.4316L14.1504 16.3281C14.3164 16.416 14.4629 16.4551 14.6777 16.4551Z", fillAlpha = 0.85f)
        }
        return _sFLessthanSquare!!
    }

private var _sFLessthanSquare: ImageVector? = null
