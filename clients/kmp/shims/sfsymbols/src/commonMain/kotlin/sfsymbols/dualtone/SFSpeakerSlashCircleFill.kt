package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSpeakerSlashCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFSpeakerSlashCircleFill: ImageVector
    get() {
        if (_sFSpeakerSlashCircleFill != null) {
            return _sFSpeakerSlashCircleFill!!
        }
        _sFSpeakerSlashCircleFill = sfIcon(
            name = "Dualtone.SFSpeakerSlashCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.459C19.7266 25.459 25.4395 19.7461 25.4395 12.7344C25.4395 5.73242 19.7266 0.0195312 12.7148 0.0195312C5.71289 0.0195312 0 5.73242 0 12.7344C0 19.7461 5.71289 25.459 12.7148 25.459Z", fillAlpha = 0.2125f)
            addSfPath("M7.04102 6.29883L19.1602 18.418C19.3652 18.623 19.3652 18.9453 19.1602 19.1699C18.9551 19.375 18.6035 19.375 18.4082 19.1699L6.2793 7.05078C6.08398 6.85547 6.07422 6.51367 6.2793 6.29883C6.50391 6.08398 6.83594 6.08398 7.04102 6.29883ZM15.8594 17.8418L15.8594 18.2031C15.8594 18.623 15.5664 18.9258 15.1562 18.9258C14.8438 18.9258 14.5898 18.8086 14.3066 18.5254L11.25 15.6152C11.2012 15.5664 11.1035 15.5371 11.0254 15.5371L8.95508 15.5371C8.05664 15.5371 7.55859 15.0293 7.55859 14.0918L7.55859 11.4258C7.55859 10.8137 7.77536 10.3844 8.18212 10.1645ZM15.8594 7.32422L15.8594 13.9031L11.568 9.6083L14.3066 6.99219C14.6289 6.69922 14.834 6.57227 15.1465 6.57227C15.5664 6.57227 15.8594 6.91406 15.8594 7.32422Z", fillAlpha = 0.85f)
        }
        return _sFSpeakerSlashCircleFill!!
    }

private var _sFSpeakerSlashCircleFill: ImageVector? = null
