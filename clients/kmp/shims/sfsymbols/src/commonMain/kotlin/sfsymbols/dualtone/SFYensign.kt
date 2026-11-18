package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFYensign (dualtone)
 * Viewport: 17.666 x 23.1934
 */
public val SfSymbols.Dualtone.SFYensign: ImageVector
    get() {
        if (_sFYensign != null) {
            return _sFYensign!!
        }
        _sFYensign = sfIcon(
            name = "Dualtone.SFYensign",
            viewportWidth = 17.666f,
            viewportHeight = 23.1934f
        ) {
            addSfPath("M8.65234 23.1738C9.27734 23.1738 9.6875 22.7637 9.6875 22.1094L9.6875 13.3203L17.0703 1.65039C17.2168 1.42578 17.3047 1.19141 17.3047 0.947266C17.3047 0.371094 16.8164 0 16.25 0C15.8691 0 15.5273 0.224609 15.332 0.556641L8.71094 11.377L8.60352 11.377L1.99219 0.576172C1.78711 0.224609 1.43555 0 1.05469 0C0.488281 0 0 0.361328 0 0.947266C0 1.21094 0.078125 1.42578 0.224609 1.65039L7.61719 13.3203L7.61719 22.1094C7.61719 22.7637 8.01758 23.1738 8.65234 23.1738ZM1.875 12.8125C1.875 13.2031 2.19727 13.5254 2.58789 13.5254L14.8926 13.5254C15.2832 13.5254 15.6055 13.2031 15.6055 12.8125C15.6055 12.4219 15.2832 12.0996 14.8926 12.0996L2.58789 12.0996C2.19727 12.0996 1.875 12.4219 1.875 12.8125ZM1.875 17.041C1.875 17.4316 2.19727 17.7539 2.58789 17.7539L14.8926 17.7539C15.2832 17.7539 15.6055 17.4316 15.6055 17.041C15.6055 16.6504 15.2832 16.3281 14.8926 16.3281L2.58789 16.3281C2.19727 16.3281 1.875 16.6504 1.875 17.041Z", fillAlpha = 0.85f)
        }
        return _sFYensign!!
    }

private var _sFYensign: ImageVector? = null
