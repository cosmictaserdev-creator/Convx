package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIpadCase (dualtone)
 * Viewport: 21.084 x 27.998
 */
public val SfSymbols.Dualtone.SFIpadCase: ImageVector
    get() {
        if (_sFIpadCase != null) {
            return _sFIpadCase!!
        }
        _sFIpadCase = sfIcon(
            name = "Dualtone.SFIpadCase",
            viewportWidth = 21.084f,
            viewportHeight = 27.998f
        ) {
            addSfPath("M0 24.6777C0 26.6992 1.34766 27.998 3.4375 27.998L17.2852 27.998C19.375 27.998 20.7227 26.6992 20.7227 24.6777L20.7227 3.32031C20.7227 1.29883 19.375 0 17.2852 0L3.4375 0C1.34766 0 0 1.29883 0 3.32031ZM8.85742 24.248C8.4668 24.248 8.14453 23.9258 8.14453 23.5352L8.14453 4.46289C8.14453 4.07227 8.4668 3.74023 8.85742 3.74023C9.25781 3.74023 9.58008 4.07227 9.58008 4.46289L9.58008 23.5352C9.58008 23.9258 9.25781 24.248 8.85742 24.248ZM15.1465 24.248C14.7559 24.248 14.4336 23.9258 14.4336 23.5352L14.4336 4.46289C14.4336 4.07227 14.7559 3.74023 15.1465 3.74023C15.5371 3.74023 15.8594 4.07227 15.8594 4.46289L15.8594 23.5352C15.8594 23.9258 15.5371 24.248 15.1465 24.248Z", fillAlpha = 0.85f)
        }
        return _sFIpadCase!!
    }

private var _sFIpadCase: ImageVector? = null
