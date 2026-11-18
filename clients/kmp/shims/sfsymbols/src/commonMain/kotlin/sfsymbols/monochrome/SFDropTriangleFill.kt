package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDropTriangleFill (monochrome)
 * Viewport: 26.6504 x 24.0723
 */
public val SfSymbols.Monochrome.SFDropTriangleFill: ImageVector
    get() {
        if (_sFDropTriangleFill != null) {
            return _sFDropTriangleFill!!
        }
        _sFDropTriangleFill = sfIcon(
            name = "Monochrome.SFDropTriangleFill",
            viewportWidth = 26.6504f,
            viewportHeight = 24.0723f
        ) {
            addSfPath("M15.9277 1.62109L25.8008 18.8867C26.123 19.4434 26.2891 20.0488 26.2891 20.6348C26.2891 22.4414 25.0586 23.8672 23.0176 23.8672L3.26172 23.8672C1.23047 23.8672 0 22.4414 0 20.6348C0 20.0488 0.15625 19.4531 0.488281 18.8867L10.3613 1.62109C10.9766 0.537109 12.0508 0 13.1445 0C14.2285 0 15.3125 0.537109 15.9277 1.62109ZM12.6074 9.00391C11.8066 10.2441 10.8887 11.7383 10.3223 12.9883C9.95117 13.7695 9.53125 14.7266 9.53125 15.7324C9.53125 17.8223 10.9766 19.2285 13.1348 19.2285C15.293 19.2285 16.7383 17.8223 16.7383 15.7324C16.7383 14.7266 16.3281 13.7695 15.957 12.9883C15.3809 11.7383 14.4727 10.2441 13.6621 9.00391C13.5156 8.76953 13.3594 8.66211 13.1348 8.66211C12.9199 8.66211 12.7637 8.76953 12.6074 9.00391Z", fillAlpha = 0.85f)
        }
        return _sFDropTriangleFill!!
    }

private var _sFDropTriangleFill: ImageVector? = null
