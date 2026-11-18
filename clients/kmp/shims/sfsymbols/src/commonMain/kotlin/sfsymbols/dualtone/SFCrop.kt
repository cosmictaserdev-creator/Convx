package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCrop (dualtone)
 * Viewport: 27.6074 x 27.9395
 */
public val SfSymbols.Dualtone.SFCrop: ImageVector
    get() {
        if (_sFCrop != null) {
            return _sFCrop!!
        }
        _sFCrop = sfIcon(
            name = "Dualtone.SFCrop",
            viewportWidth = 27.6074f,
            viewportHeight = 27.9395f
        ) {
            addSfPath("M27.2461 20.9863C27.2461 20.4785 26.9043 20.1758 26.3867 20.1758L8.20312 20.1758C7.96875 20.1758 7.87109 20.0781 7.87109 19.834L7.87109 1.68945C7.87109 1.16211 7.5293 0.791016 7.01172 0.791016C6.49414 0.791016 6.13281 1.16211 6.13281 1.68945L6.13281 20.8984C6.13281 21.4453 6.49414 21.8066 7.05078 21.8066L26.3867 21.8066C26.9043 21.8066 27.2461 21.4941 27.2461 20.9863ZM0 7.74414C0 8.25195 0.341797 8.55469 0.859375 8.55469L19.0332 8.55469C19.2773 8.55469 19.375 8.65234 19.375 8.89648L19.375 27.041C19.375 27.5684 19.7168 27.9395 20.2344 27.9395C20.752 27.9395 21.1035 27.5684 21.1035 27.041L21.1035 7.83203C21.1035 7.28516 20.752 6.92383 20.1953 6.92383L0.859375 6.92383C0.341797 6.92383 0 7.24609 0 7.74414Z", fillAlpha = 0.85f)
        }
        return _sFCrop!!
    }

private var _sFCrop: ImageVector? = null
