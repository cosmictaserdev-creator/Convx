package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowUpToLineSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowUpToLineSquareFill: ImageVector
    get() {
        if (_sFArrowUpToLineSquareFill != null) {
            return _sFArrowUpToLineSquareFill!!
        }
        _sFArrowUpToLineSquareFill = sfIcon(
            name = "Monochrome.SFArrowUpToLineSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM10.9082 7.38281L6.48438 11.875C6.33789 12.0117 6.24023 12.2559 6.24023 12.4414C6.24023 12.8809 6.5332 13.1836 6.97266 13.1836C7.20703 13.1836 7.39258 13.1152 7.5293 12.9492L8.4668 11.9531L10.8398 9.35547L10.7129 11.7383L10.7129 17.959C10.7129 18.3984 11.0156 18.7207 11.4746 18.7207C11.9043 18.7207 12.2266 18.3984 12.2266 17.959L12.2266 11.7383L12.1094 9.36523L14.4336 11.9336L15.4395 12.9492C15.5859 13.1152 15.791 13.1836 16.0059 13.1836C16.4355 13.1836 16.7285 12.8613 16.7285 12.4414C16.7285 12.2266 16.6504 12.0312 16.4648 11.8555L12.041 7.38281C11.875 7.2168 11.6797 7.11914 11.4746 7.11914C11.2695 7.11914 11.0742 7.19727 10.9082 7.38281ZM6.65039 4.55078C6.21094 4.55078 5.9082 4.86328 5.9082 5.32227C5.9082 5.74219 6.21094 6.05469 6.65039 6.05469L16.3281 6.05469C16.7578 6.05469 17.0605 5.74219 17.0605 5.32227C17.0605 4.86328 16.7578 4.55078 16.3281 4.55078Z", fillAlpha = 0.85f)
        }
        return _sFArrowUpToLineSquareFill!!
    }

private var _sFArrowUpToLineSquareFill: ImageVector? = null
