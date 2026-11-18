package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDieFace2Fill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFDieFace2Fill: ImageVector
    get() {
        if (_sFDieFace2Fill != null) {
            return _sFDieFace2Fill!!
        }
        _sFDieFace2Fill = sfIcon(
            name = "Dualtone.SFDieFace2Fill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M17.3926 7.62695C16.2598 7.60742 15.3125 6.70898 15.3125 5.55664C15.3125 4.41406 16.2598 3.50586 17.3926 3.50586C18.5156 3.50586 19.4434 4.41406 19.4434 5.55664C19.4434 6.70898 18.5156 7.64648 17.3926 7.62695ZM5.57617 19.4629C4.44336 19.4629 3.52539 18.5352 3.52539 17.4023C3.52539 16.2598 4.44336 15.3516 5.57617 15.3516C6.69922 15.3516 7.62695 16.2598 7.62695 17.4023C7.62695 18.5352 6.69922 19.4629 5.57617 19.4629Z", fillAlpha = 0.85f)
        }
        return _sFDieFace2Fill!!
    }

private var _sFDieFace2Fill: ImageVector? = null
