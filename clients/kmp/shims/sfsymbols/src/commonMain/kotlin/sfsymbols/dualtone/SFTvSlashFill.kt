package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTvSlashFill (dualtone)
 * Viewport: 31.3477 x 31.9776
 */
public val SfSymbols.Dualtone.SFTvSlashFill: ImageVector
    get() {
        if (_sFTvSlashFill != null) {
            return _sFTvSlashFill!!
        }
        _sFTvSlashFill = sfIcon(
            name = "Dualtone.SFTvSlashFill",
            viewportWidth = 31.3477f,
            viewportHeight = 31.9776f
        ) {
            addSfPath("M22.6172 27.4097C22.6172 27.8882 22.2266 28.2788 21.748 28.2788L9.24805 28.2788C8.75977 28.2788 8.36914 27.8882 8.36914 27.4097C8.36914 26.9214 8.75977 26.5308 9.24805 26.5308L21.748 26.5308C22.2266 26.5308 22.6172 26.9214 22.6172 27.4097ZM1.79688 4.08937L21.9742 24.2749L3.05664 24.2749C1.04492 24.2749 0 23.2788 0 21.228L0 6.72609C0 5.23434 0.552893 4.30064 1.63128 3.90519C1.6826 3.96889 1.73786 4.03036 1.79688 4.08937ZM30.9863 6.72609L30.9863 21.228C30.9863 23.0102 30.2046 23.9959 28.6785 24.2225L8.15188 3.67922L27.9395 3.67922C29.9512 3.67922 30.9863 4.67531 30.9863 6.72609Z", fillAlpha = 0.425f)
            addSfPath("M26.9043 27.0093C27.2266 27.3316 27.7539 27.3316 28.0762 27.0093C28.3984 26.6773 28.4082 26.1499 28.0762 25.8277L4.08203 1.81398C3.76953 1.49172 3.24219 1.47219 2.90039 1.81398C2.57812 2.12648 2.57812 2.67336 2.90039 2.98586Z", fillAlpha = 0.85f)
        }
        return _sFTvSlashFill!!
    }

private var _sFTvSlashFill: ImageVector? = null
