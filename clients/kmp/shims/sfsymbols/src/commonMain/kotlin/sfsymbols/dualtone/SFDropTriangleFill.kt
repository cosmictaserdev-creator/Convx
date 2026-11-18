package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDropTriangleFill (dualtone)
 * Viewport: 26.6504 x 24.0723
 */
public val SfSymbols.Dualtone.SFDropTriangleFill: ImageVector
    get() {
        if (_sFDropTriangleFill != null) {
            return _sFDropTriangleFill!!
        }
        _sFDropTriangleFill = sfIcon(
            name = "Dualtone.SFDropTriangleFill",
            viewportWidth = 26.6504f,
            viewportHeight = 24.0723f
        ) {
            addSfPath("M3.26172 23.8672L23.0176 23.8672C25.0586 23.8672 26.2891 22.4414 26.2891 20.6348C26.2891 20.0488 26.123 19.4434 25.8008 18.8867L15.9277 1.62109C15.3125 0.537109 14.2285 0 13.1445 0C12.0508 0 10.9766 0.537109 10.3613 1.62109L0.488281 18.8867C0.15625 19.4531 0 20.0488 0 20.6348C0 22.4414 1.23047 23.8672 3.26172 23.8672Z", fillAlpha = 0.2125f)
            addSfPath("M13.1348 19.2285C10.9766 19.2285 9.53125 17.8223 9.53125 15.7324C9.53125 14.7266 9.95117 13.7695 10.3223 12.9883C10.8887 11.7383 11.8066 10.2441 12.6074 9.00391C12.7637 8.76953 12.9199 8.66211 13.1348 8.66211C13.3594 8.66211 13.5156 8.76953 13.6621 9.00391C14.4727 10.2441 15.3809 11.7383 15.957 12.9883C16.3281 13.7695 16.7383 14.7266 16.7383 15.7324C16.7383 17.8223 15.293 19.2285 13.1348 19.2285Z", fillAlpha = 0.85f)
        }
        return _sFDropTriangleFill!!
    }

private var _sFDropTriangleFill: ImageVector? = null
