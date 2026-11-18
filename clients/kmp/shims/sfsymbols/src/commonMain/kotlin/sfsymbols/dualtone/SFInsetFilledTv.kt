package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInsetFilledTv (dualtone)
 * Viewport: 31.3477 x 24.6191
 */
public val SfSymbols.Dualtone.SFInsetFilledTv: ImageVector
    get() {
        if (_sFInsetFilledTv != null) {
            return _sFInsetFilledTv!!
        }
        _sFInsetFilledTv = sfIcon(
            name = "Dualtone.SFInsetFilledTv",
            viewportWidth = 31.3477f,
            viewportHeight = 24.6191f
        ) {
            addSfPath("M3.05664 20.6055L27.9395 20.6055C29.8438 20.6055 30.9863 19.4629 30.9863 17.5586L30.9863 3.04688C30.9863 1.14258 29.8438 0 27.9395 0L3.05664 0C1.15234 0 0 1.14258 0 3.04688L0 17.5586C0 19.4629 1.15234 20.6055 3.05664 20.6055ZM3.08594 18.877C2.23633 18.877 1.72852 18.3789 1.72852 17.5293L1.72852 3.08594C1.72852 2.22656 2.23633 1.72852 3.08594 1.72852L27.9004 1.72852C28.75 1.72852 29.2578 2.22656 29.2578 3.08594L29.2578 17.5293C29.2578 18.3789 28.75 18.877 27.9004 18.877ZM9.24805 24.6094L21.748 24.6094C22.2266 24.6094 22.6172 24.2188 22.6172 23.7402C22.6172 23.252 22.2266 22.8613 21.748 22.8613L9.24805 22.8613C8.75977 22.8613 8.36914 23.252 8.36914 23.7402C8.36914 24.2188 8.75977 24.6094 9.24805 24.6094Z", fillAlpha = 0.425f)
            addSfPath("M4.0918 17.3242L26.9141 17.3242C27.4512 17.3242 27.7051 17.0605 27.7051 16.5137L27.7051 4.11133C27.7051 3.55469 27.4512 3.30078 26.9141 3.30078L4.0918 3.30078C3.54492 3.30078 3.29102 3.55469 3.29102 4.11133L3.29102 16.5137C3.29102 17.0605 3.54492 17.3242 4.0918 17.3242Z", fillAlpha = 0.85f)
        }
        return _sFInsetFilledTv!!
    }

private var _sFInsetFilledTv: ImageVector? = null
