package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFRighttriangle (dualtone)
 * Viewport: 24.2578 x 25.6543
 */
public val SfSymbols.Dualtone.SFRighttriangle: ImageVector
    get() {
        if (_sFRighttriangle != null) {
            return _sFRighttriangle!!
        }
        _sFRighttriangle = sfIcon(
            name = "Dualtone.SFRighttriangle",
            viewportWidth = 24.2578f,
            viewportHeight = 25.6543f
        ) {
            addSfPath("M1.5918 23.2617L21.2012 23.2617C22.627 23.2617 23.2617 22.627 23.2617 21.1914L23.2617 1.5918C23.2617 0.615234 22.6367 0 21.6992 0C21.1035 0 20.6836 0.273438 20.2344 0.722656L0.712891 20.2441C0.263672 20.6836 0 21.1133 0 21.709C0 22.6465 0.605469 23.2617 1.5918 23.2617ZM2.48047 21.5039C2.34375 21.5039 2.25586 21.4551 2.25586 21.3574C2.25586 21.2891 2.28516 21.2402 2.37305 21.1523L21.1426 2.37305C21.2305 2.29492 21.2793 2.26562 21.3477 2.26562C21.4453 2.26562 21.4941 2.35352 21.4941 2.48047L21.4941 20.9863C21.4941 21.416 21.4062 21.5039 20.9766 21.5039Z", fillAlpha = 0.85f)
        }
        return _sFRighttriangle!!
    }

private var _sFRighttriangle: ImageVector? = null
