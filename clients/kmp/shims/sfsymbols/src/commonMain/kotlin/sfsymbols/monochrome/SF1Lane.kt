package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF1Lane (monochrome)
 * Viewport: 23.3203 x 18.9746
 */
public val SfSymbols.Monochrome.SF1Lane: ImageVector
    get() {
        if (_sF1Lane != null) {
            return _sF1Lane!!
        }
        _sF1Lane = sfIcon(
            name = "Monochrome.SF1Lane",
            viewportWidth = 23.3203f,
            viewportHeight = 18.9746f
        ) {
            addSfPath("M0 18.1055C0 18.584 0.390625 18.9746 0.869141 18.9746C1.33789 18.9746 1.72852 18.584 1.72852 18.1055L1.72852 0.898438C1.72852 0.419922 1.33789 0.0292969 0.869141 0.0292969C0.390625 0.0292969 0 0.419922 0 0.898438ZM21.2305 18.1055C21.2305 18.584 21.6211 18.9746 22.0898 18.9746C22.5684 18.9746 22.959 18.584 22.959 18.1055L22.959 0.898438C22.959 0.419922 22.5684 0.0292969 22.0898 0.0292969C21.6211 0.0292969 21.2305 0.419922 21.2305 0.898438Z", fillAlpha = 0.85f)
            addSfPath("M12.0801 15.2832C12.6172 15.2832 12.9395 14.9023 12.9395 14.3652L12.9395 5.07812C12.9395 4.46289 12.5586 4.11133 11.9531 4.11133C11.4551 4.11133 11.1133 4.32617 10.7227 4.61914L8.94531 5.92773C8.70117 6.12305 8.54492 6.32812 8.54492 6.63086C8.54492 6.99219 8.80859 7.27539 9.16992 7.27539C9.3457 7.27539 9.48242 7.22656 9.61914 7.11914L11.1914 5.94727L11.2109 5.94727L11.2109 14.3652C11.2109 14.9023 11.5332 15.2832 12.0801 15.2832Z", fillAlpha = 0.85f)
        }
        return _sF1Lane!!
    }

private var _sF1Lane: ImageVector? = null
