package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMustacheFill (dualtone)
 * Viewport: 35.8691 x 13.3691
 */
public val SfSymbols.Dualtone.SFMustacheFill: ImageVector
    get() {
        if (_sFMustacheFill != null) {
            return _sFMustacheFill!!
        }
        _sFMustacheFill = sfIcon(
            name = "Dualtone.SFMustacheFill",
            viewportWidth = 35.8691f,
            viewportHeight = 13.3691f
        ) {
            addSfPath("M0 6.23047C0 10.3516 4.26758 13.3691 9.3457 13.3691C13.0371 13.3691 16.0156 12.0703 17.7539 9.7168C19.4922 12.0703 22.4805 13.3691 26.1621 13.3691C31.2402 13.3691 35.5078 10.3516 35.5078 6.23047C35.5078 4.90234 35.1465 3.99414 34.3164 3.99414C34.043 3.99414 33.75 4.16992 33.5547 4.45312C32.8613 5.41016 31.748 5.9375 30.3906 5.9375C26.6016 5.9375 25.5566 1.30859 20.9961 1.30859C18.916 1.30859 17.4414 2.42188 17.002 4.29688L18.5059 4.29688C18.0664 2.42188 16.5918 1.30859 14.5117 1.30859C9.95117 1.30859 8.90625 5.9375 5.11719 5.9375C3.83789 5.9375 2.72461 5.41992 1.94336 4.44336C1.72852 4.16992 1.46484 3.99414 1.19141 3.99414C0.371094 3.99414 0 4.90234 0 6.23047Z", fillAlpha = 0.85f)
        }
        return _sFMustacheFill!!
    }

private var _sFMustacheFill: ImageVector? = null
