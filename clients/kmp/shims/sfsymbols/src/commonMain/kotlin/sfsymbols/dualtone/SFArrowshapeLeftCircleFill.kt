package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeLeftCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowshapeLeftCircleFill: ImageVector
    get() {
        if (_sFArrowshapeLeftCircleFill != null) {
            return _sFArrowshapeLeftCircleFill!!
        }
        _sFArrowshapeLeftCircleFill = sfIcon(
            name = "Dualtone.SFArrowshapeLeftCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M18.4277 9.99023L10.1758 9.99023C9.16992 9.99023 8.60352 10.5566 8.60352 11.5234L8.60352 13.9453C8.60352 14.9121 9.16992 15.4688 10.1758 15.4688L18.4277 15.4688C19.4336 15.4688 20 14.9121 20 13.9453L20 11.5234C20 10.5566 19.4336 9.99023 18.4277 9.99023ZM12.4219 18.4277L12.4219 7.04102C12.4219 6.62109 12.1094 6.25 11.6504 6.25C11.3379 6.25 11.1328 6.39648 10.8105 6.69922L5.16602 11.9531C4.86328 12.2461 4.78516 12.4902 4.78516 12.7246C4.78516 12.9395 4.86328 13.1934 5.16602 13.4766L10.8105 18.7793C11.1035 19.043 11.3477 19.1895 11.6699 19.1895C12.1094 19.1895 12.4219 18.877 12.4219 18.4277Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeLeftCircleFill!!
    }

private var _sFArrowshapeLeftCircleFill: ImageVector? = null
