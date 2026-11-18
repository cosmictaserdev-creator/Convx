package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAngle (monochrome)
 * Viewport: 27.8027 x 20.5957
 */
public val SfSymbols.Monochrome.SFAngle: ImageVector
    get() {
        if (_sFAngle != null) {
            return _sFAngle!!
        }
        _sFAngle = sfIcon(
            name = "Monochrome.SFAngle",
            viewportWidth = 27.8027f,
            viewportHeight = 20.5957f
        ) {
            addSfPath("M0 19.7266C0 20.1953 0.400391 20.5957 0.869141 20.5957L26.5723 20.5957C27.041 20.5957 27.4414 20.1953 27.4414 19.7266C27.4414 19.2578 27.0508 18.8672 26.5723 18.8672L2.94922 18.8672L20.2637 1.54297C20.6055 1.20117 20.6055 0.654297 20.2637 0.322266C19.9414-0.00976562 19.3848-0.0195312 19.043 0.322266L0.244141 19.1211C0.0976562 19.2676 0 19.4922 0 19.7266ZM14.1211 19.6777L15.8496 19.6777C15.8496 15.5664 14.1797 11.8359 11.4844 9.14062L10.2441 10.3516C12.6465 12.7344 14.1211 16.0352 14.1211 19.6777Z", fillAlpha = 0.85f)
        }
        return _sFAngle!!
    }

private var _sFAngle: ImageVector? = null
