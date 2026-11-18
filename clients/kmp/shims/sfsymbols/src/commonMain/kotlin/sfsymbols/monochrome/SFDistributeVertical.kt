package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDistributeVertical (monochrome)
 * Viewport: 29.7461 x 23.0762
 */
public val SfSymbols.Monochrome.SFDistributeVertical: ImageVector
    get() {
        if (_sFDistributeVertical != null) {
            return _sFDistributeVertical!!
        }
        _sFDistributeVertical = sfIcon(
            name = "Monochrome.SFDistributeVertical",
            viewportWidth = 29.7461f,
            viewportHeight = 23.0762f
        ) {
            addSfPath("M5.48828 16.0742L23.8965 16.0742C25.5762 16.0742 26.5137 15.1465 26.5137 13.4863L26.5137 9.57031C26.5137 7.91992 25.5762 6.99219 23.8965 6.99219L5.48828 6.99219C3.80859 6.99219 2.87109 7.91992 2.87109 9.57031L2.87109 13.4863C2.87109 15.1465 3.80859 16.0742 5.48828 16.0742ZM5.52734 14.4434C4.88281 14.4434 4.50195 14.0625 4.50195 13.3984L4.50195 9.6582C4.50195 9.00391 4.88281 8.62305 5.52734 8.62305L23.8574 8.62305C24.502 8.62305 24.8828 9.00391 24.8828 9.6582L24.8828 13.3984C24.8828 14.0625 24.502 14.4434 23.8574 14.4434Z", fillAlpha = 0.85f)
            addSfPath("M0.751953 1.5918L28.623 1.5918C29.0527 1.5918 29.3848 1.2207 29.3848 0.800781C29.3848 0.371094 29.0527 0 28.623 0L0.751953 0C0.332031 0 0 0.371094 0 0.800781C0 1.2207 0.332031 1.5918 0.751953 1.5918ZM0.751953 21.4648C0.332031 21.4648 0 21.8359 0 22.2656C0 22.6855 0.332031 23.0566 0.751953 23.0566L28.623 23.0566C29.0527 23.0566 29.3848 22.6855 29.3848 22.2656C29.3848 21.8359 29.0527 21.4648 28.623 21.4648Z", fillAlpha = 0.85f)
        }
        return _sFDistributeVertical!!
    }

private var _sFDistributeVertical: ImageVector? = null
