package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFFlashlightOnFill (monochrome)
 * Viewport: 12.5879 x 29.5801
 */
public val SfSymbols.Monochrome.SFFlashlightOnFill: ImageVector
    get() {
        if (_sFFlashlightOnFill != null) {
            return _sFFlashlightOnFill!!
        }
        _sFFlashlightOnFill = sfIcon(
            name = "Monochrome.SFFlashlightOnFill",
            viewportWidth = 12.5879f,
            viewportHeight = 29.5801f
        ) {
            addSfPath("M0 2.88086L12.2266 2.88086L12.2266 2.43164C12.2266 0.800781 11.4355 0 9.85352 0L2.37305 0C0.791016 0 0 0.800781 0 2.43164ZM4.94141 29.5703L7.28516 29.5703C8.81836 29.5703 9.64844 28.7207 9.64844 27.1289L9.64844 12.6855C9.64844 11.2891 9.9707 10.3418 10.4688 9.57031L11.1914 8.4375C11.8066 7.48047 12.2266 6.64062 12.2266 5.51758L12.2266 4.23828L0 4.23828L0 5.51758C0 6.64062 0.419922 7.48047 1.02539 8.4375L1.75781 9.57031C2.25586 10.3418 2.57812 11.2891 2.57812 12.6855L2.57812 27.1289C2.57812 28.7207 3.4082 29.5703 4.94141 29.5703ZM3.92578 17.5391L3.92578 13.8281C3.92578 12.6074 4.88281 11.6406 6.12305 11.6406C7.34375 11.6406 8.30078 12.6074 8.30078 13.8281L8.30078 17.5391C8.30078 18.7598 7.34375 19.7168 6.12305 19.7168C4.88281 19.7168 3.92578 18.7598 3.92578 17.5391ZM6.12305 12.4219C5.3125 12.4219 4.70703 13.0273 4.70703 13.8281C4.70703 14.5996 5.33203 15.2246 6.12305 15.2246C6.89453 15.2246 7.5293 14.5996 7.5293 13.8281C7.5293 13.0273 6.92383 12.4219 6.12305 12.4219Z", fillAlpha = 0.85f)
        }
        return _sFFlashlightOnFill!!
    }

private var _sFFlashlightOnFill: ImageVector? = null
