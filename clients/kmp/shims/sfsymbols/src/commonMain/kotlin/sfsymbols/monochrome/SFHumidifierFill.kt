package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHumidifierFill (monochrome)
 * Viewport: 22.666 x 23.8379
 */
public val SfSymbols.Monochrome.SFHumidifierFill: ImageVector
    get() {
        if (_sFHumidifierFill != null) {
            return _sFHumidifierFill!!
        }
        _sFHumidifierFill = sfIcon(
            name = "Monochrome.SFHumidifierFill",
            viewportWidth = 22.666f,
            viewportHeight = 23.8379f
        ) {
            addSfPath("M4.08203 5.84961L18.2129 5.84961C17.2852 4.39453 16.25 2.90039 15.1367 1.39648C14.4434 0.478516 13.9746 0 12.7832 0L9.52148 0C8.33008 0 7.85156 0.478516 7.16797 1.39648C6.05469 2.90039 5.00977 4.39453 4.08203 5.84961ZM11.1523 12.7051C12.6074 12.7051 13.8086 13.6523 14.1895 14.9902L22.2656 14.9902C21.9922 12.9297 20.9277 10.3809 19.3359 7.63672L2.95898 7.63672C1.37695 10.3809 0.302734 12.9297 0.0390625 14.9902L8.11523 14.9902C8.49609 13.6523 9.69727 12.7051 11.1523 12.7051ZM11.1523 17.4512C12.041 17.4512 12.7246 16.7676 12.7246 15.8691C12.7246 14.9902 12.041 14.2871 11.1523 14.2871C10.2637 14.2871 9.57031 14.9902 9.57031 15.8691C9.57031 16.7676 10.2637 17.4512 11.1523 17.4512ZM8.21289 23.8184L14.0918 23.8184C19.248 23.8184 22.0117 21.1621 22.3047 16.7773L14.1895 16.7773C13.8184 18.0859 12.6172 19.0332 11.1523 19.0332C9.69727 19.0332 8.48633 18.0859 8.11523 16.7773L0 16.7773C0.283203 21.1621 3.05664 23.8184 8.21289 23.8184Z", fillAlpha = 0.85f)
        }
        return _sFHumidifierFill!!
    }

private var _sFHumidifierFill: ImageVector? = null
