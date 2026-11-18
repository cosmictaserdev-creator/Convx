package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlayTv (monochrome)
 * Viewport: 31.3477 x 24.6191
 */
public val SfSymbols.Monochrome.SFPlayTv: ImageVector
    get() {
        if (_sFPlayTv != null) {
            return _sFPlayTv!!
        }
        _sFPlayTv = sfIcon(
            name = "Monochrome.SFPlayTv",
            viewportWidth = 31.3477f,
            viewportHeight = 24.6191f
        ) {
            addSfPath("M3.05664 20.6055L27.9395 20.6055C29.8438 20.6055 30.9863 19.4629 30.9863 17.5586L30.9863 3.04688C30.9863 1.14258 29.8438 0 27.9395 0L3.05664 0C1.15234 0 0 1.14258 0 3.04688L0 17.5586C0 19.4629 1.15234 20.6055 3.05664 20.6055ZM3.08594 18.877C2.23633 18.877 1.72852 18.3789 1.72852 17.5293L1.72852 3.08594C1.72852 2.22656 2.23633 1.72852 3.08594 1.72852L27.9004 1.72852C28.75 1.72852 29.2578 2.22656 29.2578 3.08594L29.2578 17.5293C29.2578 18.3789 28.75 18.877 27.9004 18.877ZM9.24805 24.6094L21.748 24.6094C22.2266 24.6094 22.6172 24.2188 22.6172 23.7402C22.6172 23.252 22.2266 22.8613 21.748 22.8613L9.24805 22.8613C8.75977 22.8613 8.36914 23.252 8.36914 23.7402C8.36914 24.2188 8.75977 24.6094 9.24805 24.6094Z", fillAlpha = 0.85f)
            addSfPath("M11.377 15.5566C11.377 16.1914 12.0508 16.4258 12.5684 16.123L21.3574 10.9473C21.8652 10.6543 21.8555 9.9707 21.3477 9.67773L12.5684 4.50195C12.0508 4.19922 11.377 4.44336 11.377 5.07812Z", fillAlpha = 0.85f)
        }
        return _sFPlayTv!!
    }

private var _sFPlayTv: ImageVector? = null
