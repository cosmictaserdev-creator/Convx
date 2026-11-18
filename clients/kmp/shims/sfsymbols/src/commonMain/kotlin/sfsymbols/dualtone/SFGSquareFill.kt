package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFGSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFGSquareFill: ImageVector
    get() {
        if (_sFGSquareFill != null) {
            return _sFGSquareFill!!
        }
        _sFGSquareFill = sfIcon(
            name = "Dualtone.SFGSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M11.5723 17.6074C8.21289 17.6074 6.06445 15.1758 6.06445 11.3867C6.06445 7.60742 8.21289 5.16602 11.5234 5.16602C14.043 5.16602 15.7129 6.52344 16.2598 8.10547C16.3184 8.30078 16.3574 8.45703 16.3574 8.66211C16.3574 9.15039 16.0547 9.45312 15.5762 9.45312C15.2148 9.45312 14.9902 9.27734 14.8242 8.86719C14.3457 7.48047 13.1641 6.66992 11.5625 6.66992C9.35547 6.66992 7.88086 8.57422 7.88086 11.3867C7.88086 14.209 9.36523 16.1035 11.5918 16.1035C13.6426 16.1035 14.7461 14.9609 14.7461 13.2129L14.7461 12.4121L12.0508 12.4121C11.6406 12.4121 11.377 12.0996 11.377 11.748C11.377 11.3574 11.6406 11.084 12.0508 11.084L15.5957 11.084C16.1816 11.084 16.5234 11.4453 16.5234 12.0508L16.5234 12.998C16.5234 15.7324 14.668 17.6074 11.5723 17.6074Z", fillAlpha = 0.85f)
        }
        return _sFGSquareFill!!
    }

private var _sFGSquareFill: ImageVector? = null
