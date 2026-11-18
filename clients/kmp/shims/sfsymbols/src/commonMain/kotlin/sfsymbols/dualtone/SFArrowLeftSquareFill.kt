package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowLeftSquareFill (dualtone)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Dualtone.SFArrowLeftSquareFill: ImageVector
    get() {
        if (_sFArrowLeftSquareFill != null) {
            return _sFArrowLeftSquareFill!!
        }
        _sFArrowLeftSquareFill = sfIcon(
            name = "Dualtone.SFArrowLeftSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M3.79883 22.959L19.1504 22.959C21.6797 22.959 22.959 21.6797 22.959 19.1992L22.959 3.76953C22.959 1.2793 21.6797 0 19.1504 0L3.79883 0C1.2793 0 0 1.26953 0 3.76953L0 19.1992C0 21.6992 1.2793 22.959 3.79883 22.959Z", fillAlpha = 0.2125f)
            addSfPath("M17.1191 12.3438C17.5977 12.3438 17.9883 11.9434 17.9883 11.4648C17.9883 10.9766 17.5977 10.5762 17.1191 10.5762L9.0332 10.5859L6.98242 10.6543C6.54297 10.6738 6.18164 11.0254 6.18164 11.4648C6.18164 11.8945 6.54297 12.2559 6.98242 12.2754L9.0332 12.3438ZM10.0977 16.6504C10.5469 16.6504 10.8789 16.3184 10.8789 15.8594C10.8789 15.6348 10.791 15.3906 10.6152 15.2441L8.38867 13.1152L6.65039 11.4648L6.65039 11.4648L8.38867 9.81445L10.6152 7.68555C10.791 7.53906 10.8789 7.30469 10.8789 7.08008C10.8789 6.63086 10.5469 6.2793 10.0977 6.2793C9.86328 6.2793 9.67773 6.36719 9.52148 6.54297L5.35156 10.8203C5.12695 11.0449 5.05859 11.2305 5.05859 11.4648C5.05859 11.6895 5.12695 11.875 5.35156 12.0996L9.52148 16.3867C9.67773 16.5625 9.86328 16.6504 10.0977 16.6504Z", fillAlpha = 0.85f)
        }
        return _sFArrowLeftSquareFill!!
    }

private var _sFArrowLeftSquareFill: ImageVector? = null
