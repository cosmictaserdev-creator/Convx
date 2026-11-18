package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFNotequalSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFNotequalSquareFill: ImageVector
    get() {
        if (_sFNotequalSquareFill != null) {
            return _sFNotequalSquareFill!!
        }
        _sFNotequalSquareFill = sfIcon(
            name = "Dualtone.SFNotequalSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M16.4551 12.998C17.0508 12.998 17.4316 13.291 17.4316 13.8574C17.4316 14.4141 17.0312 14.707 16.4551 14.707L10.1562 14.707L8.55469 16.8848C8.23242 17.3438 7.75391 17.4902 7.30469 17.1582C6.85547 16.8359 6.82617 16.3477 7.1875 15.8789L8.02734 14.707L6.49414 14.707C5.92773 14.707 5.52734 14.4141 5.52734 13.8574C5.52734 13.291 5.9082 12.998 6.49414 12.998L9.29688 12.998L11.5039 9.98047L6.49414 9.98047C5.92773 9.98047 5.52734 9.69727 5.52734 9.14062C5.52734 8.56445 5.9082 8.27148 6.49414 8.27148L12.7539 8.27148L14.4043 6.04492C14.7461 5.55664 15.2051 5.44922 15.6641 5.78125C16.123 6.10352 16.1133 6.5918 15.7715 7.05078L14.873 8.27148L16.4551 8.27148C17.0508 8.27148 17.4316 8.56445 17.4316 9.14062C17.4316 9.69727 17.0312 9.98047 16.4551 9.98047L13.623 9.98047L11.4062 12.998Z", fillAlpha = 0.85f)
        }
        return _sFNotequalSquareFill!!
    }

private var _sFNotequalSquareFill: ImageVector? = null
