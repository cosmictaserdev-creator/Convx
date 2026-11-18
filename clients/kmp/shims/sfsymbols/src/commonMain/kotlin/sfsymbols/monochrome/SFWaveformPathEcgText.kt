package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWaveformPathEcgText (monochrome)
 * Viewport: 23.1152 x 28.1543
 */
public val SfSymbols.Monochrome.SFWaveformPathEcgText: ImageVector
    get() {
        if (_sFWaveformPathEcgText != null) {
            return _sFWaveformPathEcgText!!
        }
        _sFWaveformPathEcgText = sfIcon(
            name = "Monochrome.SFWaveformPathEcgText",
            viewportWidth = 23.1152f,
            viewportHeight = 28.1543f
        ) {
            addSfPath("M0.869141 27.5391L15.4492 27.5391C15.9277 27.5391 16.3184 27.1485 16.3184 26.6699C16.3184 26.2012 15.9277 25.8106 15.4492 25.8106L0.869141 25.8106C0.390625 25.8106 0 26.2012 0 26.6699C0 27.1485 0.390625 27.5391 0.869141 27.5391Z", fillAlpha = 0.85f)
            addSfPath("M0.869141 20.9766L21.8848 20.9766C22.3535 20.9766 22.7539 20.586 22.7539 20.1172C22.7539 19.6387 22.3535 19.2481 21.8848 19.2481L0.869141 19.2481C0.390625 19.2481 0 19.6387 0 20.1172C0 20.586 0.390625 20.9766 0.869141 20.9766Z", fillAlpha = 0.85f)
            addSfPath("M2.72461 10.127L6.5918 10.127C7.06055 10.127 7.44141 9.85354 7.54883 9.39455L8.71094 4.64846L10.8984 15.2344C11.0938 16.1328 12.3438 16.1621 12.5879 15.2539L14.9316 6.91408L15.5664 9.33596C15.7129 9.8633 16.0156 10.127 16.5039 10.127L20.0391 10.127C20.5273 10.127 20.9277 9.72658 20.9277 9.28713C20.9277 8.78908 20.5469 8.41799 20.0391 8.41799L16.9824 8.41799L15.8105 3.78908C15.5859 2.959 14.4434 2.94924 14.1602 3.82814L11.8359 11.7383L9.54102 0.683614C9.36523-0.224589 8.13477-0.224589 7.91016 0.664082L6.07422 8.41799L2.72461 8.41799C2.2168 8.41799 1.82617 8.79885 1.82617 9.28713C1.82617 9.72658 2.22656 10.127 2.72461 10.127Z", fillAlpha = 0.85f)
        }
        return _sFWaveformPathEcgText!!
    }

private var _sFWaveformPathEcgText: ImageVector? = null
