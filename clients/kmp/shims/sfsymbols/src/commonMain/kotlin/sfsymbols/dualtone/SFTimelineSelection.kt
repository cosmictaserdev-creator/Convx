package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFTimelineSelection (dualtone)
 * Viewport: 43.1934 x 22.9785
 */
public val SfSymbols.Dualtone.SFTimelineSelection: ImageVector
    get() {
        if (_sFTimelineSelection != null) {
            return _sFTimelineSelection!!
        }
        _sFTimelineSelection = sfIcon(
            name = "Dualtone.SFTimelineSelection",
            viewportWidth = 43.1934f,
            viewportHeight = 22.9785f
        ) {
            addSfPath("M11.8164 22.9785L31.0254 22.9785C33.5547 22.9785 34.8242 21.709 34.8242 19.2188L34.8242 3.78906C34.8242 1.29883 33.5547 0.0292969 31.0254 0.0292969L11.8164 0.0292969C9.29688 0.0292969 8.00781 1.28906 8.00781 3.78906L8.00781 19.2188C8.00781 21.7188 9.29688 22.9785 11.8164 22.9785ZM10.4102 15.5957L10.4102 7.41211C10.4102 7.05078 10.7129 6.74805 11.0742 6.74805C11.4453 6.74805 11.748 7.05078 11.748 7.41211L11.748 15.5957C11.748 15.957 11.4453 16.2598 11.0742 16.2598C10.7129 16.2598 10.4102 15.957 10.4102 15.5957ZM14.1309 21.25L14.1309 1.75781L28.7012 1.75781L28.7012 21.25ZM31.1035 15.5957L31.1035 7.41211C31.1035 7.05078 31.4062 6.74805 31.7676 6.74805C32.1387 6.74805 32.4414 7.05078 32.4414 7.41211L32.4414 15.5957C32.4414 15.957 32.1387 16.2598 31.7676 16.2598C31.4062 16.2598 31.1035 15.957 31.1035 15.5957Z", fillAlpha = 0.425f)
            addSfPath("M0 14.3066C0 14.9316 0.517578 15.1855 1.04492 14.8535L5.52734 12.1289C6.05469 11.8066 6.03516 11.1816 5.52734 10.8789L1.03516 8.125C0.507812 7.79297 0 8.06641 0 8.68164ZM42.832 14.3066L42.832 8.68164C42.832 8.06641 42.3242 7.79297 41.7969 8.125L37.3047 10.8789C36.7969 11.1816 36.7773 11.8066 37.3047 12.1289L41.7871 14.8535C42.3145 15.1855 42.832 14.9316 42.832 14.3066Z", fillAlpha = 0.85f)
        }
        return _sFTimelineSelection!!
    }

private var _sFTimelineSelection: ImageVector? = null
