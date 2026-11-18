package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRectangleGrid1x3Fill (monochrome)
 * Viewport: 27.8418 x 24.0527
 */
public val SfSymbols.Monochrome.SFRectangleGrid1x3Fill: ImageVector
    get() {
        if (_sFRectangleGrid1x3Fill != null) {
            return _sFRectangleGrid1x3Fill!!
        }
        _sFRectangleGrid1x3Fill = sfIcon(
            name = "Monochrome.SFRectangleGrid1x3Fill",
            viewportWidth = 27.8418f,
            viewportHeight = 24.0527f
        ) {
            addSfPath("M2.07031 24.0332L25.4102 24.0332C26.7188 24.0332 27.4805 23.291 27.4805 21.9238L27.4805 19.0918C27.4805 17.7246 26.7188 16.9824 25.4102 16.9824L2.07031 16.9824C0.761719 16.9824 0 17.7246 0 19.0918L0 21.9238C0 23.291 0.761719 24.0332 2.07031 24.0332Z", fillAlpha = 0.85f)
            addSfPath("M2.07031 15.5371L25.4102 15.5371C26.7188 15.5371 27.4805 14.8047 27.4805 13.4375L27.4805 10.5957C27.4805 9.22852 26.7188 8.49609 25.4102 8.49609L2.07031 8.49609C0.761719 8.49609 0 9.22852 0 10.5957L0 13.4375C0 14.8047 0.761719 15.5371 2.07031 15.5371Z", fillAlpha = 0.85f)
            addSfPath("M2.07031 7.05078L25.4102 7.05078C26.7188 7.05078 27.4805 6.30859 27.4805 4.94141L27.4805 2.10938C27.4805 0.742188 26.7188 0 25.4102 0L2.07031 0C0.761719 0 0 0.742188 0 2.10938L0 4.94141C0 6.30859 0.761719 7.05078 2.07031 7.05078Z", fillAlpha = 0.85f)
        }
        return _sFRectangleGrid1x3Fill!!
    }

private var _sFRectangleGrid1x3Fill: ImageVector? = null
