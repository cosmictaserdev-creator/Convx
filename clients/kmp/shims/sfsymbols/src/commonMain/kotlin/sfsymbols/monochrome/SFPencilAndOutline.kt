package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPencilAndOutline (monochrome)
 * Viewport: 27.0068 x 29.6278
 */
public val SfSymbols.Monochrome.SFPencilAndOutline: ImageVector
    get() {
        if (_sFPencilAndOutline != null) {
            return _sFPencilAndOutline!!
        }
        _sFPencilAndOutline = sfIcon(
            name = "Monochrome.SFPencilAndOutline",
            viewportWidth = 27.0068f,
            viewportHeight = 29.6278f
        ) {
            addSfPath("M18.8621 3.64171L17.7092 4.79553C15.6237 3.5411 13.2558 2.80705 10.896 2.80705C5.34912 2.80705 1.97998 6.7426 1.97998 11.8891C1.97998 18.5102 7.36084 25.1508 15.7788 26.1664C16.2769 26.225 16.521 26.5863 16.521 26.9477C16.521 27.3383 16.2183 27.7485 15.603 27.6703C7.87842 27.0649 0.446777 20.4438 0.446777 11.9379C0.446777 5.86369 4.52881 1.26408 10.9351 1.26408C13.6865 1.26408 16.4559 2.13656 18.8621 3.64171ZM26.2085 15.9028C26.2085 20.3266 23.1616 23.2563 18.9331 23.2563C16.0238 23.2563 12.8676 21.8935 10.4909 19.7004L12.1204 18.9475C14.1419 20.7028 16.6235 21.8012 18.9038 21.8012C22.0776 21.8012 24.7144 19.5649 24.7144 15.9028C24.7144 13.4594 23.8348 11.0964 22.3807 9.07248L23.5117 7.9457C25.1859 10.2374 26.2085 12.9748 26.2085 15.9028Z", fillAlpha = 0.85f)
            addSfPath("M11.4917 17.2113L22.8296 5.89299L21.0913 4.15471L9.76318 15.4731L8.65967 17.8461C8.56201 18.0903 8.80615 18.3539 9.05029 18.2465ZM23.728 5.00432L24.7046 4.03752C25.1831 3.559 25.2026 2.91447 24.7241 2.46525L24.4604 2.22111C24.0405 1.82072 23.4058 1.82072 22.9565 2.27971L21.9897 3.25627Z", fillAlpha = 0.85f)
        }
        return _sFPencilAndOutline!!
    }

private var _sFPencilAndOutline: ImageVector? = null
