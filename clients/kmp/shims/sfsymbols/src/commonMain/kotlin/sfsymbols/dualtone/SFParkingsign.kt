package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFParkingsign (dualtone)
 * Viewport: 16.0156 x 23.9355
 */
public val SfSymbols.Dualtone.SFParkingsign: ImageVector
    get() {
        if (_sFParkingsign != null) {
            return _sFParkingsign!!
        }
        _sFParkingsign = sfIcon(
            name = "Dualtone.SFParkingsign",
            viewportWidth = 16.0156f,
            viewportHeight = 23.9355f
        ) {
            addSfPath("M1.28906 23.9355C2.07031 23.9355 2.57812 23.3984 2.57812 22.5977L2.57812 14.9219L7.99805 14.9219C12.5586 14.9219 15.6543 11.9141 15.6543 7.51953C15.6543 3.11523 12.5684 0.126953 8.00781 0.126953L1.28906 0.126953C0.517578 0.126953 0 0.654297 0 1.45508L0 22.5977C0 23.4082 0.517578 23.9355 1.28906 23.9355ZM2.57812 12.5391L2.57812 2.5L7.54883 2.5C10.9961 2.5 13.0273 4.3457 13.0273 7.51953C13.0273 10.6836 10.9863 12.5391 7.54883 12.5391Z", fillAlpha = 0.85f)
        }
        return _sFParkingsign!!
    }

private var _sFParkingsign: ImageVector? = null
