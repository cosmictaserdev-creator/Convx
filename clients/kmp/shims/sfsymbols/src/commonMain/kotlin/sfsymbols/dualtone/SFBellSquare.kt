package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBellSquare (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFBellSquare: ImageVector
    get() {
        if (_sFBellSquare != null) {
            return _sFBellSquare!!
        }
        _sFBellSquare = sfIcon(
            name = "Dualtone.SFBellSquare",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959ZM3.83789 21.2305C2.4707 21.2305 1.72852 20.5078 1.72852 19.1016L1.72852 3.85742C1.72852 2.46094 2.4707 1.72852 3.83789 1.72852L19.1211 1.72852C20.459 1.72852 21.2305 2.46094 21.2305 3.85742L21.2305 19.1016C21.2305 20.5078 20.459 21.2305 19.1211 21.2305Z", fillAlpha = 0.425f)
            addSfPath("M5.83008 16.0254L17.1582 16.0254C17.7051 16.0254 18.0371 15.7324 18.0371 15.293C18.0371 14.5898 17.3535 13.9844 16.748 13.3594C16.2012 12.7734 16.1523 11.6016 16.0645 10.6055C15.9668 8.13477 15.2441 6.39648 13.4473 5.78125C13.1934 4.85352 12.4805 4.15039 11.4941 4.15039C10.5078 4.15039 9.79492 4.85352 9.54102 5.78125C7.74414 6.39648 7.03125 8.13477 6.93359 10.6055C6.83594 11.6016 6.76758 12.7734 6.24023 13.3594C5.6543 13.9941 4.95117 14.5898 4.95117 15.293C4.95117 15.7324 5.2832 16.0254 5.83008 16.0254ZM11.4941 18.8574C12.6465 18.8574 13.4766 18.0566 13.5742 17.0312L9.41406 17.0312C9.52148 18.0566 10.3418 18.8574 11.4941 18.8574Z", fillAlpha = 0.85f)
        }
        return _sFBellSquare!!
    }

private var _sFBellSquare: ImageVector? = null
