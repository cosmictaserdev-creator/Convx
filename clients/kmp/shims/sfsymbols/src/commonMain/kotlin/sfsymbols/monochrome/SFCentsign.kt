package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCentsign (monochrome)
 * Viewport: 15.2637 x 23.3887
 */
public val SfSymbols.Monochrome.SFCentsign: ImageVector
    get() {
        if (_sFCentsign != null) {
            return _sFCentsign!!
        }
        _sFCentsign = sfIcon(
            name = "Monochrome.SFCentsign",
            viewportWidth = 15.2637f,
            viewportHeight = 23.3887f
        ) {
            addSfPath("M7.91992 20.4004C9.9707 20.4004 12.002 19.7559 13.4375 18.3398C14.1504 17.6172 14.9023 16.3867 14.9023 15.498C14.9023 14.9512 14.5312 14.5898 13.9551 14.5898C13.5547 14.5898 13.2129 14.8438 13.0273 15.2539C12.2168 17.373 10.4004 18.6035 7.92969 18.6035C4.01367 18.6035 2.08008 15.625 2.08008 11.6895C2.08008 7.75391 4.04297 4.77539 7.91016 4.77539C10.4004 4.77539 12.1973 5.99609 13.0078 8.11523C13.1836 8.53516 13.5449 8.79883 13.9746 8.79883C14.502 8.79883 14.8828 8.4375 14.8828 7.87109C14.8828 7.01172 14.1602 5.78125 13.418 5.03906C12.002 3.62305 9.93164 2.97852 7.90039 2.97852C2.91992 2.97852 0 6.65039 0 11.6895C0 16.7383 2.86133 20.4004 7.91992 20.4004ZM7.82227 23.3789C8.22266 23.3789 8.54492 23.0566 8.54492 22.666L8.54492 0.712891C8.54492 0.322266 8.22266 0 7.82227 0C7.43164 0 7.10938 0.322266 7.10938 0.712891L7.10938 22.666C7.10938 23.0566 7.43164 23.3789 7.82227 23.3789Z", fillAlpha = 0.85f)
        }
        return _sFCentsign!!
    }

private var _sFCentsign: ImageVector? = null
