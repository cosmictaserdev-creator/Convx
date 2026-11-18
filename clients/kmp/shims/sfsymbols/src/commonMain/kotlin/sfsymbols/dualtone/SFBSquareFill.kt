package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFBSquareFill: ImageVector
    get() {
        if (_sFBSquareFill != null) {
            return _sFBSquareFill!!
        }
        _sFBSquareFill = sfIcon(
            name = "Dualtone.SFBSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M8.33008 17.2559C7.71484 17.2559 7.34375 16.8848 7.34375 16.2598L7.34375 6.50391C7.34375 5.87891 7.71484 5.50781 8.33008 5.50781L12.1094 5.50781C14.2676 5.50781 15.6934 6.66016 15.6934 8.4082C15.6934 9.6875 14.9121 10.7129 13.6719 11.0156L13.6719 11.084C15.3516 11.2988 16.3965 12.4023 16.3965 13.9648C16.3965 15.9863 14.7168 17.2559 12.0996 17.2559ZM9.08203 10.5762L11.2695 10.5762C12.9785 10.5762 13.9746 9.86328 13.9746 8.69141C13.9746 7.53906 13.1543 6.85547 11.7578 6.85547L9.08203 6.85547ZM9.08203 15.9082L11.3867 15.9082C13.5742 15.9082 14.5996 15.2637 14.5996 13.916C14.5996 12.6074 13.6426 11.8457 12.0215 11.8457L9.08203 11.8457Z", fillAlpha = 0.85f)
        }
        return _sFBSquareFill!!
    }

private var _sFBSquareFill: ImageVector? = null
