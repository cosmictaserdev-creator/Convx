package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleSplit3x3Fill (monochrome)
 * Viewport: 29.9512 x 22.9785
 */
public val SfSymbols.Monochrome.SFRectangleSplit3x3Fill: ImageVector
    get() {
        if (_sFRectangleSplit3x3Fill != null) {
            return _sFRectangleSplit3x3Fill!!
        }
        _sFRectangleSplit3x3Fill = sfIcon(
            name = "Monochrome.SFRectangleSplit3x3Fill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M0 16.4844L0 14.7559L8.7207 14.7559L8.7207 8.25195L0 8.25195L0 6.52344L8.7207 6.52344L8.7207 0.0292969L10.4492 0.0292969L10.4492 6.52344L19.1406 6.52344L19.1406 0.0292969L20.8691 0.0292969L20.8691 6.52344L29.5898 6.52344L29.5898 8.25195L20.8691 8.25195L20.8691 14.7559L29.5898 14.7559L29.5898 16.4844L20.8691 16.4844L20.8691 22.9785L19.1406 22.9785L19.1406 16.4844L10.4492 16.4844L10.4492 22.9785L8.7207 22.9785L8.7207 16.4844ZM10.4492 14.7559L19.1406 14.7559L19.1406 8.25195L10.4492 8.25195ZM3.79883 22.9688L25.7812 22.9688C28.3105 22.9688 29.5898 21.6895 29.5898 19.209L29.5898 3.7793C29.5898 1.28906 28.3105 0.00976562 25.7812 0.00976562L3.79883 0.00976562C1.2793 0.00976562 0 1.2793 0 3.7793L0 19.209C0 21.709 1.2793 22.9688 3.79883 22.9688Z", fillAlpha = 0.85f)
        }
        return _sFRectangleSplit3x3Fill!!
    }

private var _sFRectangleSplit3x3Fill: ImageVector? = null
