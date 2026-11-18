package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFPlusSquareFill: ImageVector
    get() {
        if (_sFPlusSquareFill != null) {
            return _sFPlusSquareFill!!
        }
        _sFPlusSquareFill = sfIcon(
            name = "Dualtone.SFPlusSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M12.4121 16.4258L12.4121 6.49414C12.4121 5.92773 12.0312 5.53711 11.4648 5.53711C10.9375 5.53711 10.5566 5.9375 10.5566 6.49414L10.5566 16.4258C10.5566 16.9727 10.9375 17.373 11.4648 17.373C12.0312 17.373 12.4121 16.9824 12.4121 16.4258ZM6.52344 12.3828L16.4648 12.3828C17.002 12.3828 17.4023 12.0117 17.4023 11.4746C17.4023 10.9082 17.0117 10.5273 16.4648 10.5273L6.52344 10.5273C5.95703 10.5273 5.56641 10.9082 5.56641 11.4746C5.56641 12.0117 5.9668 12.3828 6.52344 12.3828Z", fillAlpha = 0.85f)
        }
        return _sFPlusSquareFill!!
    }

private var _sFPlusSquareFill: ImageVector? = null
