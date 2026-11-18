package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTriangleCircleFill (dualtone)
 * Viewport: 25.8008 x 25.5566
 */
public val SfSymbols.Dualtone.SFTriangleCircleFill: ImageVector
    get() {
        if (_sFTriangleCircleFill != null) {
            return _sFTriangleCircleFill!!
        }
        _sFTriangleCircleFill = sfIcon(
            name = "Dualtone.SFTriangleCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.5566f
        ) {
            addSfPath("M12.7148 25.5566C19.7266 25.5566 25.4395 19.8438 25.4395 12.832C25.4395 5.82031 19.7266 0.117188 12.7148 0.117188C5.71289 0.117188 0 5.82031 0 12.832C0 19.8438 5.71289 25.5566 12.7148 25.5566Z", fillAlpha = 0.2125f)
            addSfPath("M6.31836 15.4102L11.1621 6.97266C11.9629 5.58594 13.5352 5.6543 14.2676 6.94336L19.1406 15.4102C19.9316 16.7773 19.3457 18.125 17.7539 18.125L7.70508 18.125C6.11328 18.125 5.52734 16.7773 6.31836 15.4102ZM7.7832 16.0059C7.60742 16.2793 7.66602 16.5527 8.03711 16.5527L17.4219 16.5527C17.793 16.5527 17.8516 16.2793 17.6758 16.0059L13.0273 7.91992C12.8809 7.66602 12.5586 7.65625 12.4121 7.92969Z", fillAlpha = 0.85f)
        }
        return _sFTriangleCircleFill!!
    }

private var _sFTriangleCircleFill: ImageVector? = null
