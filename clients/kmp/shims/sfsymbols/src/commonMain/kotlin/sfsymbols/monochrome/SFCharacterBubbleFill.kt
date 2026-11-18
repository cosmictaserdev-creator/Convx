package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCharacterBubbleFill (monochrome)
 * Viewport: 27.8711 x 27.3926
 */
public val SfSymbols.Monochrome.SFCharacterBubbleFill: ImageVector
    get() {
        if (_sFCharacterBubbleFill != null) {
            return _sFCharacterBubbleFill!!
        }
        _sFCharacterBubbleFill = sfIcon(
            name = "Monochrome.SFCharacterBubbleFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.3926f
        ) {
            addSfPath("M27.5098 7.14844L27.5098 17.1094C27.5098 20.5859 25.5371 22.5977 22.002 22.5977L13.0273 22.5977L8.4668 26.709C7.96875 27.168 7.64648 27.3926 7.2168 27.3926C6.5918 27.3926 6.23047 26.9434 6.23047 26.2598L6.23047 22.5977L5.50781 22.5977C1.96289 22.5977 0 20.6055 0 17.1094L0 7.14844C0 3.65234 1.96289 1.64062 5.50781 1.64062L22.002 1.64062C25.5371 1.64062 27.5098 3.66211 27.5098 7.14844ZM12.5098 6.82617L8.79883 16.7676C8.71094 17.0117 8.68164 17.168 8.68164 17.334C8.68164 17.793 9.00391 18.1055 9.52148 18.1055C9.92188 18.1055 10.2051 17.9199 10.3809 17.3926L11.3965 14.4922L16.1035 14.4922L17.1191 17.3926C17.3047 17.9199 17.5781 18.1055 17.998 18.1055C18.4863 18.1055 18.8184 17.793 18.8184 17.3438C18.8184 17.168 18.7988 17.0117 18.7012 16.7676L14.9902 6.82617C14.7656 6.24023 14.3457 5.94727 13.7402 5.94727C13.1445 5.94727 12.7344 6.24023 12.5098 6.82617ZM15.6445 13.125L11.8555 13.125L13.6914 7.91992L13.8086 7.91992Z", fillAlpha = 0.85f)
        }
        return _sFCharacterBubbleFill!!
    }

private var _sFCharacterBubbleFill: ImageVector? = null
