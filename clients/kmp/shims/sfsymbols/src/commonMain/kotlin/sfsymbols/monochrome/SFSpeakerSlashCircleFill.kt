package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSpeakerSlashCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFSpeakerSlashCircleFill: ImageVector
    get() {
        if (_sFSpeakerSlashCircleFill != null) {
            return _sFSpeakerSlashCircleFill!!
        }
        _sFSpeakerSlashCircleFill = sfIcon(
            name = "Monochrome.SFSpeakerSlashCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7344C25.4395 19.7461 19.7266 25.459 12.7148 25.459C5.71289 25.459 0 19.7461 0 12.7344C0 5.73242 5.71289 0.0195312 12.7148 0.0195312C19.7266 0.0195312 25.4395 5.73242 25.4395 12.7344ZM6.2793 6.29883C6.07422 6.51367 6.08398 6.85547 6.2793 7.05078L18.4082 19.1699C18.6035 19.375 18.9551 19.375 19.1602 19.1699C19.3652 18.9453 19.3652 18.623 19.1602 18.418L7.04102 6.29883C6.83594 6.08398 6.50391 6.08398 6.2793 6.29883ZM7.55859 11.4258L7.55859 14.0918C7.55859 15.0293 8.05664 15.5371 8.95508 15.5371L11.0254 15.5371C11.1035 15.5371 11.2012 15.5664 11.25 15.6152L14.3066 18.5254C14.5898 18.8086 14.8438 18.9258 15.1562 18.9258C15.5664 18.9258 15.8594 18.623 15.8594 18.2031L15.8594 17.8418L8.18212 10.1645C7.77536 10.3844 7.55859 10.8137 7.55859 11.4258ZM14.3066 6.99219L11.568 9.6083L15.8594 13.9031L15.8594 7.32422C15.8594 6.91406 15.5664 6.57227 15.1465 6.57227C14.834 6.57227 14.6289 6.69922 14.3066 6.99219Z", fillAlpha = 0.85f)
        }
        return _sFSpeakerSlashCircleFill!!
    }

private var _sFSpeakerSlashCircleFill: ImageVector? = null
