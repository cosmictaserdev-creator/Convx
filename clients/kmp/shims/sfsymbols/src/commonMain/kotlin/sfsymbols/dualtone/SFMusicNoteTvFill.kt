package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMusicNoteTvFill (dualtone)
 * Viewport: 31.3477 x 24.6191
 */
public val SfSymbols.Dualtone.SFMusicNoteTvFill: ImageVector
    get() {
        if (_sFMusicNoteTvFill != null) {
            return _sFMusicNoteTvFill!!
        }
        _sFMusicNoteTvFill = sfIcon(
            name = "Dualtone.SFMusicNoteTvFill",
            viewportWidth = 31.3477f,
            viewportHeight = 24.6191f
        ) {
            addSfPath("M3.05664 20.5957L27.9395 20.5957C29.9512 20.5957 30.9863 19.5996 30.9863 17.5488L30.9863 3.04688C30.9863 0.996094 29.9512 0 27.9395 0L3.05664 0C1.04492 0 0 0.996094 0 3.04688L0 17.5488C0 19.5996 1.04492 20.5957 3.05664 20.5957ZM9.24805 24.5996L21.748 24.5996C22.2266 24.5996 22.6172 24.209 22.6172 23.7305C22.6172 23.2422 22.2266 22.8516 21.748 22.8516L9.24805 22.8516C8.75977 22.8516 8.36914 23.2422 8.36914 23.7305C8.36914 24.209 8.75977 24.5996 9.24805 24.5996Z", fillAlpha = 0.2125f)
            addSfPath("M19.2969 7.85156L16.2988 8.57422C16.0059 8.64258 15.8984 8.75977 15.8984 9.22852L15.8984 13.7891C15.8984 15.625 14.6973 16.9238 12.8809 16.9238C11.5918 16.9238 10.7812 16.1523 10.7812 15.0586C10.7812 13.8184 11.7578 13.1348 12.9102 12.8223L14.1016 12.5098C14.7461 12.3438 14.7949 12.2266 14.7949 11.6602L14.7949 5.77148C14.7949 5.01953 14.9316 4.89258 15.5566 4.73633L19.248 3.82812C19.9316 3.65234 20.0977 3.86719 20.0977 4.35547L20.0977 6.82617C20.0977 7.56836 19.9219 7.70508 19.2969 7.85156Z", fillAlpha = 0.85f)
        }
        return _sFMusicNoteTvFill!!
    }

private var _sFMusicNoteTvFill: ImageVector? = null
