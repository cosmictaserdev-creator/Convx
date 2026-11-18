package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownRight (dualtone)
 * Viewport: 18.8843 x 18.5254
 */
public val SfSymbols.Dualtone.SFArrowDownRight: ImageVector
    get() {
        if (_sFArrowDownRight != null) {
            return _sFArrowDownRight!!
        }
        _sFArrowDownRight = sfIcon(
            name = "Dualtone.SFArrowDownRight",
            viewportWidth = 18.8843f,
            viewportHeight = 18.5254f
        ) {
            addSfPath("M0.296642 0.371094C-0.0939828 0.751953-0.103748 1.29883 0.296642 1.69922L13.5388 14.9609L16.156 17.4121C16.5173 17.7734 17.0154 17.7637 17.3572 17.4219C17.699 17.0703 17.7185 16.5918 17.3474 16.2207L14.8865 13.6133L1.6443 0.371094C1.23414-0.0488281 0.687267-0.0195312 0.296642 0.371094ZM16.5662 10.1953L16.7908 16.8262L9.66188 16.6211L4.58375 16.6211C4.07594 16.6211 3.62672 17.0508 3.62672 17.5586C3.62672 18.0762 4.02711 18.5254 4.63258 18.5254L17.4451 18.5254C18.0798 18.5254 18.4802 18.1055 18.4802 17.5L18.4998 4.67773C18.4998 4.10156 18.0408 3.67188 17.5232 3.67188C16.9959 3.67188 16.5662 4.13086 16.5662 4.63867Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownRight!!
    }

private var _sFArrowDownRight: ImageVector? = null
