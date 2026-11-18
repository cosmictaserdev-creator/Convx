package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSpeakerCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSpeakerCircleFill: ImageVector
    get() {
        if (_sFSpeakerCircleFill != null) {
            return _sFSpeakerCircleFill!!
        }
        _sFSpeakerCircleFill = sfIcon(
            name = "Dualtone.SFSpeakerCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M15.1562 18.9258C14.8438 18.9258 14.5898 18.8086 14.3066 18.5254L11.25 15.6152C11.2012 15.5664 11.1035 15.5371 11.0254 15.5371L8.95508 15.5371C8.05664 15.5371 7.55859 15.0293 7.55859 14.0918L7.55859 11.4258C7.55859 10.498 8.05664 9.99023 8.95508 9.99023L11.0254 9.99023C11.1035 9.99023 11.2012 9.96094 11.25 9.91211L14.3066 6.99219C14.6289 6.69922 14.834 6.57227 15.1465 6.57227C15.5664 6.57227 15.8594 6.91406 15.8594 7.32422L15.8594 18.2031C15.8594 18.623 15.5664 18.9258 15.1562 18.9258Z", fillAlpha = 0.85f)
        }
        return _sFSpeakerCircleFill!!
    }

private var _sFSpeakerCircleFill: ImageVector? = null
