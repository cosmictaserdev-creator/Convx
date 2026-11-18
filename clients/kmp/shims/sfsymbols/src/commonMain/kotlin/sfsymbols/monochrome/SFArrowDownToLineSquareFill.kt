package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowDownToLineSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowDownToLineSquareFill: ImageVector
    get() {
        if (_sFArrowDownToLineSquareFill != null) {
            return _sFArrowDownToLineSquareFill!!
        }
        _sFArrowDownToLineSquareFill = sfIcon(
            name = "Monochrome.SFArrowDownToLineSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM6.65039 16.7871C6.21094 16.7871 5.9082 17.0996 5.9082 17.5195C5.9082 17.9785 6.21094 18.291 6.65039 18.291L16.3281 18.291C16.7578 18.291 17.0605 17.9785 17.0605 17.5195C17.0605 17.0996 16.7578 16.7871 16.3281 16.7871ZM10.7129 4.88281L10.7129 11.1035L10.8398 13.4863L8.4668 10.8887L7.5293 9.89258C7.39258 9.72656 7.20703 9.6582 6.97266 9.6582C6.5332 9.6582 6.24023 9.96094 6.24023 10.3906C6.24023 10.5859 6.33789 10.8203 6.48438 10.9668L10.9082 15.459C11.0742 15.6445 11.2695 15.7227 11.4746 15.7227C11.6797 15.7227 11.875 15.625 12.041 15.459L16.4648 10.9863C16.6504 10.8105 16.7285 10.6152 16.7285 10.3906C16.7285 9.98047 16.4355 9.6582 16.0059 9.6582C15.791 9.6582 15.5859 9.72656 15.4395 9.89258L14.4336 10.9082L12.1094 13.4766L12.2266 11.1035L12.2266 4.88281C12.2266 4.44336 11.9043 4.12109 11.4746 4.12109C11.0156 4.12109 10.7129 4.44336 10.7129 4.88281Z", fillAlpha = 0.85f)
        }
        return _sFArrowDownToLineSquareFill!!
    }

private var _sFArrowDownToLineSquareFill: ImageVector? = null
