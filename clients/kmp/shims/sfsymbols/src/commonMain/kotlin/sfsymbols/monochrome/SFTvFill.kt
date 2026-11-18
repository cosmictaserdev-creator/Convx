package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTvFill (monochrome)
 * Viewport: 31.3477 x 24.6191
 */
public val SfSymbols.Monochrome.SFTvFill: ImageVector
    get() {
        if (_sFTvFill != null) {
            return _sFTvFill!!
        }
        _sFTvFill = sfIcon(
            name = "Monochrome.SFTvFill",
            viewportWidth = 31.3477f,
            viewportHeight = 24.6191f
        ) {
            addSfPath("M3.05664 20.5957L27.9395 20.5957C29.9512 20.5957 30.9863 19.5996 30.9863 17.5488L30.9863 3.04688C30.9863 0.996094 29.9512 0 27.9395 0L3.05664 0C1.04492 0 0 0.996094 0 3.04688L0 17.5488C0 19.5996 1.04492 20.5957 3.05664 20.5957ZM9.24805 24.5996L21.748 24.5996C22.2266 24.5996 22.6172 24.209 22.6172 23.7305C22.6172 23.2422 22.2266 22.8516 21.748 22.8516L9.24805 22.8516C8.75977 22.8516 8.36914 23.2422 8.36914 23.7305C8.36914 24.209 8.75977 24.5996 9.24805 24.5996Z", fillAlpha = 0.85f)
        }
        return _sFTvFill!!
    }

private var _sFTvFill: ImageVector? = null
