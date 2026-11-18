package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFigureSeatedSideLeft (monochrome)
 * Viewport: 24.1532 x 25.166
 */
public val SfSymbols.Monochrome.SFFigureSeatedSideLeft: ImageVector
    get() {
        if (_sFFigureSeatedSideLeft != null) {
            return _sFFigureSeatedSideLeft!!
        }
        _sFFigureSeatedSideLeft = sfIcon(
            name = "Monochrome.SFFigureSeatedSideLeft",
            viewportWidth = 24.1532f,
            viewportHeight = 25.166f
        ) {
            addSfPath("M20.4506 6.73828C22.3061 6.73828 23.7904 5.19531 23.7904 3.39844C23.7904 1.52344 22.3061 0 20.4506 0C18.5756 0 17.0815 1.52344 17.0815 3.39844C17.0815 5.26367 18.5756 6.73828 20.4506 6.73828ZM0.450604 24.4727C1.09514 25.1465 2.19865 25.0879 2.98967 24.2773L8.33146 18.7598C8.50724 18.5742 8.6342 18.5254 8.90764 18.6328L13.1459 20.2441C15.8119 21.2598 18.3022 20.3223 19.5131 18.125L22.1401 13.3594C23.185 11.4453 22.8432 9.49219 21.0365 8.53516C19.2885 7.56836 17.4623 8.29102 16.4076 10.2344L12.9311 16.582L9.97209 15.5078C8.49748 14.9707 7.27678 15.1367 6.11467 16.3477L0.567792 22.0898C-0.203693 22.8809-0.135333 23.877 0.450604 24.4727Z", fillAlpha = 0.85f)
        }
        return _sFFigureSeatedSideLeft!!
    }

private var _sFFigureSeatedSideLeft: ImageVector? = null
