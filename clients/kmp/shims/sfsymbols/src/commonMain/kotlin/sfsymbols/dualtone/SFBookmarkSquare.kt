package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBookmarkSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFBookmarkSquare: ImageVector
    get() {
        if (_sFBookmarkSquare != null) {
            return _sFBookmarkSquare!!
        }
        _sFBookmarkSquare = sfIcon(
            name = "Dualtone.SFBookmarkSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M7.72461 18.1445C8.00781 18.1445 8.18359 17.9883 8.67188 17.5098L11.4258 14.7559C11.4551 14.7168 11.5137 14.7168 11.543 14.7559L14.2969 17.5098C14.7852 17.9883 14.9609 18.1445 15.2441 18.1445C15.6348 18.1445 15.8789 17.8711 15.8789 17.4121L15.8789 6.74805C15.8789 5.54688 15.2637 4.92188 14.0723 4.92188L8.89648 4.92188C7.70508 4.92188 7.08984 5.54688 7.08984 6.74805L7.08984 17.4121C7.08984 17.8711 7.33398 18.1445 7.72461 18.1445Z", fillAlpha = 0.85f)
        }
        return _sFBookmarkSquare!!
    }

private var _sFBookmarkSquare: ImageVector? = null
