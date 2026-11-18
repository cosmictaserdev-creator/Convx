package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSpeakerSlashFill (monochrome)
 * Viewport: 25.5811 x 27.7979
 */
public val SfSymbols.Monochrome.SFSpeakerSlashFill: ImageVector
    get() {
        if (_sFSpeakerSlashFill != null) {
            return _sFSpeakerSlashFill!!
        }
        _sFSpeakerSlashFill = sfIcon(
            name = "Monochrome.SFSpeakerSlashFill",
            viewportWidth = 25.5811f,
            viewportHeight = 27.7979f
        ) {
            addSfPath("M18.5676 23.8831C18.4448 24.4646 17.9723 24.8706 17.3218 24.8706C16.7847 24.8706 16.3452 24.6363 15.8374 24.1577L10.3784 18.9917C10.2905 18.9038 10.1734 18.855 10.0269 18.855L6.3257 18.855C4.7632 18.855 3.855 17.9468 3.855 16.2964L3.855 11.5698C3.855 10.7945 4.05308 10.1832 4.42569 9.75423ZM18.6011 4.28469L18.6011 17.7153L9.88322 9.00149L10.0269 9.00149C10.1734 9.00149 10.2905 8.96242 10.3784 8.87453L15.8374 3.69875C16.3941 3.17141 16.7652 2.93703 17.3023 2.93703C18.0835 2.93703 18.6011 3.5425 18.6011 4.28469Z", fillAlpha = 0.85f)
            addSfPath("M22.8003 26.189C23.1226 26.521 23.6597 26.521 23.982 26.189C24.3042 25.857 24.3042 25.3394 23.982 25.0171L2.79055 3.83547C2.46828 3.5132 1.93117 3.50344 1.59914 3.83547C1.28664 4.14797 1.28664 4.69484 1.59914 5.00734Z", fillAlpha = 0.85f)
        }
        return _sFSpeakerSlashFill!!
    }

private var _sFSpeakerSlashFill: ImageVector? = null
