package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTextRectangle (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFTextRectangle: ImageVector
    get() {
        if (_sFTextRectangle != null) {
            return _sFTextRectangle!!
        }
        _sFTextRectangle = sfIcon(
            name = "Dualtone.SFTextRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M5.6543 6.9043L23.9355 6.9043C24.3262 6.9043 24.6191 6.60156 24.6191 6.2207C24.6191 5.83984 24.3262 5.54688 23.9355 5.54688L5.6543 5.54688C5.25391 5.54688 4.96094 5.83984 4.96094 6.2207C4.96094 6.60156 5.25391 6.9043 5.6543 6.9043ZM5.6543 11.9824L14.8926 11.9824C15.2832 11.9824 15.5762 11.6797 15.5762 11.2988C15.5762 10.9277 15.2832 10.6348 14.8926 10.6348L5.6543 10.6348C5.25391 10.6348 4.96094 10.9277 4.96094 11.2988C4.96094 11.6797 5.25391 11.9824 5.6543 11.9824Z", fillAlpha = 0.85f)
        }
        return _sFTextRectangle!!
    }

private var _sFTextRectangle: ImageVector? = null
