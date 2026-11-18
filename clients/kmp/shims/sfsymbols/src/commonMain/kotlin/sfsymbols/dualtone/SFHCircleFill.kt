package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFHCircleFill: ImageVector
    get() {
        if (_sFHCircleFill != null) {
            return _sFHCircleFill!!
        }
        _sFHCircleFill = sfIcon(
            name = "Dualtone.SFHCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M8.68164 18.6133C8.125 18.6133 7.79297 18.2422 7.79297 17.627L7.79297 7.64648C7.79297 7.02148 8.125 6.65039 8.68164 6.65039C9.24805 6.65039 9.58008 7.01172 9.58008 7.64648L9.58008 11.7285L15.8398 11.7285L15.8398 7.64648C15.8398 7.02148 16.1719 6.65039 16.7188 6.65039C17.2949 6.65039 17.6172 7.01172 17.6172 7.64648L17.6172 17.627C17.6172 18.252 17.2949 18.6133 16.7188 18.6133C16.1719 18.6133 15.8398 18.2422 15.8398 17.627L15.8398 13.1445L9.58008 13.1445L9.58008 17.627C9.58008 18.252 9.24805 18.6133 8.68164 18.6133Z", fillAlpha = 0.85f)
        }
        return _sFHCircleFill!!
    }

private var _sFHCircleFill: ImageVector? = null
