package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCloudBoltFill (monochrome)
 * Viewport: 29.5605 x 31.8856
 */
public val SfSymbols.Monochrome.SFCloudBoltFill: ImageVector
    get() {
        if (_sFCloudBoltFill != null) {
            return _sFCloudBoltFill!!
        }
        _sFCloudBoltFill = sfIcon(
            name = "Monochrome.SFCloudBoltFill",
            viewportWidth = 29.5605f,
            viewportHeight = 31.8856f
        ) {
            addSfPath("M17.8809 25.5082L15.1465 25.5082L15.1562 26.016L16.7676 22.9399C16.9043 22.6664 16.7969 22.4516 16.5137 22.4516L14.1895 22.4516C13.8379 22.4516 13.7012 22.5785 13.5938 22.8422L11.8652 26.7875C11.7285 27.1 11.875 27.2856 12.207 27.2856L14.4141 27.2856L14.5703 26.5336L12.5293 31.4946C12.4609 31.6703 12.5 31.8168 12.6172 31.8656C12.7344 31.9145 12.8809 31.8754 13.0078 31.7289L18.0957 26.1235C18.3496 25.85 18.2422 25.5082 17.8809 25.5082Z", fillAlpha = 0.85f)
            addSfPath("M6.73828 20.6254L22.4316 20.6254C26.5039 20.6254 29.5605 17.5395 29.5605 13.7114C29.5605 9.75627 26.3184 6.86565 22.0996 6.86565C20.5176 3.72112 17.6465 1.68987 13.9551 1.68987C9.26758 1.68987 5.41016 5.41057 5 10.0883C2.69531 10.7426 0.927734 12.686 0.927734 15.3422C0.927734 18.2524 3.05664 20.6254 6.73828 20.6254Z", fillAlpha = 0.85f)
        }
        return _sFCloudBoltFill!!
    }

private var _sFCloudBoltFill: ImageVector? = null
