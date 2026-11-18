package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTvSlash (monochrome)
 * Viewport: 31.3477 x 31.9776
 */
public val SfSymbols.Monochrome.SFTvSlash: ImageVector
    get() {
        if (_sFTvSlash != null) {
            return _sFTvSlash!!
        }
        _sFTvSlash = sfIcon(
            name = "Monochrome.SFTvSlash",
            viewportWidth = 31.3477f,
            viewportHeight = 31.9776f
        ) {
            addSfPath("M22.6172 27.4195C22.6172 27.898 22.2266 28.2886 21.748 28.2886L9.24805 28.2886C8.75977 28.2886 8.36914 27.898 8.36914 27.4195C8.36914 26.9312 8.75977 26.5405 9.24805 26.5405L21.748 26.5405C22.2266 26.5405 22.6172 26.9312 22.6172 27.4195ZM1.79688 4.08937L3.1147 5.40773L3.08594 5.40773C2.23633 5.40773 1.72852 5.90578 1.72852 6.76516L1.72852 21.2085C1.72852 22.0581 2.23633 22.5562 3.08594 22.5562L20.2562 22.5562L21.984 24.2847L3.05664 24.2847C1.15234 24.2847 0 23.1421 0 21.2378L0 6.72609C0 5.34889 0.602708 4.37009 1.65915 3.93673ZM30.9863 6.72609L30.9863 21.2378C30.9863 22.8817 30.1349 23.9579 28.6756 24.2196L27.0135 22.5562L27.9004 22.5562C28.75 22.5562 29.2578 22.0581 29.2578 21.2085L29.2578 6.76516C29.2578 5.90578 28.75 5.40773 27.9004 5.40773L9.87899 5.40773L8.15188 3.67922L27.9395 3.67922C29.8438 3.67922 30.9863 4.8218 30.9863 6.72609Z", fillAlpha = 0.85f)
            addSfPath("M26.9043 27.0093C27.2266 27.3316 27.7539 27.3316 28.0762 27.0093C28.3984 26.6773 28.4082 26.1499 28.0762 25.8277L4.08203 1.81398C3.76953 1.49172 3.24219 1.47219 2.90039 1.81398C2.57812 2.12648 2.57812 2.67336 2.90039 2.98586Z", fillAlpha = 0.85f)
        }
        return _sFTvSlash!!
    }

private var _sFTvSlash: ImageVector? = null
