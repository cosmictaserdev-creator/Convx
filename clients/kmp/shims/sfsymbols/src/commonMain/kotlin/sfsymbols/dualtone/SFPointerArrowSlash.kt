package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPointerArrowSlash (dualtone)
 * Viewport: 25.6372 x 25.044
 */
public val SfSymbols.Dualtone.SFPointerArrowSlash: ImageVector
    get() {
        if (_sFPointerArrowSlash != null) {
            return _sFPointerArrowSlash!!
        }
        _sFPointerArrowSlash = sfIcon(
            name = "Dualtone.SFPointerArrowSlash",
            viewportWidth = 25.6372f,
            viewportHeight = 25.044f
        ) {
            addSfPath("M17.696 21.8491L17.9993 22.5659C18.3606 23.4351 17.9504 24.4312 17.0715 24.773C16.1926 25.1343 15.2063 24.7144 14.8352 23.855L11.5308 15.6993ZM11.0353 15.205L7.95044 18.562C7.4524 19.148 6.57349 18.8159 6.57349 18.0445L6.61447 10.7952ZM7.96021 0.261251L20.0403 12.8198C20.5774 13.3765 20.2063 14.1968 19.4544 14.148L16.0054 13.9656L6.64933 4.62819L6.67115 0.769064C6.67115 0.0464073 7.46216-0.246561 7.96021 0.261251Z", fillAlpha = 0.425f)
            addSfPath("M1.60279 3.87453L21.2122 23.4351C21.5442 23.7573 22.0715 23.7573 22.3938 23.4351C22.7161 23.103 22.7161 22.5855 22.3938 22.2534L2.79419 2.69289C2.47193 2.38039 1.94459 2.36086 1.60279 2.69289C1.29029 3.01516 1.29029 3.56203 1.60279 3.87453Z", fillAlpha = 0.85f)
        }
        return _sFPointerArrowSlash!!
    }

private var _sFPointerArrowSlash: ImageVector? = null
