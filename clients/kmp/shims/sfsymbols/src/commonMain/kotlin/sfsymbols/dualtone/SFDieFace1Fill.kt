package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDieFace1Fill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFDieFace1Fill: ImageVector
    get() {
        if (_sFDieFace1Fill != null) {
            return _sFDieFace1Fill!!
        }
        _sFDieFace1Fill = sfIcon(
            name = "Dualtone.SFDieFace1Fill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.4844 13.5449C10.3516 13.5352 9.42383 12.627 9.42383 11.4746C9.42383 10.3418 10.3516 9.42383 11.4844 9.42383C12.6074 9.42383 13.5449 10.3418 13.5449 11.4746C13.5449 12.627 12.6074 13.5547 11.4844 13.5449Z", fillAlpha = 0.85f)
        }
        return _sFDieFace1Fill!!
    }

private var _sFDieFace1Fill: ImageVector? = null
