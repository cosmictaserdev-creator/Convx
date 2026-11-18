package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMoon (dualtone)
 * Viewport: 25.4297 x 25.3088
 */
public val SfSymbols.Dualtone.SFMoon: ImageVector
    get() {
        if (_sFMoon != null) {
            return _sFMoon!!
        }
        _sFMoon = sfIcon(
            name = "Dualtone.SFMoon",
            viewportWidth = 25.4297f,
            viewportHeight = 25.3088f
        ) {
            addSfPath("M13.0859 25.2277C18.5254 25.2277 22.9883 21.9464 24.9414 17.6691C25.3027 16.9171 24.834 16.38 24.0918 16.6241C23.1836 16.9464 21.6113 17.3077 20.0488 17.3077C12.4414 17.3077 8.11523 12.9816 8.11523 5.37414C8.11523 3.8507 8.4375 2.30773 8.93555 1.0675C9.25781 0.256952 8.70117-0.23133 7.91992 0.110467C3.69141 1.90734 0 6.38976 0 12.132C0 19.3585 5.86914 25.2277 13.0859 25.2277ZM13.0859 23.548C6.79688 23.548 1.66992 18.4308 1.66992 12.132C1.66992 7.85461 4.0918 4.49523 6.80664 2.67883C6.57227 3.54797 6.44531 4.39758 6.44531 5.37414C6.44531 13.8995 11.5137 18.9777 20.0488 18.9777C20.9277 18.9777 21.7285 18.8312 22.3535 18.6847C20.3906 21.5558 17.0215 23.548 13.0859 23.548Z", fillAlpha = 0.85f)
        }
        return _sFMoon!!
    }

private var _sFMoon: ImageVector? = null
