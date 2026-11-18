package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAsteriskCircleFill (dualtone)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Dualtone.SFAsteriskCircleFill: ImageVector
    get() {
        if (_sFAsteriskCircleFill != null) {
            return _sFAsteriskCircleFill!!
        }
        _sFAsteriskCircleFill = sfIcon(
            name = "Dualtone.SFAsteriskCircleFill",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7266 25.4395 25.4395 19.7266 25.4395 12.7246C25.4395 5.71289 19.7266 0 12.7148 0C5.71289 0 0 5.71289 0 12.7246C0 19.7266 5.71289 25.4395 12.7148 25.4395Z", fillAlpha = 0.2125f)
            addSfPath("M12.7246 19.1211C12.1387 19.1211 11.7773 18.75 11.7871 18.1641L11.875 14.1602L8.29102 16.4551C8.125 16.5723 7.92969 16.6309 7.69531 16.6309C7.24609 16.6309 6.82617 16.2793 6.82617 15.7129C6.82617 15.332 7.00195 15.0879 7.36328 14.8828L11.0742 12.7637L7.35352 10.6738C7.00195 10.4688 6.81641 10.2148 6.81641 9.85352C6.81641 9.26758 7.25586 8.91602 7.71484 8.91602C7.93945 8.91602 8.13477 8.97461 8.29102 9.08203L11.875 11.3477L11.7871 7.25586C11.7773 6.66992 12.1387 6.28906 12.7246 6.28906C13.3105 6.28906 13.6328 6.65039 13.623 7.25586L13.5254 11.3477L17.1289 9.0625C17.2949 8.94531 17.5 8.88672 17.7246 8.88672C18.1836 8.88672 18.6133 9.24805 18.6133 9.81445C18.6133 10.1758 18.4375 10.4297 18.0664 10.6543L14.3164 12.7637L18.0664 14.9023C18.4375 15.1074 18.6035 15.3516 18.6035 15.7227C18.6035 16.2988 18.1738 16.6504 17.7246 16.6504C17.4902 16.6504 17.3047 16.5918 17.1289 16.4746L13.5254 14.1602L13.623 18.1641C13.6328 18.7695 13.3105 19.1211 12.7246 19.1211Z", fillAlpha = 0.85f)
        }
        return _sFAsteriskCircleFill!!
    }

private var _sFAsteriskCircleFill: ImageVector? = null
