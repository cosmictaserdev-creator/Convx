package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEnvelopeOpenFill (dualtone)
 * Viewport: 30.4785 x 28.9746
 */
public val SfSymbols.Dualtone.SFEnvelopeOpenFill: ImageVector
    get() {
        if (_sFEnvelopeOpenFill != null) {
            return _sFEnvelopeOpenFill!!
        }
        _sFEnvelopeOpenFill = sfIcon(
            name = "Dualtone.SFEnvelopeOpenFill",
            viewportWidth = 30.4785f,
            viewportHeight = 28.9746f
        ) {
            addSfPath("M0.175781 26.6406L9.48242 18.252L2.42188 11.1914C2.05078 10.8203 2.07031 10.2832 2.54883 9.96094L13.7598 2.34375C14.6973 1.69922 15.4297 1.69922 16.3574 2.34375L27.5684 9.96094C28.0469 10.2832 28.0566 10.8301 27.7051 11.1914L20.6348 18.252L29.9512 26.6406C30.0586 26.2207 30.1172 25.7422 30.1172 25.2148L30.1172 12.2559C30.1172 10.4004 29.8242 9.59961 28.4961 8.70117L17.4805 1.13281C15.7031-0.0878906 14.4141-0.0878906 12.6367 1.13281L1.62109 8.70117C0.302734 9.59961 0 10.4004 0 12.2559L0 25.2148C0 25.7422 0.0585938 26.2207 0.175781 26.6406ZM3.79883 28.9746L26.3281 28.9746C27.666 28.9746 28.5352 28.6133 29.1406 28.0176L16.7969 16.9043C16.2207 16.3867 15.6641 16.1523 15.0586 16.1523C14.4629 16.1523 13.8965 16.3867 13.3203 16.9043L0.986328 27.998C1.5918 28.6035 2.46094 28.9746 3.79883 28.9746Z", fillAlpha = 0.85f)
        }
        return _sFEnvelopeOpenFill!!
    }

private var _sFEnvelopeOpenFill: ImageVector? = null
