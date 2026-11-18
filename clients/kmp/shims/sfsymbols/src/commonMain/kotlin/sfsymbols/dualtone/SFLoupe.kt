package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLoupe (dualtone)
 * Viewport: 21.9899 x 24.7168
 */
public val SfSymbols.Dualtone.SFLoupe: ImageVector
    get() {
        if (_sFLoupe != null) {
            return _sFLoupe!!
        }
        _sFLoupe = sfIcon(
            name = "Dualtone.SFLoupe",
            viewportWidth = 21.9899f,
            viewportHeight = 24.7168f
        ) {
            addSfPath("M10.8192 24.7168C17.3035 24.7168 22.5574 22.2168 21.5028 17.8906L19.491 9.60938L19.2762 4.64844C19.1492 1.88477 15.868 0.00976562 10.8094 0.00976562C5.76057 0.00976562 2.48909 1.88477 2.36213 4.64844L2.14729 9.60938L0.135572 17.8906C-0.919116 22.2168 4.32502 24.7168 10.8192 24.7168ZM10.8094 22.998C5.70198 22.998 1.0926 21.123 1.82502 18.1152L3.36799 11.7871C4.91096 13.0566 7.47932 13.8281 10.8192 13.8281C14.1492 13.8281 16.7176 13.0566 18.2703 11.7871L19.8035 18.1152C20.536 21.123 15.9266 22.998 10.8094 22.998ZM10.8094 6.38672C7.4012 6.38672 4.80354 5.29297 4.80354 3.95508C4.80354 2.60742 7.4012 1.51367 10.8094 1.51367C14.2274 1.51367 16.8348 2.60742 16.8348 3.95508C16.8348 5.29297 14.2274 6.38672 10.8094 6.38672Z", fillAlpha = 0.85f)
        }
        return _sFLoupe!!
    }

private var _sFLoupe: ImageVector? = null
