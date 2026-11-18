package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFNotequalSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFNotequalSquareFill: ImageVector
    get() {
        if (_sFNotequalSquareFill != null) {
            return _sFNotequalSquareFill!!
        }
        _sFNotequalSquareFill = sfIcon(
            name = "Monochrome.SFNotequalSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM14.4043 6.04492L12.7539 8.27148L6.49414 8.27148C5.9082 8.27148 5.52734 8.56445 5.52734 9.14062C5.52734 9.69727 5.92773 9.98047 6.49414 9.98047L11.5039 9.98047L9.29688 12.998L6.49414 12.998C5.9082 12.998 5.52734 13.291 5.52734 13.8574C5.52734 14.4141 5.92773 14.707 6.49414 14.707L8.02734 14.707L7.1875 15.8789C6.82617 16.3477 6.85547 16.8359 7.30469 17.1582C7.75391 17.4902 8.23242 17.3438 8.55469 16.8848L10.1562 14.707L16.4551 14.707C17.0312 14.707 17.4316 14.4141 17.4316 13.8574C17.4316 13.291 17.0508 12.998 16.4551 12.998L11.4062 12.998L13.623 9.98047L16.4551 9.98047C17.0312 9.98047 17.4316 9.69727 17.4316 9.14062C17.4316 8.56445 17.0508 8.27148 16.4551 8.27148L14.873 8.27148L15.7715 7.05078C16.1133 6.5918 16.123 6.10352 15.6641 5.78125C15.2051 5.44922 14.7461 5.55664 14.4043 6.04492Z", fillAlpha = 0.85f)
        }
        return _sFNotequalSquareFill!!
    }

private var _sFNotequalSquareFill: ImageVector? = null
