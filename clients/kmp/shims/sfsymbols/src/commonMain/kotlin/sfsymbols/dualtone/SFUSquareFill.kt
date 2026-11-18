package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFUSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFUSquareFill: ImageVector
    get() {
        if (_sFUSquareFill != null) {
            return _sFUSquareFill!!
        }
        _sFUSquareFill = sfIcon(
            name = "Dualtone.SFUSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.4844 17.4902C8.57422 17.4902 6.58203 15.791 6.58203 13.1348L6.58203 6.39648C6.58203 5.77148 6.91406 5.40039 7.4707 5.40039C8.03711 5.40039 8.36914 5.77148 8.36914 6.39648L8.36914 12.9688C8.36914 14.8242 9.62891 15.9961 11.4844 15.9961C13.3398 15.9961 14.5996 14.8242 14.5996 12.9688L14.5996 6.39648C14.5996 5.77148 14.9316 5.40039 15.4883 5.40039C16.0547 5.40039 16.3867 5.77148 16.3867 6.39648L16.3867 13.1348C16.3867 15.791 14.3945 17.4902 11.4844 17.4902Z", fillAlpha = 0.85f)
        }
        return _sFUSquareFill!!
    }

private var _sFUSquareFill: ImageVector? = null
