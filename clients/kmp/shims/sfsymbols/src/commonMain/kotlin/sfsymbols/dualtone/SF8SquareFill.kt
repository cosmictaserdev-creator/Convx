package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF8SquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SF8SquareFill: ImageVector
    get() {
        if (_sF8SquareFill != null) {
            return _sF8SquareFill!!
        }
        _sF8SquareFill = sfIcon(
            name = "Dualtone.SF8SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.4648 17.5586C8.80859 17.5586 7.01172 16.1621 7.01172 14.1211C7.01172 12.6855 8.05664 11.4844 9.58008 11.1816L9.58008 11.1035C8.30078 10.7227 7.50977 9.7168 7.50977 8.48633C7.50977 6.66016 9.15039 5.39062 11.4844 5.39062C13.8184 5.39062 15.459 6.66016 15.459 8.48633C15.459 9.7168 14.6484 10.7422 13.3887 11.1035L13.3887 11.1816C14.9023 11.4844 15.957 12.6758 15.957 14.1113C15.957 16.1523 14.1211 17.5586 11.4648 17.5586ZM11.4941 16.2988C13.0859 16.2988 14.2676 15.3516 14.2676 14.0234C14.2676 12.7344 13.1152 11.7969 11.4941 11.7969C9.90234 11.7969 8.7207 12.7441 8.7207 14.0234C8.7207 15.3516 9.89258 16.2988 11.4941 16.2988ZM11.4844 10.5859C12.8418 10.5859 13.8086 9.76562 13.8086 8.61328C13.8086 7.4707 12.8516 6.66016 11.4844 6.66016C10.1172 6.66016 9.16016 7.4707 9.16016 8.61328C9.16016 9.76562 10.127 10.5859 11.4844 10.5859Z", fillAlpha = 0.85f)
        }
        return _sF8SquareFill!!
    }

private var _sF8SquareFill: ImageVector? = null
