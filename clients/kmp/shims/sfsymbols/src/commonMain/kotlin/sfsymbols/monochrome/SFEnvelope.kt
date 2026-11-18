package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFEnvelope (monochrome)
 * Viewport: 30.498 x 21.4551
 */
public val SfSymbols.Monochrome.SFEnvelope: ImageVector
    get() {
        if (_sFEnvelope != null) {
            return _sFEnvelope!!
        }
        _sFEnvelope = sfIcon(
            name = "Monochrome.SFEnvelope",
            viewportWidth = 30.498f,
            viewportHeight = 21.4551f
        ) {
            addSfPath("M3.79883 21.4551L26.6309 21.4551C28.8477 21.4551 30.1367 20.1758 30.1367 17.6855L30.1367 3.76953C30.1367 1.2793 28.8379 0.00976562 26.3281 0.00976562L3.50586 0.00976562C1.28906 0.00976562 0 1.2793 0 3.76953L0 17.6855C0 20.1758 1.29883 21.4551 3.79883 21.4551ZM3.75977 19.8047C2.41211 19.8047 1.65039 19.0625 1.65039 17.6758L1.65039 3.76953C1.65039 2.40234 2.41211 1.66016 3.75977 1.66016L26.377 1.66016C27.7246 1.66016 28.4766 2.40234 28.4766 3.7793L28.4766 17.6855C28.4766 19.0625 27.7246 19.8047 26.377 19.8047ZM15.0586 13.9062C15.8398 13.9062 16.5723 13.6133 17.2754 12.9785L29.1602 2.28516L28.0273 1.15234L16.3086 11.6992C15.8984 12.0703 15.4883 12.2461 15.0586 12.2461C14.6289 12.2461 14.2285 12.0703 13.8184 11.6992L2.08984 1.15234L0.957031 2.28516L12.8516 12.9785C13.5547 13.6133 14.2773 13.9062 15.0586 13.9062ZM2.28516 19.9902L11.377 10.8984L10.2441 9.77539L1.15234 18.8477ZM27.8613 20L28.9844 18.8574L19.8926 9.77539L18.7598 10.8984Z", fillAlpha = 0.85f)
        }
        return _sFEnvelope!!
    }

private var _sFEnvelope: ImageVector? = null
