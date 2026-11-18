package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDieFace1Fill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFDieFace1Fill: ImageVector
    get() {
        if (_sFDieFace1Fill != null) {
            return _sFDieFace1Fill!!
        }
        _sFDieFace1Fill = sfIcon(
            name = "Monochrome.SFDieFace1Fill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM9.42383 11.4746C9.42383 12.627 10.3516 13.5352 11.4844 13.5449C12.6074 13.5547 13.5449 12.627 13.5449 11.4746C13.5449 10.3418 12.6074 9.42383 11.4844 9.42383C10.3516 9.42383 9.42383 10.3418 9.42383 11.4746Z", fillAlpha = 0.85f)
        }
        return _sFDieFace1Fill!!
    }

private var _sFDieFace1Fill: ImageVector? = null
