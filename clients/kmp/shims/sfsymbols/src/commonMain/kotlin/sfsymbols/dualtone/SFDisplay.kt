package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDisplay (dualtone)
 * Viewport: 29.9902 x 25.791
 */
public val SfSymbols.Dualtone.SFDisplay: ImageVector
    get() {
        if (_sFDisplay != null) {
            return _sFDisplay!!
        }
        _sFDisplay = sfIcon(
            name = "Dualtone.SFDisplay",
            viewportWidth = 29.9902f,
            viewportHeight = 25.791f
        ) {
            addSfPath("M2.61719 19.6973C2.07031 19.6973 1.72852 19.3555 1.72852 18.8086L1.72852 3.44727C1.72852 2.90039 2.07031 2.55859 2.61719 2.55859L27.0117 2.55859C27.5586 2.55859 27.9004 2.90039 27.9004 3.44727L27.9004 18.8086C27.9004 19.3555 27.5586 19.6973 27.0117 19.6973Z", fillAlpha = 0.2125f)
            addSfPath("M2.57812 21.4258L27.0508 21.4258C28.6523 21.4258 29.6289 20.4492 29.6289 18.8477L29.6289 3.4082C29.6289 1.80664 28.6523 0.830078 27.0508 0.830078L2.57812 0.830078C0.976562 0.830078 0 1.80664 0 3.4082L0 18.8477C0 20.4492 0.976562 21.4258 2.57812 21.4258ZM2.61719 19.6973C2.07031 19.6973 1.72852 19.3555 1.72852 18.8086L1.72852 3.44727C1.72852 2.90039 2.07031 2.55859 2.61719 2.55859L27.0117 2.55859C27.5586 2.55859 27.9004 2.90039 27.9004 3.44727L27.9004 18.8086C27.9004 19.3555 27.5586 19.6973 27.0117 19.6973ZM10.9082 24.541L18.7207 24.541L18.7207 21.2695L10.9082 21.2695ZM10.7812 25.791L18.8477 25.791C19.3262 25.791 19.7168 25.4102 19.7168 24.9316C19.7168 24.4434 19.3262 24.0527 18.8477 24.0527L10.7812 24.0527C10.3027 24.0527 9.91211 24.4434 9.91211 24.9316C9.91211 25.4102 10.3027 25.791 10.7812 25.791Z", fillAlpha = 0.85f)
        }
        return _sFDisplay!!
    }

private var _sFDisplay: ImageVector? = null
