package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTriangleCircleFill (monochrome)
 * Viewport: 25.8008 x 25.5566
 */
public val SfSymbols.Monochrome.SFTriangleCircleFill: ImageVector
    get() {
        if (_sFTriangleCircleFill != null) {
            return _sFTriangleCircleFill!!
        }
        _sFTriangleCircleFill = sfIcon(
            name = "Monochrome.SFTriangleCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.5566f
        ) {
            addSfPath("M25.4395 12.832C25.4395 19.8438 19.7266 25.5566 12.7148 25.5566C5.71289 25.5566 0 19.8438 0 12.832C0 5.82031 5.71289 0.117188 12.7148 0.117188C19.7266 0.117188 25.4395 5.82031 25.4395 12.832ZM11.1621 6.97266L6.31836 15.4102C5.52734 16.7773 6.11328 18.125 7.70508 18.125L17.7539 18.125C19.3457 18.125 19.9316 16.7773 19.1406 15.4102L14.2676 6.94336C13.5352 5.6543 11.9629 5.58594 11.1621 6.97266ZM13.0273 7.91992L17.6758 16.0059C17.8516 16.2793 17.793 16.5527 17.4219 16.5527L8.03711 16.5527C7.66602 16.5527 7.60742 16.2793 7.7832 16.0059L12.4121 7.92969C12.5586 7.65625 12.8809 7.66602 13.0273 7.91992Z", fillAlpha = 0.85f)
        }
        return _sFTriangleCircleFill!!
    }

private var _sFTriangleCircleFill: ImageVector? = null
