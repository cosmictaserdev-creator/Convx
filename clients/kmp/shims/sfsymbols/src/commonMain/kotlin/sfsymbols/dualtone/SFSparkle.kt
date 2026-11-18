package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSparkle (dualtone)
 * Viewport: 24.9023 x 24.541
 */
public val SfSymbols.Dualtone.SFSparkle: ImageVector
    get() {
        if (_sFSparkle != null) {
            return _sFSparkle!!
        }
        _sFSparkle = sfIcon(
            name = "Dualtone.SFSparkle",
            viewportWidth = 24.9023f,
            viewportHeight = 24.541f
        ) {
            addSfPath("M12.2754 24.5215C12.7246 24.5215 13.0664 24.209 13.1348 23.7402C14.1797 15.0879 15.2539 13.9941 23.7305 13.125C24.1992 13.0762 24.541 12.7148 24.541 12.2656C24.541 11.8066 24.1992 11.4453 23.7305 11.4062C15.2539 10.5273 14.1797 9.44336 13.1348 0.791016C13.0664 0.3125 12.7246 0 12.2754 0C11.8359 0 11.4844 0.3125 11.4062 0.791016C10.3711 9.44336 9.28711 10.5273 0.820312 11.4062C0.341797 11.4453 0 11.8066 0 12.2656C0 12.7148 0.341797 13.0664 0.820312 13.125C9.26758 14.209 10.293 15.0781 11.4062 23.7402C11.4844 24.209 11.8359 24.5215 12.2754 24.5215Z", fillAlpha = 0.85f)
        }
        return _sFSparkle!!
    }

private var _sFSparkle: ImageVector? = null
