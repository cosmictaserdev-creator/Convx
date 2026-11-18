package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowClockwiseSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowClockwiseSquareFill: ImageVector
    get() {
        if (_sFArrowClockwiseSquareFill != null) {
            return _sFArrowClockwiseSquareFill!!
        }
        _sFArrowClockwiseSquareFill = sfIcon(
            name = "Monochrome.SFArrowClockwiseSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM10.4785 4.80469C10.4785 5.01953 10.5566 5.20508 10.7129 5.35156L11.8383 6.4613C11.6507 6.45635 11.4686 6.45508 11.2891 6.45508C8.35938 6.45508 5.80078 8.92578 5.80078 12.0605C5.80078 15.2539 8.33008 17.8027 11.4844 17.8027C14.6484 17.8027 17.168 15.2539 17.168 12.0996C17.168 11.6797 16.8066 11.3281 16.3965 11.3281C15.9668 11.3281 15.6152 11.6797 15.6152 12.0996C15.6152 14.4043 13.7793 16.25 11.4844 16.25C9.17969 16.25 7.35352 14.4043 7.35352 12.0996C7.35352 9.79492 9.17969 7.96875 11.4844 7.96875C11.7462 7.96875 12.0003 7.97654 12.2341 7.98953L10.7129 9.48242C10.5566 9.63867 10.4785 9.81445 10.4785 10.0293C10.4785 10.4297 10.791 10.7422 11.2109 10.7422C11.3867 10.7422 11.5918 10.6543 11.6992 10.5371L14.2676 7.94922C14.4141 7.8125 14.4824 7.61719 14.4824 7.42188C14.4824 7.22656 14.4141 7.02148 14.2676 6.89453L11.6992 4.30664C11.6016 4.19922 11.3867 4.0918 11.2109 4.0918C10.791 4.0918 10.4785 4.41406 10.4785 4.80469Z", fillAlpha = 0.85f)
        }
        return _sFArrowClockwiseSquareFill!!
    }

private var _sFArrowClockwiseSquareFill: ImageVector? = null
