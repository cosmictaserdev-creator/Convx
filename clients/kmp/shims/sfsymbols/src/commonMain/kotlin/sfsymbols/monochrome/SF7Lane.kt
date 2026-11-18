package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF7Lane (monochrome)
 * Viewport: 23.3203 x 18.9746
 */
public val SfSymbols.Monochrome.SF7Lane: ImageVector
    get() {
        if (_sF7Lane != null) {
            return _sF7Lane!!
        }
        _sF7Lane = sfIcon(
            name = "Monochrome.SF7Lane",
            viewportWidth = 23.3203f,
            viewportHeight = 18.9746f
        ) {
            addSfPath("M0 18.1055C0 18.584 0.390625 18.9746 0.869141 18.9746C1.33789 18.9746 1.72852 18.584 1.72852 18.1055L1.72852 0.898438C1.72852 0.419922 1.33789 0.0292969 0.869141 0.0292969C0.390625 0.0292969 0 0.419922 0 0.898438ZM21.2305 18.1055C21.2305 18.584 21.6211 18.9746 22.0898 18.9746C22.5684 18.9746 22.959 18.584 22.959 18.1055L22.959 0.898438C22.959 0.419922 22.5684 0.0292969 22.0898 0.0292969C21.6211 0.0292969 21.2305 0.419922 21.2305 0.898438Z", fillAlpha = 0.85f)
            addSfPath("M10.0879 15.2832C10.498 15.2832 10.7129 15.1074 10.8691 14.7363L14.5605 6.2207C14.707 5.89844 14.8242 5.57617 14.8242 5.22461C14.8242 4.60938 14.3848 4.29688 13.8477 4.29688L8.79883 4.29688C8.38867 4.29688 8.10547 4.58008 8.10547 4.99023C8.10547 5.41016 8.38867 5.69336 8.79883 5.69336L13.0957 5.69336L13.0957 5.77148L9.44336 13.9355C9.3457 14.1406 9.28711 14.3359 9.28711 14.5703C9.28711 15.0098 9.61914 15.2832 10.0879 15.2832Z", fillAlpha = 0.85f)
        }
        return _sF7Lane!!
    }

private var _sF7Lane: ImageVector? = null
