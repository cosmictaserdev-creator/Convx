package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTagSlashFill (dualtone)
 * Viewport: 28.3106 x 29.3042
 */
public val SfSymbols.Dualtone.SFTagSlashFill: ImageVector
    get() {
        if (_sFTagSlashFill != null) {
            return _sFTagSlashFill!!
        }
        _sFTagSlashFill = sfIcon(
            name = "Dualtone.SFTagSlashFill",
            viewportWidth = 28.3106f,
            viewportHeight = 29.3042f
        ) {
            addSfPath("M19.0709 23.5394L14.6338 27.9871C12.8955 29.7351 11.0986 29.7546 9.3213 27.9773L1.32325 19.989C-0.454091 18.2312-0.424795 16.4148 1.31349 14.6765L5.76483 10.2392ZM23.7842 2.6062L26.7139 5.53589C27.6611 6.48316 27.9346 6.86402 27.9346 8.13355L27.9346 12.9285C27.9346 14.2566 27.8662 14.7351 26.9678 15.6238L22.1737 20.4293L8.87914 7.13475L13.6865 2.34253C14.585 1.45386 15.0635 1.3855 16.3916 1.3855L21.1865 1.3855C22.4463 1.3855 22.8272 1.64917 23.7842 2.6062ZM17.4951 9.57886C17.4951 10.4773 18.1787 11.1804 19.0967 11.1804C20.0147 11.1804 20.6983 10.4773 20.6983 9.57886C20.6983 8.68042 20.0147 7.9773 19.0967 7.9773C18.1787 7.9773 17.4951 8.68042 17.4951 9.57886Z", fillAlpha = 0.425f)
            addSfPath("M24.3115 26.8543C24.6338 27.1765 25.1611 27.1765 25.4834 26.8543C25.8057 26.532 25.8154 25.9949 25.4834 25.6726L3.63771 3.82691C3.31544 3.50464 2.7881 3.48511 2.4463 3.82691C2.1338 4.13941 2.1338 4.68628 2.4463 4.99878Z", fillAlpha = 0.85f)
        }
        return _sFTagSlashFill!!
    }

private var _sFTagSlashFill: ImageVector? = null
