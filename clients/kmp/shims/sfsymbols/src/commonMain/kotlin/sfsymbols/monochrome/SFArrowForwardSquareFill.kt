package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowForwardSquareFill (monochrome)
 * Viewport: 23.3203 x 22.959
 */
public val SfSymbols.Monochrome.SFArrowForwardSquareFill: ImageVector
    get() {
        if (_sFArrowForwardSquareFill != null) {
            return _sFArrowForwardSquareFill!!
        }
        _sFArrowForwardSquareFill = sfIcon(
            name = "Monochrome.SFArrowForwardSquareFill",
            viewportWidth = 23.3203f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M22.959 3.76953L22.959 19.1992C22.959 21.6797 21.6797 22.959 19.1504 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L19.1504 0C21.6797 0 22.959 1.2793 22.959 3.76953ZM12.1582 7.08008C12.1582 7.30469 12.2461 7.53906 12.4316 7.68555L14.6484 9.81445L15.5138 10.636L14.0039 10.5859L5.91797 10.5762C5.44922 10.5762 5.05859 10.9766 5.05859 11.4648C5.05859 11.9434 5.44922 12.3438 5.91797 12.3438L14.0039 12.3438L15.5138 12.2937L14.6484 13.1152L12.4316 15.2441C12.2461 15.3906 12.1582 15.6348 12.1582 15.8594C12.1582 16.3184 12.4902 16.6504 12.9395 16.6504C13.1836 16.6504 13.3594 16.5625 13.5254 16.3867L17.6953 12.0996C17.9102 11.875 17.9883 11.6895 17.9883 11.4648C17.9883 11.2305 17.9102 11.0449 17.6953 10.8203L13.5254 6.54297C13.3594 6.36719 13.1836 6.2793 12.9395 6.2793C12.4902 6.2793 12.1582 6.63086 12.1582 7.08008Z", fillAlpha = 0.85f)
        }
        return _sFArrowForwardSquareFill!!
    }

private var _sFArrowForwardSquareFill: ImageVector? = null
