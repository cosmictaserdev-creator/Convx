package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowCounterclockwise (dualtone)
 * Viewport: 22.4219 x 30.0098
 */
public val SfSymbols.Dualtone.SFArrowCounterclockwise: ImageVector
    get() {
        if (_sFArrowCounterclockwise != null) {
            return _sFArrowCounterclockwise!!
        }
        _sFArrowCounterclockwise = sfIcon(
            name = "Dualtone.SFArrowCounterclockwise",
            viewportWidth = 22.4219f,
            viewportHeight = 30.0098f
        ) {
            addSfPath("M11.0254 26.9434C17.1191 26.9434 22.0605 22.002 22.0605 15.9082C22.0605 9.81445 17.1191 4.88281 11.0254 4.88281C9.62891 4.88281 8.29102 5.15625 7.10938 5.625C6.79688 5.74219 6.40625 5.9668 6.42578 6.50391C6.45508 7.14844 7.08984 7.46094 7.63672 7.24609C8.69141 6.82617 9.83398 6.60156 11.0254 6.60156C16.1621 6.60156 20.332 10.7617 20.332 15.8984C20.332 21.0352 16.1621 25.2051 11.0254 25.2051C5.88867 25.2051 1.72852 21.0352 1.72852 15.8984C1.72852 15.4297 1.33789 15.0391 0.869141 15.0391C0.390625 15.0391 0 15.4297 0 15.8984C0 22.002 4.93164 26.9434 11.0254 26.9434ZM7.40234 6.34766L12.2363 1.51367C12.4121 1.34766 12.4805 1.11328 12.4805 0.888672C12.4805 0.400391 12.1191 0 11.6309 0C11.3574 0 11.1621 0.107422 11.0059 0.273438L5.70312 5.66406C5.49805 5.86914 5.40039 6.11328 5.40039 6.34766C5.40039 6.5918 5.48828 6.80664 5.70312 7.04102L11.0059 12.3926C11.1719 12.5488 11.3672 12.6465 11.6309 12.6465C12.1191 12.6465 12.4805 12.2656 12.4805 11.7773C12.4805 11.543 12.4121 11.3281 12.2266 11.1621Z", fillAlpha = 0.85f)
        }
        return _sFArrowCounterclockwise!!
    }

private var _sFArrowCounterclockwise: ImageVector? = null
