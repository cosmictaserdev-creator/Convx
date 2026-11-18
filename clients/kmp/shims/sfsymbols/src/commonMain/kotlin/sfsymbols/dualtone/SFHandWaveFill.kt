package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHandWaveFill (dualtone)
 * Viewport: 23.96 x 23.4766
 */
public val SfSymbols.Dualtone.SFHandWaveFill: ImageVector
    get() {
        if (_sFHandWaveFill != null) {
            return _sFHandWaveFill!!
        }
        _sFHandWaveFill = sfIcon(
            name = "Dualtone.SFHandWaveFill",
            viewportWidth = 23.96f,
            viewportHeight = 23.4766f
        ) {
            addSfPath("M8.63525 20.6006C12.5903 24.5459 17.3853 24.2041 20.8228 20.7764C23.645 17.9541 24.2407 14.8682 22.7466 11.0791C22.0337 9.12598 20.979 7.08496 19.7388 4.8291C19.3091 3.98926 18.7817 2.9541 18.4302 2.59277C17.9614 2.13379 17.3267 2.17285 16.8384 2.6416C16.3306 3.14941 16.3208 3.78418 16.7114 4.89746L18.1567 8.74512C18.3325 9.19434 18.2739 9.46777 18.1079 9.63379C17.9126 9.83887 17.6294 9.86816 17.2778 9.50684L8.13721 0.366211C7.62939-0.12207 6.80908-0.12207 6.31104 0.366211C5.81299 0.874023 5.81299 1.68457 6.31104 2.19238L12.8149 8.69629C12.4731 8.85254 12.1021 9.04785 11.7505 9.29199L4.11377 1.64551C3.61572 1.1377 2.78564 1.1377 2.27783 1.64551C1.78955 2.14355 1.78955 2.96387 2.27783 3.47168L9.82666 11.0107C9.54346 11.3428 9.27979 11.6943 9.03564 12.0264L2.18994 5.19043C1.69189 4.69238 0.881348 4.69238 0.373535 5.19043C-0.124512 5.68848-0.124512 6.50879 0.373535 7.0166L7.80518 14.4385C7.62939 14.8975 7.51221 15.3369 7.43408 15.7568L2.2876 10.6104C1.78955 10.1025 0.969238 10.1025 0.461426 10.6104C-0.0366211 11.1084-0.0366211 11.9189 0.461426 12.4268Z", fillAlpha = 0.85f)
        }
        return _sFHandWaveFill!!
    }

private var _sFHandWaveFill: ImageVector? = null
