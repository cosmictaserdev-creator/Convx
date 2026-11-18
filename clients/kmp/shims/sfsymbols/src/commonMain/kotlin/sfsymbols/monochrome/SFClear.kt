package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFClear (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFClear: ImageVector
    get() {
        if (_sFClear != null) {
            return _sFClear!!
        }
        _sFClear = sfIcon(
            name = "Monochrome.SFClear",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M7.76367 16.4453L16.4551 7.74414C16.6309 7.57812 16.7285 7.36328 16.7285 7.12891C16.7285 6.64062 16.3379 6.26953 15.8496 6.26953C15.6152 6.26953 15.4102 6.35742 15.2441 6.5332L6.52344 15.2246C6.34766 15.4004 6.25 15.5957 6.25 15.8496C6.25 16.3281 6.64062 16.7188 7.12891 16.7188C7.38281 16.7188 7.58789 16.6211 7.76367 16.4453ZM15.2246 16.4453C15.3906 16.6211 15.5957 16.7188 15.8496 16.7188C16.3379 16.7188 16.7285 16.3281 16.7285 15.8496C16.7285 15.5957 16.6309 15.4004 16.4551 15.2246L7.74414 6.5332C7.56836 6.35742 7.37305 6.26953 7.12891 6.26953C6.64062 6.26953 6.25 6.64062 6.25 7.12891C6.25 7.36328 6.34766 7.57812 6.52344 7.74414Z", fillAlpha = 0.85f)
        }
        return _sFClear!!
    }

private var _sFClear: ImageVector? = null
