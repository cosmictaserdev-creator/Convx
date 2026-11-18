package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlaskFill (dualtone)
 * Viewport: 23.6523 x 25.8301
 */
public val SfSymbols.Dualtone.SFFlaskFill: ImageVector
    get() {
        if (_sFFlaskFill != null) {
            return _sFFlaskFill!!
        }
        _sFFlaskFill = sfIcon(
            name = "Dualtone.SFFlaskFill",
            viewportWidth = 23.6523f,
            viewportHeight = 25.8301f
        ) {
            addSfPath("M17.1484 0.898438C17.1484 1.38672 16.7773 1.80664 16.25 1.80664L16.1133 1.80664L16.1133 11.2109L22.4316 20.2832C23.0273 21.1426 23.291 21.9531 23.291 22.5684C23.291 24.375 22.0605 25.8105 20.0195 25.8105L3.26172 25.8105C1.23047 25.8105 0 24.375 0 22.5684C0 21.9531 0.224609 21.1914 0.771484 20.4102L7.17773 11.2109L7.17773 1.80664L7.04102 1.80664C6.50391 1.80664 6.13281 1.38672 6.13281 0.898438C6.13281 0.400391 6.50391 0 7.04102 0L16.25 0C16.7773 0 17.1484 0.400391 17.1484 0.898438ZM8.90625 10.8301L8.90625 12.1289L11.3184 12.1289C11.6797 12.1289 11.9629 11.8457 11.9629 11.4746C11.9629 11.123 11.6699 10.8301 11.3184 10.8301ZM8.90625 7.35352L8.90625 8.64258L11.3184 8.64258C11.6797 8.64258 11.9629 8.35938 11.9629 7.98828C11.9629 7.64648 11.6602 7.35352 11.3086 7.35352ZM8.90625 3.86719L8.90625 5.16602L11.3086 5.16602C11.6699 5.16602 11.9531 4.88281 11.9531 4.51172C11.9531 4.16016 11.6504 3.86719 11.2988 3.86719Z", fillAlpha = 0.85f)
        }
        return _sFFlaskFill!!
    }

private var _sFFlaskFill: ImageVector? = null
