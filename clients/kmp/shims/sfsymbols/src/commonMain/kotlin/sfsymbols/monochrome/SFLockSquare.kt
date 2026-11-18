package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockSquare (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFLockSquare: ImageVector
    get() {
        if (_sFLockSquare != null) {
            return _sFLockSquare!!
        }
        _sFLockSquare = sfIcon(
            name = "Monochrome.SFLockSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M6.91406 16.4746C6.91406 17.4414 7.34375 17.8809 8.23242 17.8809L14.7363 17.8809C15.625 17.8809 16.0547 17.4414 16.0547 16.4746L16.0547 11.3867C16.0547 10.4785 15.6641 10.0293 14.873 9.99023L14.873 8.41797C14.873 6.16211 13.5059 4.64844 11.4844 4.64844C9.46289 4.64844 8.0957 6.16211 8.0957 8.41797L8.0957 9.99023C7.30469 10.0293 6.91406 10.4785 6.91406 11.3867ZM9.26758 9.98047L9.26758 8.31055C9.26758 6.78711 10.1562 5.79102 11.4844 5.79102C12.8125 5.79102 13.7109 6.78711 13.7109 8.31055L13.7109 9.98047Z", fillAlpha = 0.85f)
        }
        return _sFLockSquare!!
    }

private var _sFLockSquare: ImageVector? = null
