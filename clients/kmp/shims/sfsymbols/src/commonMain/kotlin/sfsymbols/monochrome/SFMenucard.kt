package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMenucard (monochrome)
 * Viewport: 21.0059 x 28.8234
 */
public val SfSymbols.Monochrome.SFMenucard: ImageVector
    get() {
        if (_sFMenucard != null) {
            return _sFMenucard!!
        }
        _sFMenucard = sfIcon(
            name = "Monochrome.SFMenucard",
            viewportWidth = 21.0059f,
            viewportHeight = 28.8234f
        ) {
            addSfPath("M2.36328 26.233L16.5918 28.7427C19.1016 29.1822 20.6445 27.8052 20.6445 25.1002L20.6445 9.48492C20.6445 7.07281 19.3945 6.12555 16.9238 5.68609L3.06641 3.24469C1.20117 2.91266 0 3.88922 0 5.69586L0 23.4986C0 25.0416 0.78125 25.9498 2.36328 26.233ZM2.76367 24.524C2.07031 24.4068 1.72852 23.9966 1.72852 23.2642L1.72852 5.98883C1.72852 5.21734 2.24609 4.82672 3.08594 4.9732L16.8359 7.39508C18.2129 7.63922 18.916 8.23492 18.916 9.43609L18.916 24.9341C18.916 26.4576 18.0078 27.2193 16.5527 26.9556ZM17.793 7.10211L19.5215 7.10211L19.5215 4.11383C19.5215 1.60406 17.832 0.402891 15 0.910704L1.81641 3.23492L2.44141 4.73883L15.5957 2.52203C16.9727 2.29742 17.793 2.86383 17.793 4.065Z", fillAlpha = 0.85f)
            addSfPath("M4.88281 10.5201L14.873 12.2779C15.3906 12.3658 15.7129 12.0435 15.7129 11.5943C15.7129 11.1744 15.498 10.9595 15.1074 10.8814L5.15625 9.14313C4.64844 9.05523 4.29688 9.35797 4.29688 9.79742C4.29688 10.188 4.50195 10.4615 4.88281 10.5201Z", fillAlpha = 0.85f)
            addSfPath("M4.88281 15.3834L14.873 17.1412C15.3906 17.2291 15.7129 16.9068 15.7129 16.4478C15.7129 16.0377 15.498 15.813 15.1074 15.7447L5.15625 14.0064C4.64844 13.9185 4.29688 14.2213 4.29688 14.6607C4.29688 15.0513 4.50195 15.315 4.88281 15.3834Z", fillAlpha = 0.85f)
        }
        return _sFMenucard!!
    }

private var _sFMenucard: ImageVector? = null
