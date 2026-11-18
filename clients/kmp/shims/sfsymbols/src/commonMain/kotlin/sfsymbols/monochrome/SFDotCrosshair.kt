package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDotCrosshair (monochrome)
 * Viewport: 28.1055 x 27.7637
 */
public val SfSymbols.Monochrome.SFDotCrosshair: ImageVector
    get() {
        if (_sFDotCrosshair != null) {
            return _sFDotCrosshair!!
        }
        _sFDotCrosshair = sfIcon(
            name = "Monochrome.SFDotCrosshair",
            viewportWidth = 28.1055f,
            viewportHeight = 27.7637f
        ) {
            addSfPath("M13.8867 19.3848C14.227 19.3848 14.5604 19.3535 14.8828 19.2894L14.8828 26.7383C14.8828 27.3047 14.4434 27.7637 13.8672 27.7637C13.3008 27.7637 12.8613 27.3047 12.8613 26.7383L12.8613 19.2854C13.1931 19.3518 13.5362 19.3848 13.8867 19.3848ZM8.37891 13.877C8.37891 14.2279 8.41181 14.571 8.47821 14.9023L1.01562 14.9023C0.458984 14.9023 0 14.4629 0 13.8867C0 13.3203 0.458984 12.8809 1.01562 12.8809L8.47502 12.8809C8.41046 13.2032 8.37891 13.5366 8.37891 13.877ZM27.7441 13.8867C27.7441 14.4629 27.2852 14.9023 26.7188 14.9023L19.2855 14.9023C19.3519 14.571 19.3848 14.2279 19.3848 13.877C19.3848 13.5366 19.3532 13.2032 19.2887 12.8809L26.7188 12.8809C27.2852 12.8809 27.7441 13.3203 27.7441 13.8867ZM14.8828 1.03516L14.8828 8.48435C14.5604 8.42008 14.227 8.38867 13.8867 8.38867C13.5362 8.38867 13.1931 8.42179 12.8613 8.48839L12.8613 1.03516C12.8613 0.478516 13.3008 0.0195312 13.8672 0.0195312C14.4434 0.0195312 14.8828 0.478516 14.8828 1.03516Z", fillAlpha = 0.85f)
            addSfPath("M13.8867 17.832C16.0449 17.832 17.8223 16.0645 17.8223 13.877C17.8223 11.7188 16.0449 9.95117 13.8867 9.95117C11.709 9.95117 9.93164 11.7188 9.93164 13.877C9.93164 16.0645 11.709 17.832 13.8867 17.832Z", fillAlpha = 0.85f)
        }
        return _sFDotCrosshair!!
    }

private var _sFDotCrosshair: ImageVector? = null
