package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFChartLineFlattrendXyaxis (dualtone)
 * Viewport: 27.3242 x 23.4082
 */
public val SfSymbols.Dualtone.SFChartLineFlattrendXyaxis: ImageVector
    get() {
        if (_sFChartLineFlattrendXyaxis != null) {
            return _sFChartLineFlattrendXyaxis!!
        }
        _sFChartLineFlattrendXyaxis = sfIcon(
            name = "Dualtone.SFChartLineFlattrendXyaxis",
            viewportWidth = 27.3242f,
            viewportHeight = 23.4082f
        ) {
            addSfPath("M18.5742 11.8457L1.72852 11.8457L1.72852 10.0195L18.5742 10.0195Z", fillAlpha = 0.85f)
            addSfPath("M19.5703 6.44531L25.9375 10.3711C26.3867 10.6445 26.4062 11.2207 25.9473 11.4941L19.5898 15.4199C19.0918 15.7129 18.5742 15.5371 18.5742 14.9805L18.5742 6.89453C18.5742 6.33789 19.082 6.16211 19.5703 6.44531Z", fillAlpha = 0.85f)
            addSfPath("M0 22.5C0 23.0469 0.361328 23.4082 0.908203 23.4082L26.0938 23.4082C26.5625 23.4082 26.9629 23.0371 26.9629 22.5488C26.9629 22.0605 26.5625 21.6895 26.0938 21.6895L2.05078 21.6895C1.80664 21.6895 1.72852 21.6113 1.72852 21.3672L1.72852 0.986328C1.72852 0.527344 1.35742 0.117188 0.869141 0.117188C0.380859 0.117188 0 0.527344 0 0.986328Z", fillAlpha = 0.425f)
        }
        return _sFChartLineFlattrendXyaxis!!
    }

private var _sFChartLineFlattrendXyaxis: ImageVector? = null
