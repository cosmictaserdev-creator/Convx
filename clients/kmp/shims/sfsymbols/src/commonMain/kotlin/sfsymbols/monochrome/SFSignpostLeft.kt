package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSignpostLeft (monochrome)
 * Viewport: 26.2623 x 26.084
 */
public val SfSymbols.Monochrome.SFSignpostLeft: ImageVector
    get() {
        if (_sFSignpostLeft != null) {
            return _sFSignpostLeft!!
        }
        _sFSignpostLeft = sfIcon(
            name = "Monochrome.SFSignpostLeft",
            viewportWidth = 26.2623f,
            viewportHeight = 26.084f
        ) {
            addSfPath("M22.7307 15.4688C24.3518 15.4688 25.26 14.5703 25.26 12.9883L25.26 6.18164C25.26 4.59961 24.3518 3.71094 22.7307 3.71094L6.46121 3.71094C5.39676 3.71094 4.88895 4.0332 4.27371 4.60938L0.982697 7.72461C-0.345428 8.98438-0.306366 10.2344 0.972931 11.4355L4.27371 14.5605C4.88895 15.1367 5.39676 15.4492 6.47098 15.4492ZM22.6917 13.7305L6.52957 13.7305C6.00223 13.7305 5.80692 13.623 5.53348 13.3691L2.10574 10.1465C1.73465 9.80469 1.73465 9.33594 2.09598 9.00391L5.52371 5.80078C5.80692 5.54688 6.00223 5.43945 6.51981 5.43945L22.6917 5.43945C23.1897 5.43945 23.4827 5.74219 23.4827 6.23047L23.4827 12.9395C23.4827 13.4277 23.1897 13.7305 22.6917 13.7305ZM14.6546 5.01953L14.6546 0.869141C14.6546 0.390625 14.2639 0 13.7854 0C13.3167 0 12.9261 0.390625 12.9261 0.869141L12.9261 5.01953ZM14.6546 14.834L12.9261 14.834L12.9261 25.9375L14.6546 25.9375ZM19.3909 25.2051C19.3909 24.7266 19.0003 24.3359 18.5315 24.3359L9.03934 24.3359C8.56082 24.3359 8.1702 24.7266 8.1702 25.2051C8.1702 25.6738 8.56082 26.0645 9.03934 26.0645L18.5315 26.0645C19.0003 26.0645 19.3909 25.6738 19.3909 25.2051Z", fillAlpha = 0.85f)
        }
        return _sFSignpostLeft!!
    }

private var _sFSignpostLeft: ImageVector? = null
