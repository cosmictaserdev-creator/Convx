package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPaperclip (monochrome)
 * Viewport: 23.9865 x 27.0652
 */
public val SfSymbols.Monochrome.SFPaperclip: ImageVector
    get() {
        if (_sFPaperclip != null) {
            return _sFPaperclip!!
        }
        _sFPaperclip = sfIcon(
            name = "Monochrome.SFPaperclip",
            viewportWidth = 23.9865f,
            viewportHeight = 27.0652f
        ) {
            addSfPath("M20.4991 14.2992L11.1046 23.684C8.67292 26.1156 5.4112 25.8422 3.32136 23.7426C1.23151 21.6625 0.94831 18.4008 3.38972 15.9691L16.0167 3.34219C17.5401 1.80899 19.7178 1.76993 21.0655 3.11758C22.4229 4.475 22.3741 6.63321 20.8409 8.16641L8.5069 20.5199C7.82331 21.2035 7.01276 21.0277 6.51472 20.5492C6.04597 20.0609 5.87995 19.2504 6.54401 18.5668L15.294 9.82657C15.626 9.49454 15.6358 9.01602 15.3331 8.70352C15.0206 8.40079 14.5421 8.40079 14.2003 8.74258L5.4405 17.5121C4.18073 18.7719 4.24909 20.6273 5.34284 21.7309C6.50495 22.893 8.29206 22.893 9.55183 21.6332L21.9444 9.24063C24.2589 6.92618 24.044 3.92813 22.1202 2.0043C20.2257 0.10977 17.1885-0.153901 14.8643 2.17032L2.19831 14.8461C-0.877862 17.9223-0.565362 22.2191 2.13972 24.9242C4.85456 27.6293 9.15143 27.9418 12.2276 24.8754L21.6514 15.4516C21.9737 15.1293 21.9737 14.5629 21.6514 14.2602C21.3389 13.9379 20.8214 13.977 20.4991 14.2992Z", fillAlpha = 0.85f)
        }
        return _sFPaperclip!!
    }

private var _sFPaperclip: ImageVector? = null
