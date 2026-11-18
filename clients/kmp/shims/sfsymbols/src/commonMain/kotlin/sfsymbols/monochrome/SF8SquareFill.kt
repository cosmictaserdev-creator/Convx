package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SF8SquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SF8SquareFill: ImageVector
    get() {
        if (_sF8SquareFill != null) {
            return _sF8SquareFill!!
        }
        _sF8SquareFill = sfIcon(
            name = "Monochrome.SF8SquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM7.50977 8.48633C7.50977 9.7168 8.30078 10.7227 9.58008 11.1035L9.58008 11.1816C8.05664 11.4844 7.01172 12.6855 7.01172 14.1211C7.01172 16.1621 8.80859 17.5586 11.4648 17.5586C14.1211 17.5586 15.957 16.1523 15.957 14.1113C15.957 12.6758 14.9023 11.4844 13.3887 11.1816L13.3887 11.1035C14.6484 10.7422 15.459 9.7168 15.459 8.48633C15.459 6.66016 13.8184 5.39062 11.4844 5.39062C9.15039 5.39062 7.50977 6.66016 7.50977 8.48633ZM14.2676 14.0234C14.2676 15.3516 13.0859 16.2988 11.4941 16.2988C9.89258 16.2988 8.7207 15.3516 8.7207 14.0234C8.7207 12.7441 9.90234 11.7969 11.4941 11.7969C13.1152 11.7969 14.2676 12.7344 14.2676 14.0234ZM13.8086 8.61328C13.8086 9.76562 12.8418 10.5859 11.4844 10.5859C10.127 10.5859 9.16016 9.76562 9.16016 8.61328C9.16016 7.4707 10.1172 6.66016 11.4844 6.66016C12.8516 6.66016 13.8086 7.4707 13.8086 8.61328Z", fillAlpha = 0.85f)
        }
        return _sF8SquareFill!!
    }

private var _sF8SquareFill: ImageVector? = null
