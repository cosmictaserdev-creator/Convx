package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWaveformPathEcgRectangle (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFWaveformPathEcgRectangle: ImageVector
    get() {
        if (_sFWaveformPathEcgRectangle != null) {
            return _sFWaveformPathEcgRectangle!!
        }
        _sFWaveformPathEcgRectangle = sfIcon(
            name = "Monochrome.SFWaveformPathEcgRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L25.752 1.72852C27.0898 1.72852 27.8516 2.46094 27.8516 3.85742L27.8516 19.1016C27.8516 20.5078 27.0898 21.2305 25.752 21.2305Z", fillAlpha = 0.85f)
            addSfPath("M1.73828 12.4805L4.67773 12.4805C5.13672 12.4805 5.3418 12.2754 5.51758 11.8555L7.29492 7.55859L10.4492 17.4609C10.6445 18.0762 11.6211 18.0566 11.8262 17.4316L13.418 12.4805L16.5527 12.4805C17.0117 12.4805 17.2168 12.2754 17.3926 11.8555L19.2188 7.55859L22.373 17.4609C22.5684 18.0762 23.5449 18.0566 23.75 17.4316L25.3418 12.4805L27.8613 12.4805L27.8613 11.0645L24.9707 11.0645C24.5215 11.0645 24.2773 11.2793 24.1309 11.6797L23.0371 14.8145L19.8926 5.32227C19.6973 4.72656 18.7891 4.69727 18.5254 5.35156L16.1816 11.0645L13.0469 11.0645C12.5977 11.0645 12.3535 11.2793 12.207 11.6797L11.1133 14.8145L7.96875 5.32227C7.77344 4.72656 6.86523 4.69727 6.60156 5.35156L4.25781 11.0645L1.73828 11.0645Z", fillAlpha = 0.85f)
        }
        return _sFWaveformPathEcgRectangle!!
    }

private var _sFWaveformPathEcgRectangle: ImageVector? = null
