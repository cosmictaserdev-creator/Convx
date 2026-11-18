package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF4l (dualtone)
 * Viewport: 28.9355 x 18.6426
 */
public val SfSymbols.Dualtone.SF4l: ImageVector
    get() {
        if (_sF4l != null) {
            return _sF4l!!
        }
        _sF4l = sfIcon(
            name = "Dualtone.SF4l",
            viewportWidth = 28.9355f,
            viewportHeight = 18.6426f
        ) {
            addSfPath("M10.0293 18.623C10.5566 18.623 10.9375 18.2227 10.9375 17.666L10.9375 14.3164L12.8223 14.3164C13.3203 14.3164 13.6621 14.0039 13.6621 13.5059C13.6621 13.0371 13.3105 12.6953 12.8223 12.6953L10.9375 12.6953L10.9375 1.51367C10.9375 0.634766 10.293 0.0195312 9.4043 0.0195312C8.45703 0.0195312 7.90039 0.703125 7.40234 1.49414L0.410156 11.875C0.146484 12.2949 0 12.7246 0 13.1152C0 13.8086 0.458984 14.3164 1.30859 14.3164L9.12109 14.3164L9.12109 17.666C9.12109 18.3203 9.58008 18.623 10.0293 18.623ZM9.12109 12.7148L1.82617 12.7148L1.82617 12.627L9.02344 1.98242L9.12109 1.98242ZM18.4375 18.3789L27.6855 18.3789C28.2031 18.3789 28.5742 18.0273 28.5742 17.5293C28.5742 17.0508 28.2031 16.709 27.6855 16.709L19.3945 16.709L19.3945 0.947266C19.3945 0.419922 18.9746 0 18.4473 0C17.9102 0 17.4902 0.419922 17.4902 0.947266L17.4902 17.3828C17.4902 18.0859 17.9492 18.3789 18.4375 18.3789Z", fillAlpha = 0.85f)
        }
        return _sF4l!!
    }

private var _sF4l: ImageVector? = null
