package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFZSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFZSquareFill: ImageVector
    get() {
        if (_sFZSquareFill != null) {
            return _sFZSquareFill!!
        }
        _sFZSquareFill = sfIcon(
            name = "Monochrome.SFZSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM7.66602 5.54688C7.22656 5.54688 6.91406 5.83984 6.91406 6.2793C6.91406 6.74805 7.22656 7.02148 7.66602 7.02148L13.4277 7.02148L13.4277 7.11914L7.24609 15.6348C7.00195 15.9961 6.93359 16.1621 6.93359 16.4355C6.93359 16.8945 7.27539 17.2266 7.7832 17.2266L15.332 17.2266C15.7715 17.2266 16.084 16.9336 16.084 16.4844C16.084 16.0254 15.7715 15.752 15.332 15.752L9.24805 15.752L9.24805 15.6445L15.4102 7.12891C15.6348 6.80664 15.7031 6.65039 15.7031 6.36719C15.7031 5.88867 15.3418 5.54688 14.8242 5.54688Z", fillAlpha = 0.85f)
        }
        return _sFZSquareFill!!
    }

private var _sFZSquareFill: ImageVector? = null
