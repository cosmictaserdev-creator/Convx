package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFArrowshapeUpCircle (monochrome)
 * Viewport: 25.8008 x 25.459
 */
public val SfSymbols.Monochrome.SFArrowshapeUpCircle: ImageVector
    get() {
        if (_sFArrowshapeUpCircle != null) {
            return _sFArrowshapeUpCircle!!
        }
        _sFArrowshapeUpCircle = sfIcon(
            name = "Monochrome.SFArrowshapeUpCircle",
            viewportWidth = 25.8008f,
            viewportHeight = 25.459f
        ) {
            addSfPath("M12.7148 25.4395C19.7363 25.4395 25.4395 19.7461 25.4395 12.7246C25.4395 5.70312 19.7363 0 12.7148 0C5.69336 0 0 5.70312 0 12.7246C0 19.7461 5.69336 25.4395 12.7148 25.4395ZM12.7148 23.623C6.68945 23.623 1.81641 18.75 1.81641 12.7246C1.81641 6.69922 6.68945 1.82617 12.7148 1.82617C18.7402 1.82617 23.6133 6.69922 23.6133 12.7246C23.6133 18.75 18.7402 23.623 12.7148 23.623Z", fillAlpha = 0.85f)
            addSfPath("M15.3906 18.3008L15.3906 9.92188C15.3906 8.95508 14.8535 8.39844 13.9062 8.39844L11.5332 8.39844C10.5957 8.39844 10.0586 8.95508 10.0586 9.92188L10.0586 18.3008C10.0586 19.2676 10.5957 19.8242 11.5332 19.8242L13.9062 19.8242C14.8535 19.8242 15.3906 19.2676 15.3906 18.3008ZM7.13867 12.4121L18.2617 12.4121C18.6816 12.4121 19.0234 12.1191 19.0234 11.6699C19.0234 11.377 18.8965 11.1621 18.5938 10.8496L13.4668 5.3418C13.1836 5.04883 12.9395 4.9707 12.7148 4.9707C12.5 4.9707 12.2559 5.04883 11.9824 5.3418L6.79688 10.8496C6.54297 11.1328 6.40625 11.3867 6.40625 11.6895C6.40625 12.1191 6.70898 12.4121 7.13867 12.4121Z", fillAlpha = 0.85f)
        }
        return _sFArrowshapeUpCircle!!
    }

private var _sFArrowshapeUpCircle: ImageVector? = null
