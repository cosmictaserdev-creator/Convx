package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCharacterBubbleFill (dualtone)
 * Viewport: 27.8711 x 27.3926
 */
public val SfSymbols.Dualtone.SFCharacterBubbleFill: ImageVector
    get() {
        if (_sFCharacterBubbleFill != null) {
            return _sFCharacterBubbleFill!!
        }
        _sFCharacterBubbleFill = sfIcon(
            name = "Dualtone.SFCharacterBubbleFill",
            viewportWidth = 27.8711f,
            viewportHeight = 27.3926f
        ) {
            addSfPath("M7.2168 27.3926C7.64648 27.3926 7.96875 27.168 8.4668 26.709L13.0273 22.5977L22.002 22.5977C25.5371 22.5977 27.5098 20.5859 27.5098 17.1094L27.5098 7.14844C27.5098 3.66211 25.5371 1.64062 22.002 1.64062L5.50781 1.64062C1.96289 1.64062 0 3.65234 0 7.14844L0 17.1094C0 20.6055 1.96289 22.5977 5.50781 22.5977L6.23047 22.5977L6.23047 26.2598C6.23047 26.9434 6.5918 27.3926 7.2168 27.3926Z", fillAlpha = 0.2125f)
            addSfPath("M9.52148 18.1055C9.00391 18.1055 8.68164 17.793 8.68164 17.334C8.68164 17.168 8.71094 17.0117 8.79883 16.7676L12.5098 6.82617C12.7344 6.24023 13.1445 5.94727 13.7402 5.94727C14.3457 5.94727 14.7656 6.24023 14.9902 6.82617L18.7012 16.7676C18.7988 17.0117 18.8184 17.168 18.8184 17.3438C18.8184 17.793 18.4863 18.1055 17.998 18.1055C17.5781 18.1055 17.3047 17.9199 17.1191 17.3926L16.1035 14.4922L11.3965 14.4922L10.3809 17.3926C10.2051 17.9199 9.92188 18.1055 9.52148 18.1055ZM11.8555 13.125L15.6445 13.125L13.8086 7.91992L13.6914 7.91992Z", fillAlpha = 0.85f)
        }
        return _sFCharacterBubbleFill!!
    }

private var _sFCharacterBubbleFill: ImageVector? = null
