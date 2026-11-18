package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSignpostRight (monochrome)
 * Viewport: 25.5396 x 26.084
 */
public val SfSymbols.Monochrome.SFSignpostRight: ImageVector
    get() {
        if (_sFSignpostRight != null) {
            return _sFSignpostRight!!
        }
        _sFSignpostRight = sfIcon(
            name = "Monochrome.SFSignpostRight",
            viewportWidth = 25.5396f,
            viewportHeight = 26.084f
        ) {
            addSfPath("M2.80887 15.4688L19.0686 15.4492C20.1429 15.4492 20.6507 15.1367 21.2659 14.5605L24.5667 11.4355C25.846 10.2344 25.885 8.98438 24.5569 7.72461L21.2659 4.60938C20.6507 4.0332 20.1429 3.71094 19.0784 3.71094L2.80887 3.71094C1.18777 3.71094 0.279572 4.59961 0.279572 6.18164L0.279572 12.9883C0.279572 14.5703 1.18777 15.4688 2.80887 15.4688ZM2.84793 13.7305C2.34988 13.7305 2.05692 13.4277 2.05692 12.9395L2.05692 6.23047C2.05692 5.74219 2.34988 5.43945 2.84793 5.43945L19.0198 5.43945C19.5374 5.43945 19.7327 5.54688 20.0159 5.80078L23.4436 9.00391C23.805 9.33594 23.805 9.80469 23.4339 10.1465L20.0061 13.3691C19.7327 13.623 19.5374 13.7305 19.01 13.7305ZM10.885 5.01953L12.6136 5.01953L12.6136 0.869141C12.6136 0.390625 12.2229 0 11.7542 0C11.2757 0 10.885 0.390625 10.885 0.869141ZM10.885 14.834L10.885 25.9375L12.6136 25.9375L12.6136 14.834ZM6.14871 25.2051C6.14871 25.6738 6.53934 26.0645 7.00809 26.0645L16.5003 26.0645C16.9788 26.0645 17.3694 25.6738 17.3694 25.2051C17.3694 24.7266 16.9788 24.3359 16.5003 24.3359L7.00809 24.3359C6.53934 24.3359 6.14871 24.7266 6.14871 25.2051Z", fillAlpha = 0.85f)
        }
        return _sFSignpostRight!!
    }

private var _sFSignpostRight: ImageVector? = null
