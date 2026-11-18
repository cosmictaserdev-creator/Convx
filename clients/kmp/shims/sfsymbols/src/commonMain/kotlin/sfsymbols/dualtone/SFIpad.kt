package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFIpad (dualtone)
 * Viewport: 21.084 x 27.998
 */
public val SfSymbols.Dualtone.SFIpad: ImageVector
    get() {
        if (_sFIpad != null) {
            return _sFIpad!!
        }
        _sFIpad = sfIcon(
            name = "Dualtone.SFIpad",
            viewportWidth = 21.084f,
            viewportHeight = 27.998f
        ) {
            addSfPath("M1.72852 24.4043L1.72852 3.59375C1.72852 2.40234 2.42188 1.72852 3.64258 1.72852L17.0703 1.72852C18.291 1.72852 18.9844 2.40234 18.9844 3.59375L18.9844 24.4043C18.9844 25.5859 18.291 26.2598 17.0703 26.2598L3.64258 26.2598C2.42188 26.2598 1.72852 25.5859 1.72852 24.4043Z", fillAlpha = 0.2125f)
            addSfPath("M6.83594 25.3027L13.8965 25.3027C14.1992 25.3027 14.4238 25.0879 14.4238 24.7656C14.4238 24.4531 14.1992 24.2383 13.8965 24.2383L6.83594 24.2383C6.52344 24.2383 6.30859 24.4531 6.30859 24.7656C6.30859 25.0879 6.52344 25.3027 6.83594 25.3027ZM0 24.6777C0 26.6992 1.34766 27.998 3.4375 27.998L17.2852 27.998C19.375 27.998 20.7227 26.6992 20.7227 24.6777L20.7227 3.32031C20.7227 1.29883 19.375 0 17.2852 0L3.4375 0C1.34766 0 0 1.29883 0 3.32031ZM1.72852 24.4043L1.72852 3.59375C1.72852 2.40234 2.42188 1.72852 3.64258 1.72852L17.0703 1.72852C18.291 1.72852 18.9844 2.40234 18.9844 3.59375L18.9844 24.4043C18.9844 25.5859 18.291 26.2598 17.0703 26.2598L3.64258 26.2598C2.42188 26.2598 1.72852 25.5859 1.72852 24.4043Z", fillAlpha = 0.85f)
        }
        return _sFIpad!!
    }

private var _sFIpad: ImageVector? = null
