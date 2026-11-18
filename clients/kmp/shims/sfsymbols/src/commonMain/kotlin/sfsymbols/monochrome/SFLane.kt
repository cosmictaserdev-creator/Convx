package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFLane (monochrome)
 * Viewport: 23.3203 x 18.9746
 */
public val SfSymbols.Monochrome.SFLane: ImageVector
    get() {
        if (_sFLane != null) {
            return _sFLane!!
        }
        _sFLane = sfIcon(
            name = "Monochrome.SFLane",
            viewportWidth = 23.3203f,
            viewportHeight = 18.9746f
        ) {
            addSfPath("M0 18.1055C0 18.584 0.390625 18.9746 0.869141 18.9746C1.33789 18.9746 1.72852 18.584 1.72852 18.1055L1.72852 0.898438C1.72852 0.419922 1.33789 0.0292969 0.869141 0.0292969C0.390625 0.0292969 0 0.419922 0 0.898438ZM21.2305 18.1055C21.2305 18.584 21.6211 18.9746 22.0898 18.9746C22.5684 18.9746 22.959 18.584 22.959 18.1055L22.959 0.898438C22.959 0.419922 22.5684 0.0292969 22.0898 0.0292969C21.6211 0.0292969 21.2305 0.419922 21.2305 0.898438Z", fillAlpha = 0.85f)
        }
        return _sFLane!!
    }

private var _sFLane: ImageVector? = null
