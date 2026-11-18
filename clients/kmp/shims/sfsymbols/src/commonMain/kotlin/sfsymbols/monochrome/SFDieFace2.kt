package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDieFace2 (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFDieFace2: ImageVector
    get() {
        if (_sFDieFace2 != null) {
            return _sFDieFace2!!
        }
        _sFDieFace2 = sfIcon(
            name = "Monochrome.SFDieFace2",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M17.2559 7.71484C18.3496 7.73438 19.2578 6.81641 19.2578 5.69336C19.2578 4.58984 18.3496 3.69141 17.2559 3.69141C16.1523 3.69141 15.2344 4.58984 15.2344 5.69336C15.2344 6.81641 16.1523 7.69531 17.2559 7.71484ZM5.71289 19.2773C6.80664 19.2773 7.71484 18.3691 7.71484 17.2656C7.71484 16.1523 6.80664 15.2539 5.71289 15.2539C4.60938 15.2539 3.71094 16.1523 3.71094 17.2656C3.71094 18.3691 4.60938 19.2773 5.71289 19.2773Z", fillAlpha = 0.85f)
        }
        return _sFDieFace2!!
    }

private var _sFDieFace2: ImageVector? = null
