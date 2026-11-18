package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMusicMicrophone (monochrome)
 * Viewport: 25.2431 x 26.4073
 */
public val SfSymbols.Monochrome.SFMusicMicrophone: ImageVector
    get() {
        if (_sFMusicMicrophone != null) {
            return _sFMusicMicrophone!!
        }
        _sFMusicMicrophone = sfIcon(
            name = "Monochrome.SFMusicMicrophone",
            viewportWidth = 25.2431f,
            viewportHeight = 26.4073f
        ) {
            addSfPath("M12.7241 25.4937C13.3198 25.4937 13.7788 25.0347 13.7788 24.4488L13.7788 15.064L17.5776 11.5484C19.0327 11.773 20.5854 11.1577 21.9819 9.76125L15.1167 2.88625C13.7104 4.28274 13.1538 5.77688 13.3881 7.24172L1.65963 19.8784C1.19088 20.396 1.16158 21.0503 1.76705 21.6558L0.0971318 23.7652C-0.0395869 23.9312-0.0395869 24.1753 0.14596 24.3706L0.517054 24.732C0.702601 24.9175 0.92721 24.9273 1.12252 24.7808L3.2319 23.0913C3.7983 23.7066 4.49166 23.6675 4.98971 23.1988L11.6596 17.0269L11.6596 24.4488C11.6596 25.0347 12.1284 25.4937 12.7241 25.4937ZM2.72408 20.8159L13.9741 8.85305C14.1987 9.26321 14.4819 9.65383 14.8237 10.0445C15.1655 10.4155 15.5561 10.728 15.937 10.9429L4.06198 22.1636ZM16.2592 1.74367L23.1245 8.61867C25.3803 6.37258 25.4682 3.62844 23.3393 1.51906C21.2397-0.570779 18.5053-0.512186 16.2592 1.74367Z", fillAlpha = 0.85f)
        }
        return _sFMusicMicrophone!!
    }

private var _sFMusicMicrophone: ImageVector? = null
