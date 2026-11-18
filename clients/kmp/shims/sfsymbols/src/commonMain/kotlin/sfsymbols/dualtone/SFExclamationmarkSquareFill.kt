package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFExclamationmarkSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFExclamationmarkSquareFill: ImageVector
    get() {
        if (_sFExclamationmarkSquareFill != null) {
            return _sFExclamationmarkSquareFill!!
        }
        _sFExclamationmarkSquareFill = sfIcon(
            name = "Dualtone.SFExclamationmarkSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.4844 13.7988C10.9277 13.7988 10.6055 13.4668 10.5957 12.8809L10.4492 6.00586C10.4395 5.41992 10.8594 5 11.4746 5C12.0801 5 12.5293 5.42969 12.5195 6.01562L12.3535 12.8809C12.3438 13.4766 12.0312 13.7988 11.4844 13.7988ZM11.4844 17.8711C10.8008 17.8711 10.2051 17.3145 10.2051 16.6406C10.2051 15.957 10.791 15.4004 11.4844 15.4004C12.1777 15.4004 12.7637 15.9473 12.7637 16.6406C12.7637 17.3242 12.168 17.8711 11.4844 17.8711Z", fillAlpha = 0.85f)
        }
        return _sFExclamationmarkSquareFill!!
    }

private var _sFExclamationmarkSquareFill: ImageVector? = null
