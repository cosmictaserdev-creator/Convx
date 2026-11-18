package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBookmarkSlash (dualtone)
 * Viewport: 26.8994 x 26.6895
 */
public val SfSymbols.Dualtone.SFBookmarkSlash: ImageVector
    get() {
        if (_sFBookmarkSlash != null) {
            return _sFBookmarkSlash!!
        }
        _sFBookmarkSlash = sfIcon(
            name = "Dualtone.SFBookmarkSlash",
            viewportWidth = 26.8994f,
            viewportHeight = 26.6895f
        ) {
            addSfPath("M6.65283 9.77539L6.65283 23.6816C6.65283 23.9453 6.85791 23.9941 7.04346 23.8086L12.6587 18.252C13.02 17.9004 13.5181 17.9004 13.8794 18.252L19.4946 23.8086C19.6899 23.9941 19.895 23.9453 19.895 23.6816L19.895 23.0176L21.5942 24.7168L21.5942 25.2637C21.5942 26.1426 21.1353 26.6602 20.3833 26.6602C19.8364 26.6602 19.5044 26.3477 18.5474 25.4297L13.3813 20.2637C13.3228 20.2051 13.2153 20.2051 13.1567 20.2637L7.99072 25.4297C7.04346 26.3574 6.70166 26.6602 6.16455 26.6602C5.40283 26.6602 4.95361 26.1426 4.95361 25.2637L4.95361 8.07617ZM21.5942 3.45703L21.5942 18.5118L19.895 16.8111L19.895 3.48633C19.895 2.30469 19.2798 1.69922 18.0884 1.69922L8.45947 1.69922C7.26807 1.69922 6.65283 2.30469 6.65283 3.48633L6.65283 3.55724L5.14394 2.04702C5.55315 0.690281 6.64023 0 8.37158 0L18.1665 0C20.4321 0 21.5942 1.17188 21.5942 3.45703Z", fillAlpha = 0.425f)
            addSfPath("M23.8403 25.0391C24.1626 25.3613 24.6997 25.3613 25.0122 25.0391C25.3345 24.707 25.3442 24.1895 25.0122 23.8574L2.78564 1.62109C2.46338 1.29883 1.93604 1.28906 1.59424 1.62109C1.28174 1.93359 1.28174 2.48047 1.59424 2.80273Z", fillAlpha = 0.85f)
        }
        return _sFBookmarkSlash!!
    }

private var _sFBookmarkSlash: ImageVector? = null
