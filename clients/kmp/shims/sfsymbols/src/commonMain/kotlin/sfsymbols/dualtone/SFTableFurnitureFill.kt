package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTableFurnitureFill (dualtone)
 * Viewport: 29.7656 x 19.0137
 */
public val SfSymbols.Dualtone.SFTableFurnitureFill: ImageVector
    get() {
        if (_sFTableFurnitureFill != null) {
            return _sFTableFurnitureFill!!
        }
        _sFTableFurnitureFill = sfIcon(
            name = "Dualtone.SFTableFurnitureFill",
            viewportWidth = 29.7656f,
            viewportHeight = 19.0137f
        ) {
            addSfPath("M0 1.13281L0 17.9004C0 18.5742 0.429688 19.0137 1.11328 19.0137L4.73633 19.0137C5.41016 19.0137 5.83984 18.5742 5.83984 17.9004L5.83984 5.85938L23.5645 5.85938L23.5645 17.9004C23.5645 18.5742 23.9941 19.0137 24.668 19.0137L28.291 19.0137C28.9746 19.0137 29.4043 18.5742 29.4043 17.9004L29.4043 1.13281C29.4043 0.449219 28.9746 0.0195312 28.3008 0.0195312L1.10352 0.0195312C0.429688 0.0195312 0 0.449219 0 1.13281Z", fillAlpha = 0.85f)
        }
        return _sFTableFurnitureFill!!
    }

private var _sFTableFurnitureFill: ImageVector? = null
