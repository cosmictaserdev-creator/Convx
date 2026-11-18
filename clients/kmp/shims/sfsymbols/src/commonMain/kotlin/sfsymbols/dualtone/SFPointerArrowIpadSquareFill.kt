package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPointerArrowIpadSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFPointerArrowIpadSquareFill: ImageVector
    get() {
        if (_sFPointerArrowIpadSquareFill != null) {
            return _sFPointerArrowIpadSquareFill!!
        }
        _sFPointerArrowIpadSquareFill = sfIcon(
            name = "Dualtone.SFPointerArrowIpadSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M12.0703 15.1953L8.70117 18.5645C7.94922 19.3066 6.97266 18.916 6.97266 18.0078L6.97266 5.39062C6.97266 4.36523 8.05664 3.99414 8.75977 4.69727L17.6465 13.584C18.291 14.2285 17.8906 15.1953 16.8164 15.1953Z", fillAlpha = 0.85f)
        }
        return _sFPointerArrowIpadSquareFill!!
    }

private var _sFPointerArrowIpadSquareFill: ImageVector? = null
