package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFZSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFZSquareFill: ImageVector
    get() {
        if (_sFZSquareFill != null) {
            return _sFZSquareFill!!
        }
        _sFZSquareFill = sfIcon(
            name = "Dualtone.SFZSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M7.7832 17.2266C7.27539 17.2266 6.93359 16.8945 6.93359 16.4355C6.93359 16.1621 7.00195 15.9961 7.24609 15.6348L13.4277 7.11914L13.4277 7.02148L7.66602 7.02148C7.22656 7.02148 6.91406 6.74805 6.91406 6.2793C6.91406 5.83984 7.22656 5.54688 7.66602 5.54688L14.8242 5.54688C15.3418 5.54688 15.7031 5.88867 15.7031 6.36719C15.7031 6.65039 15.6348 6.80664 15.4102 7.12891L9.24805 15.6445L9.24805 15.752L15.332 15.752C15.7715 15.752 16.084 16.0254 16.084 16.4844C16.084 16.9336 15.7715 17.2266 15.332 17.2266Z", fillAlpha = 0.85f)
        }
        return _sFZSquareFill!!
    }

private var _sFZSquareFill: ImageVector? = null
