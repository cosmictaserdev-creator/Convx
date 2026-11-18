package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFShield (dualtone)
 * Viewport: 20.9863 x 25.8691
 */
public val SfSymbols.Dualtone.SFShield: ImageVector
    get() {
        if (_sFShield != null) {
            return _sFShield!!
        }
        _sFShield = sfIcon(
            name = "Dualtone.SFShield",
            viewportWidth = 20.9863f,
            viewportHeight = 25.8691f
        ) {
            addSfPath("M10.3125 25.8691C10.5371 25.8691 10.8594 25.7812 11.1523 25.6055C18.252 21.5527 20.625 19.9512 20.625 15.5273L20.625 5.91797C20.625 4.58008 20.0977 4.15039 18.9746 3.68164C17.2949 3.00781 13.0176 1.43555 11.3379 0.859375C11.0059 0.742188 10.6543 0.664062 10.3125 0.664062C9.9707 0.664062 9.62891 0.761719 9.29688 0.859375C7.61719 1.38672 3.33008 3.01758 1.65039 3.68164C0.527344 4.13086 0 4.58008 0 5.91797L0 15.5273C0 19.9512 2.40234 21.5137 9.47266 25.6055C9.77539 25.7812 10.0977 25.8691 10.3125 25.8691ZM10.3125 23.9453C10.1074 23.9453 9.88281 23.8477 9.56055 23.6426C3.7207 20.0488 1.70898 19.0039 1.70898 15.1562L1.70898 6.25C1.70898 5.69336 1.81641 5.47852 2.24609 5.3125C4.00391 4.60938 7.98828 3.22266 9.74609 2.5293C9.9707 2.44141 10.166 2.39258 10.3125 2.39258C10.459 2.39258 10.6543 2.44141 10.8789 2.5293C12.6465 3.22266 16.5918 4.67773 18.3887 5.3125C18.8086 5.46875 18.916 5.69336 18.916 6.25L18.916 15.1562C18.916 19.0039 16.8945 20.0293 11.0645 23.6426C10.7422 23.8477 10.5176 23.9453 10.3125 23.9453Z", fillAlpha = 0.85f)
        }
        return _sFShield!!
    }

private var _sFShield: ImageVector? = null
