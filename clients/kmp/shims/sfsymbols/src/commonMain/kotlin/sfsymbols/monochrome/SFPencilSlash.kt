package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFPencilSlash (monochrome)
 * Viewport: 21.687 x 21.2703
 */
public val SfSymbols.Monochrome.SFPencilSlash: ImageVector
    get() {
        if (_sFPencilSlash != null) {
            return _sFPencilSlash!!
        }
        _sFPencilSlash = sfIcon(
            name = "Monochrome.SFPencilSlash",
            viewportWidth = 21.687f,
            viewportHeight = 21.2703f
        ) {
            addSfPath("M10.8337 12.5247L3.90015 19.4486L1.12671 20.601C0.853277 20.7181 0.560308 20.4056 0.677496 20.1322L1.8982 17.4369L8.82683 10.5179ZM17.9822 5.38612L13.9391 9.42358L11.9317 7.41736L15.9802 3.3744ZM19.9548 1.06971L20.3064 1.42127C20.8533 1.95838 20.824 2.59315 20.2087 3.19862L19.0271 4.37049L16.9959 2.34901L18.1873 1.17713C18.783 0.591197 19.4275 0.542368 19.9548 1.06971Z", fillAlpha = 0.85f)
            addSfPath("M18.9002 18.6674C19.2322 18.9896 19.7693 18.9896 20.0818 18.6674C20.4041 18.3353 20.4041 17.8178 20.0818 17.4857L4.19312 1.59706C3.87085 1.28456 3.32398 1.26502 3.00171 1.59706C2.68921 1.91932 2.68921 2.4662 3.00171 2.7787Z", fillAlpha = 0.85f)
        }
        return _sFPencilSlash!!
    }

private var _sFPencilSlash: ImageVector? = null
