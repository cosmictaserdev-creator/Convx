package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFStarSlashFill (dualtone)
 * Viewport: 28.0136 x 28.1738
 */
public val SfSymbols.Dualtone.SFStarSlashFill: ImageVector
    get() {
        if (_sFStarSlashFill != null) {
            return _sFStarSlashFill!!
        }
        _sFStarSlashFill = sfIcon(
            name = "Dualtone.SFStarSlashFill",
            viewportWidth = 28.0136f,
            viewportHeight = 28.1738f
        ) {
            addSfPath("M22.7779 25.3803C22.7584 25.7374 22.6128 26.0122 22.3515 26.2109C21.8828 26.5527 21.3163 26.4453 20.6132 25.9375L13.8261 20.957L7.02924 25.9375C6.33588 26.4453 5.75971 26.5527 5.30072 26.2109C4.8515 25.8691 4.74408 25.3027 5.01752 24.4824L7.68354 16.4941L0.837833 11.5723C0.144474 11.0742-0.128964 10.5566 0.056583 10.0098C0.232364 9.47266 0.740177 9.20898 1.60932 9.20898L6.55953 9.20898ZM15.0663 1.23047L17.6249 9.20898L26.0331 9.20898C26.9023 9.20898 27.4199 9.47266 27.5956 10.0098C27.7714 10.5566 27.5078 11.0742 26.8046 11.5723L20.0346 16.4397L10.6905 7.11829L12.5859 1.23047C12.8496 0.410156 13.2499 0 13.8261 0C14.3925 0 14.7929 0.410156 15.0663 1.23047Z", fillAlpha = 0.425f)
            addSfPath("M24.6953 25.3613C25.0175 25.6934 25.5546 25.6934 25.8671 25.3613C26.1894 25.0293 26.1992 24.5117 25.8671 24.1895L5.67182 4.04297C5.35932 3.7207 4.82221 3.71094 4.49018 4.04297C4.16791 4.35547 4.16791 4.90234 4.49018 5.21484Z", fillAlpha = 0.85f)
        }
        return _sFStarSlashFill!!
    }

private var _sFStarSlashFill: ImageVector? = null
