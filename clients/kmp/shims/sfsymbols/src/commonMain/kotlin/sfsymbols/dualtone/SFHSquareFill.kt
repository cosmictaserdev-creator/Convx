package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFHSquareFill: ImageVector
    get() {
        if (_sFHSquareFill != null) {
            return _sFHSquareFill!!
        }
        _sFHSquareFill = sfIcon(
            name = "Dualtone.SFHSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M7.44141 17.3633C6.89453 17.3633 6.5625 16.9922 6.5625 16.377L6.5625 6.39648C6.5625 5.77148 6.89453 5.40039 7.44141 5.40039C8.01758 5.40039 8.34961 5.76172 8.34961 6.39648L8.34961 10.4785L14.6094 10.4785L14.6094 6.39648C14.6094 5.77148 14.9414 5.40039 15.4883 5.40039C16.0645 5.40039 16.3867 5.76172 16.3867 6.39648L16.3867 16.377C16.3867 17.002 16.0645 17.3633 15.4883 17.3633C14.9414 17.3633 14.6094 16.9922 14.6094 16.377L14.6094 11.8945L8.34961 11.8945L8.34961 16.377C8.34961 17.002 8.01758 17.3633 7.44141 17.3633Z", fillAlpha = 0.85f)
        }
        return _sFHSquareFill!!
    }

private var _sFHSquareFill: ImageVector? = null
