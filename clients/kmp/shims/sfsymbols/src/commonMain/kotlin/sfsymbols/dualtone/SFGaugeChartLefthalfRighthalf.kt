package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGaugeChartLefthalfRighthalf (dualtone)
 * Viewport: 30.3809 x 24.9191
 */
public val SfSymbols.Dualtone.SFGaugeChartLefthalfRighthalf: ImageVector
    get() {
        if (_sFGaugeChartLefthalfRighthalf != null) {
            return _sFGaugeChartLefthalfRighthalf!!
        }
        _sFGaugeChartLefthalfRighthalf = sfIcon(
            name = "Dualtone.SFGaugeChartLefthalfRighthalf",
            viewportWidth = 30.3809f,
            viewportHeight = 24.9191f
        ) {
            addSfPath("M30.0195 14.9547C30.0195 7.09334 23.9746 0.648023 16.2988 0.00349172C15.8984-0.0355708 15.5859 0.257398 15.5859 0.667554L15.5859 4.66169C15.5859 4.99373 15.8301 5.2574 16.1719 5.28669C20.9863 5.87263 24.7266 9.97419 24.7266 14.9547C24.7266 17.2203 23.9551 19.3004 22.5879 21.0191C22.4023 21.2535 22.4121 21.5465 22.6172 21.7515L25.5176 24.6519C25.8105 24.9351 26.2305 24.9351 26.4844 24.6226C28.6914 22.0054 30.0195 18.6265 30.0195 14.9547Z", fillAlpha = 0.85f)
            addSfPath("M0 14.9547C0 18.6363 1.32812 22.025 3.54492 24.6422C3.78906 24.9351 4.19922 24.9351 4.49219 24.6519L7.39258 21.7515C7.59766 21.5465 7.60742 21.2535 7.42188 21.0191C6.05469 19.3004 5.2832 17.2203 5.2832 14.9547C5.2832 9.97419 9.0332 5.87263 13.8379 5.28669C14.1895 5.2574 14.4238 4.99373 14.4238 4.66169L14.4238 0.667554C14.4238 0.257398 14.1211-0.0355708 13.7207 0.00349172C6.03516 0.648023 0 7.09334 0 14.9547Z", fillAlpha = 0.85f)
        }
        return _sFGaugeChartLefthalfRighthalf!!
    }

private var _sFGaugeChartLefthalfRighthalf: ImageVector? = null
