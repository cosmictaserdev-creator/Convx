package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBaseballDiamondBases (dualtone)
 * Viewport: 35.5273 x 18.3428
 */
public val SfSymbols.Dualtone.SFBaseballDiamondBases: ImageVector
    get() {
        if (_sFBaseballDiamondBases != null) {
            return _sFBaseballDiamondBases!!
        }
        _sFBaseballDiamondBases = sfIcon(
            name = "Dualtone.SFBaseballDiamondBases",
            viewportWidth = 35.5273f,
            viewportHeight = 18.3428f
        ) {
            addSfPath("M0.615234 13.9712L6.40625 17.9946C7.02148 18.4243 7.89062 18.4048 8.57422 17.9263L14.2578 13.9712C15.0781 13.4048 15.0781 12.5161 14.2578 11.9497L8.52539 7.95557C7.88086 7.50635 7.02148 7.48682 6.40625 7.91651L0.615234 11.9497C-0.205078 12.5161-0.205078 13.4048 0.615234 13.9712ZM10.7617 6.36378L16.5527 10.3872C17.168 10.8267 18.0371 10.7974 18.7207 10.3286L24.4141 6.36378C25.2246 5.79737 25.2246 4.91846 24.4141 4.34229L18.6719 0.348152C18.0273-0.101067 17.168-0.120598 16.5527 0.318855L10.7617 4.34229C9.94141 4.91846 9.94141 5.79737 10.7617 6.36378ZM20.9082 13.9712L26.6504 17.9653C27.3047 18.4146 28.1738 18.4146 28.8184 17.9556L34.5605 13.9712C35.3711 13.4048 35.3711 12.5161 34.5605 11.9497L28.8184 7.95557C28.1738 7.50635 27.3145 7.48682 26.6992 7.91651L20.9082 11.9497C20.0879 12.5161 20.0879 13.4048 20.9082 13.9712Z", fillAlpha = 0.85f)
        }
        return _sFBaseballDiamondBases!!
    }

private var _sFBaseballDiamondBases: ImageVector? = null
