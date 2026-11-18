package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFHCircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFHCircleFill: ImageVector
    get() {
        if (_sFHCircleFill != null) {
            return _sFHCircleFill!!
        }
        _sFHCircleFill = sfIcon(
            name = "Monochrome.SFHCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM15.8398 7.64648L15.8398 11.7285L9.58008 11.7285L9.58008 7.64648C9.58008 7.01172 9.24805 6.65039 8.68164 6.65039C8.125 6.65039 7.79297 7.02148 7.79297 7.64648L7.79297 17.627C7.79297 18.2422 8.125 18.6133 8.68164 18.6133C9.24805 18.6133 9.58008 18.252 9.58008 17.627L9.58008 13.1445L15.8398 13.1445L15.8398 17.627C15.8398 18.2422 16.1719 18.6133 16.7188 18.6133C17.2949 18.6133 17.6172 18.252 17.6172 17.627L17.6172 7.64648C17.6172 7.01172 17.2949 6.65039 16.7188 6.65039C16.1719 6.65039 15.8398 7.02148 15.8398 7.64648Z", fillAlpha = 0.85f)
        }
        return _sFHCircleFill!!
    }

private var _sFHCircleFill: ImageVector? = null
