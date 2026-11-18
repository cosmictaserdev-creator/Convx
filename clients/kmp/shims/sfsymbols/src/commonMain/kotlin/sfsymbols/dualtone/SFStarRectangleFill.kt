package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStarRectangleFill (dualtone)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Dualtone.SFStarRectangleFill: ImageVector
    get() {
        if (_sFStarRectangleFill != null) {
            return _sFStarRectangleFill!!
        }
        _sFStarRectangleFill = sfIcon(
            name = "Dualtone.SFStarRectangleFill",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L25.7812 22.959C28.3105 22.959 29.5898 21.6797 29.5898 19.1992L29.5898 3.76953C29.5898 1.2793 28.3105 0 25.7812 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.0254 18.1445C10.3613 18.6426 9.64844 18.125 9.91211 17.334L11.3867 12.8906L7.57812 10.166C6.95312 9.7168 7.16797 8.81836 8.00781 8.82812L12.6953 8.85742L14.1211 4.38477C14.3652 3.62305 15.2344 3.62305 15.4785 4.38477L16.9043 8.85742L21.5918 8.82812C22.4414 8.81836 22.6172 9.73633 22.0312 10.1562L18.2129 12.8906L19.6973 17.334C19.9609 18.125 19.248 18.6426 18.5742 18.1445L14.8047 15.3809Z", fillAlpha = 0.85f)
        }
        return _sFStarRectangleFill!!
    }

private var _sFStarRectangleFill: ImageVector? = null
