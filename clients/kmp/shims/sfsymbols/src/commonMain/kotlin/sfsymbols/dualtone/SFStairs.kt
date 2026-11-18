package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStairs (dualtone)
 * Viewport: 24.3359 x 23.9941
 */
public val SfSymbols.Dualtone.SFStairs: ImageVector
    get() {
        if (_sFStairs != null) {
            return _sFStairs!!
        }
        _sFStairs = sfIcon(
            name = "Dualtone.SFStairs",
            viewportWidth = 24.3359f,
            viewportHeight = 23.9941f
        ) {
            addSfPath("M0.859375 23.9746C1.33789 23.9746 1.72852 23.584 1.72852 23.1152L1.72852 18.7109L6.30859 18.7109C6.93359 18.7109 7.39258 18.2617 7.39258 17.6465L7.39258 13.0566L11.9727 13.0566C12.5977 13.0566 13.0566 12.5977 13.0566 11.9824L13.0566 7.39258L17.6367 7.39258C18.252 7.39258 18.7109 6.93359 18.7109 6.31836L18.7109 1.72852L23.1055 1.72852C23.584 1.72852 23.9746 1.34766 23.9746 0.869141C23.9746 0.390625 23.584 0 23.1055 0L18.0566 0C17.4414 0 16.9824 0.458984 16.9824 1.08398L16.9824 5.66406L12.3926 5.66406C11.7773 5.66406 11.3184 6.12305 11.3184 6.74805L11.3184 11.3184L6.72852 11.3184C6.11328 11.3184 5.66406 11.7871 5.66406 12.4023L5.66406 16.9824L1.07422 16.9824C0.449219 16.9824 0 17.4414 0 18.0664L0 23.1152C0 23.584 0.380859 23.9746 0.859375 23.9746Z", fillAlpha = 0.85f)
        }
        return _sFStairs!!
    }

private var _sFStairs: ImageVector? = null
