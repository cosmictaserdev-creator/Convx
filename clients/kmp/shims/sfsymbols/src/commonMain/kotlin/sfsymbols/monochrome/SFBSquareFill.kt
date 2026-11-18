package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFBSquareFill: ImageVector
    get() {
        if (_sFBSquareFill != null) {
            return _sFBSquareFill!!
        }
        _sFBSquareFill = sfIcon(
            name = "Monochrome.SFBSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM8.33008 5.50781C7.71484 5.50781 7.34375 5.87891 7.34375 6.50391L7.34375 16.2598C7.34375 16.8848 7.71484 17.2559 8.33008 17.2559L12.0996 17.2559C14.7168 17.2559 16.3965 15.9863 16.3965 13.9648C16.3965 12.4023 15.3516 11.2988 13.6719 11.084L13.6719 11.0156C14.9121 10.7129 15.6934 9.6875 15.6934 8.4082C15.6934 6.66016 14.2676 5.50781 12.1094 5.50781ZM14.5996 13.916C14.5996 15.2637 13.5742 15.9082 11.3867 15.9082L9.08203 15.9082L9.08203 11.8457L12.0215 11.8457C13.6426 11.8457 14.5996 12.6074 14.5996 13.916ZM13.9746 8.69141C13.9746 9.86328 12.9785 10.5762 11.2695 10.5762L9.08203 10.5762L9.08203 6.85547L11.7578 6.85547C13.1543 6.85547 13.9746 7.53906 13.9746 8.69141Z", fillAlpha = 0.85f)
        }
        return _sFBSquareFill!!
    }

private var _sFBSquareFill: ImageVector? = null
