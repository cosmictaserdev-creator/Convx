package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFUSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFUSquareFill: ImageVector
    get() {
        if (_sFUSquareFill != null) {
            return _sFUSquareFill!!
        }
        _sFUSquareFill = sfIcon(
            name = "Monochrome.SFUSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM14.5996 6.39648L14.5996 12.9688C14.5996 14.8242 13.3398 15.9961 11.4844 15.9961C9.62891 15.9961 8.36914 14.8242 8.36914 12.9688L8.36914 6.39648C8.36914 5.77148 8.03711 5.40039 7.4707 5.40039C6.91406 5.40039 6.58203 5.77148 6.58203 6.39648L6.58203 13.1348C6.58203 15.791 8.57422 17.4902 11.4844 17.4902C14.3945 17.4902 16.3867 15.791 16.3867 13.1348L16.3867 6.39648C16.3867 5.77148 16.0547 5.40039 15.4883 5.40039C14.9316 5.40039 14.5996 5.77148 14.5996 6.39648Z", fillAlpha = 0.85f)
        }
        return _sFUSquareFill!!
    }

private var _sFUSquareFill: ImageVector? = null
