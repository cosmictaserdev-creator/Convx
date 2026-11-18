package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAirplaneseat (monochrome)
 * Viewport: 22.8125 x 26.123
 */
public val SfSymbols.Monochrome.SFAirplaneseat: ImageVector
    get() {
        if (_sFAirplaneseat != null) {
            return _sFAirplaneseat!!
        }
        _sFAirplaneseat = sfIcon(
            name = "Monochrome.SFAirplaneseat",
            viewportWidth = 22.8125f,
            viewportHeight = 26.123f
        ) {
            addSfPath("M8.34961 21.416L19.541 21.416C21.1523 21.416 22.4512 20.1074 22.4512 18.4961C22.4512 16.8945 21.1523 15.5957 19.541 15.5957L10.8789 15.5957C9.84375 15.5957 8.85742 15.9277 8.24219 16.4453L3.99414 1.81641C3.69141 0.693359 2.79297 0.185547 1.98242 0.185547C0.966797 0.185547 0 0.957031 0 2.19727C0 2.49023 0.0683594 2.80273 0.175781 3.17383L4.61914 18.3496C5.17578 20.2734 6.37695 21.416 8.34961 21.416ZM4.74609 11.6309L15.7031 11.6309C16.2012 11.6309 16.6211 11.2207 16.6211 10.7129C16.6211 10.2148 16.2012 9.80469 15.7031 9.80469L4.74609 9.80469ZM7.72461 26.123L19.1016 26.123C19.6094 26.123 20.0195 25.7129 20.0195 25.2148C20.0195 24.707 19.6094 24.2969 19.1016 24.2969L7.72461 24.2969C7.2168 24.2969 6.80664 24.707 6.80664 25.2148C6.80664 25.7129 7.2168 26.123 7.72461 26.123Z", fillAlpha = 0.85f)
        }
        return _sFAirplaneseat!!
    }

private var _sFAirplaneseat: ImageVector? = null
