package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFMappinSlash (dualtone)
 * Viewport: 22.4756 x 26.8457
 */
public val SfSymbols.Dualtone.SFMappinSlash: ImageVector
    get() {
        if (_sFMappinSlash != null) {
            return _sFMappinSlash!!
        }
        _sFMappinSlash = sfIcon(
            name = "Dualtone.SFMappinSlash",
            viewportWidth = 22.4756f,
            viewportHeight = 26.8457f
        ) {
            addSfPath("M12.0337 18.8443L12.0337 21.2598C12.0337 24.4434 11.4868 26.1426 11.0571 26.1426C10.6274 26.1426 10.0708 24.4238 10.0708 21.2598L10.0708 16.8837ZM15.4712 4.43359C15.4712 6.54297 14.0063 8.31055 12.0337 8.74023L12.0337 12.6367L10.0708 10.6738L10.0708 8.74023C8.09814 8.30078 6.64307 6.54297 6.64307 4.43359C6.64307 1.99219 8.60596 0 11.0571 0C13.5181 0 15.4712 1.99219 15.4712 4.43359ZM8.25439 3.125C8.25439 3.94531 8.95752 4.64844 9.7583 4.64844C10.5884 4.64844 11.272 3.94531 11.272 3.125C11.272 2.30469 10.5884 1.61133 9.7583 1.61133C8.95752 1.61133 8.25439 2.30469 8.25439 3.125Z", fillAlpha = 0.425f)
            addSfPath("M18.6157 23.4961C18.938 23.8184 19.4751 23.8184 19.7974 23.4961C20.1001 23.1738 20.1196 22.6465 19.7974 22.3145L2.78564 5.32227C2.47314 5.00977 1.92627 5 1.604 5.32227C1.28174 5.63477 1.28174 6.18164 1.604 6.50391Z", fillAlpha = 0.85f)
        }
        return _sFMappinSlash!!
    }

private var _sFMappinSlash: ImageVector? = null
