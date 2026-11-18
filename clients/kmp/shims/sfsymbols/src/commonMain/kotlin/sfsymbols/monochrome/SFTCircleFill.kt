package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFTCircleFill: ImageVector
    get() {
        if (_sFTCircleFill != null) {
            return _sFTCircleFill!!
        }
        _sFTCircleFill = sfIcon(
            name = "Monochrome.SFTCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM8.53516 6.82617C8.0957 6.82617 7.77344 7.09961 7.77344 7.56836C7.77344 8.01758 8.0957 8.31055 8.53516 8.31055L11.7871 8.31055L11.7871 17.6562C11.7871 18.2422 12.0996 18.6523 12.6758 18.6523C13.252 18.6523 13.5742 18.2617 13.5742 17.6562L13.5742 8.31055L16.8848 8.31055C17.3242 8.31055 17.6465 8.01758 17.6465 7.56836C17.6465 7.09961 17.3242 6.82617 16.8848 6.82617Z", fillAlpha = 0.85f)
        }
        return _sFTCircleFill!!
    }

private var _sFTCircleFill: ImageVector? = null
