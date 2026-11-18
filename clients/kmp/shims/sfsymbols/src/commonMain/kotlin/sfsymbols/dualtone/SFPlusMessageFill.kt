package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPlusMessageFill (dualtone)
 * Viewport: 29.0234 x 25.8496
 */
public val SfSymbols.Dualtone.SFPlusMessageFill: ImageVector
    get() {
        if (_sFPlusMessageFill != null) {
            return _sFPlusMessageFill!!
        }
        _sFPlusMessageFill = sfIcon(
            name = "Dualtone.SFPlusMessageFill",
            viewportWidth = 29.0234f,
            viewportHeight = 25.8496f
        ) {
            addSfPath("M14.3262 23.8086C22.6465 23.8086 28.6621 18.7891 28.6621 11.9043C28.6621 4.99023 22.6367 0 14.3262 0C6.01562 0 0 4.99023 0 11.9043C0 16.8848 2.75391 18.2129 2.75391 20.4785C2.75391 21.4746 2.41211 22.1191 1.66016 22.793C1.17188 23.2324 1.41602 23.8086 2.13867 23.8086C3.82812 23.8086 5.60547 23.2031 6.875 22.2559C9.02344 23.2715 11.5625 23.8086 14.3262 23.8086Z", fillAlpha = 0.2125f)
            addSfPath("M15.3809 16.9629L15.3809 7.02148C15.3809 6.46484 14.9902 6.07422 14.4336 6.07422C13.8965 6.07422 13.5156 6.47461 13.5156 7.02148L13.5156 16.9629C13.5156 17.5098 13.8965 17.9102 14.4336 17.9102C14.9902 17.9102 15.3809 17.5195 15.3809 16.9629ZM9.48242 12.9199L19.4238 12.9199C19.9707 12.9199 20.3613 12.5391 20.3613 12.0117C20.3613 11.4453 19.9707 11.0645 19.4238 11.0645L9.48242 11.0645C8.92578 11.0645 8.53516 11.4453 8.53516 12.0117C8.53516 12.5391 8.93555 12.9199 9.48242 12.9199Z", fillAlpha = 0.85f)
        }
        return _sFPlusMessageFill!!
    }

private var _sFPlusMessageFill: ImageVector? = null
