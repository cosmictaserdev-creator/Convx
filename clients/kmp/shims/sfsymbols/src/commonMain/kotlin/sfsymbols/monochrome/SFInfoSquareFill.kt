package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFInfoSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFInfoSquareFill: ImageVector
    get() {
        if (_sFInfoSquareFill != null) {
            return _sFInfoSquareFill!!
        }
        _sFInfoSquareFill = sfIcon(
            name = "Monochrome.SFInfoSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM9.375 9.0918C8.92578 9.0918 8.56445 9.44336 8.56445 9.88281C8.56445 10.3418 8.92578 10.6836 9.375 10.6836L10.9473 10.6836L10.9473 17.0605L9.19922 17.0605C8.73047 17.0605 8.37891 17.4121 8.37891 17.8516C8.37891 18.3105 8.73047 18.6523 9.19922 18.6523L14.4629 18.6523C14.9316 18.6523 15.293 18.3105 15.293 17.8516C15.293 17.4121 14.9316 17.0605 14.4629 17.0605L12.7148 17.0605L12.7148 10.0879C12.7148 9.50195 12.4121 9.0918 11.8457 9.0918ZM9.87305 5.29297C9.87305 6.14258 10.5469 6.82617 11.3965 6.82617C12.2461 6.82617 12.9102 6.14258 12.9102 5.29297C12.9102 4.44336 12.2461 3.75977 11.3965 3.75977C10.5469 3.75977 9.87305 4.44336 9.87305 5.29297Z", fillAlpha = 0.85f)
        }
        return _sFInfoSquareFill!!
    }

private var _sFInfoSquareFill: ImageVector? = null
