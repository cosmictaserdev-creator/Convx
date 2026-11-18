package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFL1CircleFill (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFL1CircleFill: ImageVector
    get() {
        if (_sFL1CircleFill != null) {
            return _sFL1CircleFill!!
        }
        _sFL1CircleFill = sfIcon(
            name = "Monochrome.SFL1CircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M25.4395 12.7246C25.4395 19.7266 19.7266 25.4395 12.7148 25.4395C5.71289 25.4395 0 19.7266 0 12.7246C0 5.71289 5.71289 0 12.7148 0C19.7266 0 25.4395 5.71289 25.4395 12.7246ZM15.1172 8.06641L13.6914 9.19922C13.4961 9.3457 13.3594 9.55078 13.3594 9.83398C13.3594 10.1758 13.5742 10.4102 13.9062 10.4102C14.082 10.4102 14.1895 10.3613 14.3066 10.2734L15.5176 9.28711L15.5176 17.0117C15.5176 17.4609 15.8789 17.8125 16.3184 17.8125C16.7383 17.8125 17.0996 17.4609 17.0996 17.0117L17.0996 8.49609C17.0996 7.94922 16.748 7.61719 16.1914 7.61719C15.7422 7.61719 15.3809 7.87109 15.1172 8.06641ZM8.14453 8.4082L8.14453 16.875C8.14453 17.3828 8.42773 17.6953 8.91602 17.6953L12.4902 17.6953C12.8516 17.6953 13.1543 17.3926 13.1543 17.0312C13.1543 16.6699 12.8516 16.3672 12.4902 16.3672L9.7168 16.3672L9.7168 8.4082C9.7168 7.97852 9.35547 7.62695 8.92578 7.62695C8.50586 7.62695 8.14453 7.97852 8.14453 8.4082Z", fillAlpha = 0.85f)
        }
        return _sFL1CircleFill!!
    }

private var _sFL1CircleFill: ImageVector? = null
