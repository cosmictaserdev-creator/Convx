package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlayTvFill (monochrome)
 * Viewport: 31.3477 x 24.6191
 */
public val SfSymbols.Monochrome.SFPlayTvFill: ImageVector
    get() {
        if (_sFPlayTvFill != null) {
            return _sFPlayTvFill!!
        }
        _sFPlayTvFill = sfIcon(
            name = "Monochrome.SFPlayTvFill",
            viewportWidth = 31.3477f,
            viewportHeight = 24.6191f
        ) {
            addSfPath("M22.6172 23.7305C22.6172 24.209 22.2266 24.5996 21.748 24.5996L9.24805 24.5996C8.75977 24.5996 8.36914 24.209 8.36914 23.7305C8.36914 23.2422 8.75977 22.8516 9.24805 22.8516L21.748 22.8516C22.2266 22.8516 22.6172 23.2422 22.6172 23.7305ZM30.9863 3.04688L30.9863 17.5488C30.9863 19.5996 29.9512 20.5957 27.9395 20.5957L3.05664 20.5957C1.04492 20.5957 0 19.5996 0 17.5488L0 3.04688C0 0.996094 1.04492 0 3.05664 0L27.9395 0C29.9512 0 30.9863 0.996094 30.9863 3.04688ZM11.2598 4.96094L11.2598 15.6445C11.2598 16.2793 11.9336 16.5137 12.4512 16.2207L21.2402 11.0449C21.8262 10.7031 21.8066 9.91211 21.2305 9.57031L12.4512 4.39453C11.9336 4.0918 11.2598 4.33594 11.2598 4.96094Z", fillAlpha = 0.85f)
        }
        return _sFPlayTvFill!!
    }

private var _sFPlayTvFill: ImageVector? = null
