package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLockRectangle (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFLockRectangle: ImageVector
    get() {
        if (_sFLockRectangle != null) {
            return _sFLockRectangle!!
        }
        _sFLockRectangle = sfIcon(
            name = "Monochrome.SFLockRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M10.2344 16.4746C10.2344 17.4414 10.6641 17.8809 11.5527 17.8809L18.0469 17.8809C18.9355 17.8809 19.3652 17.4414 19.3652 16.4746L19.3652 11.3867C19.3652 10.4785 18.9746 10.0293 18.1836 9.99023L18.1836 8.41797C18.1836 6.16211 16.8262 4.64844 14.8047 4.64844C12.7734 4.64844 11.416 6.16211 11.416 8.41797L11.416 9.99023C10.625 10.0293 10.2344 10.4785 10.2344 11.3867ZM12.5781 9.98047L12.5781 8.31055C12.5781 6.78711 13.4668 5.79102 14.8047 5.79102C16.1328 5.79102 17.0215 6.78711 17.0215 8.31055L17.0215 9.98047Z", fillAlpha = 0.85f)
        }
        return _sFLockRectangle!!
    }

private var _sFLockRectangle: ImageVector? = null
