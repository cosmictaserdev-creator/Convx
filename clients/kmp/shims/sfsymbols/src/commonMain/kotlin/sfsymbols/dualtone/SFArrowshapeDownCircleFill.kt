package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeDownCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFArrowshapeDownCircleFill: ImageVector
    get() {
        if (_sFArrowshapeDownCircleFill != null) {
            return _sFArrowshapeDownCircleFill!!
        }
        _sFArrowshapeDownCircleFill = sfIcon(
            name = "Dualtone.SFArrowshapeDownCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M15.4688 6.68945C15.4688 5.68359 14.9121 5.11719 13.9453 5.11719L11.5137 5.11719C10.5566 5.11719 10 5.68359 10 6.68945L10 15.2441C10 16.25 10.5566 16.8164 11.5137 16.8164L13.9453 16.8164C14.9121 16.8164 15.4688 16.25 15.4688 15.2441ZM7.02148 12.6953C6.57227 12.6953 6.25977 13.0078 6.25977 13.4473C6.25977 13.7695 6.40625 14.0137 6.66992 14.3066L11.9727 19.9512C12.2559 20.2441 12.5 20.3223 12.7246 20.3223C12.959 20.3223 13.2031 20.2441 13.4961 19.9512L18.7402 14.3066C19.0527 13.9844 19.1895 13.7793 19.1895 13.4668C19.1895 13.0078 18.8281 12.6953 18.3984 12.6953Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeDownCircleFill!!
    }

private var _sFArrowshapeDownCircleFill: ImageVector? = null
