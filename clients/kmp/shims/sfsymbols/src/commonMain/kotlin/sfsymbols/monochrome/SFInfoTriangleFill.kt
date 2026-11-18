package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInfoTriangleFill (monochrome)
 * Viewport: 26.6504 x 24.0723
 */
public val SfSymbols.Monochrome.SFInfoTriangleFill: ImageVector
    get() {
        if (_sFInfoTriangleFill != null) {
            return _sFInfoTriangleFill!!
        }
        _sFInfoTriangleFill = sfIcon(
            name = "Monochrome.SFInfoTriangleFill",
            viewportWidth = 26.6504f,
            viewportHeight = 24.0723f
        ) {
            addSfPath("M15.9277 1.62109L25.8008 18.8867C26.123 19.4434 26.2891 20.0488 26.2891 20.6348C26.2891 22.4414 25.0586 23.8672 23.0176 23.8672L3.26172 23.8672C1.23047 23.8672 0 22.4414 0 20.6348C0 20.0488 0.15625 19.4531 0.488281 18.8867L10.3613 1.62109C10.9766 0.537109 12.0508 0 13.1445 0C14.2285 0 15.3125 0.537109 15.9277 1.62109ZM11.25 11.0352C10.8398 11.0352 10.5176 11.3574 10.5176 11.748C10.5176 12.168 10.8398 12.4707 11.25 12.4707L12.6562 12.4707L12.6562 18.2031L11.084 18.2031C10.6641 18.2031 10.3418 18.5352 10.3418 18.9258C10.3418 19.3359 10.6641 19.6387 11.084 19.6387L15.8301 19.6387C16.25 19.6387 16.5625 19.3359 16.5625 18.9258C16.5625 18.5352 16.25 18.2031 15.8301 18.2031L14.248 18.2031L14.248 11.9336C14.248 11.4062 13.9746 11.0352 13.4668 11.0352ZM11.6895 7.62695C11.6895 8.38867 12.2949 8.99414 13.0664 8.99414C13.8281 8.99414 14.4238 8.38867 14.4238 7.62695C14.4238 6.85547 13.8281 6.24023 13.0664 6.24023C12.2949 6.24023 11.6895 6.85547 11.6895 7.62695Z", fillAlpha = 0.85f)
        }
        return _sFInfoTriangleFill!!
    }

private var _sFInfoTriangleFill: ImageVector? = null
