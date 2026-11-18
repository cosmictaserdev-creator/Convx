package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltSlash (dualtone)
 * Viewport: 23.9038 x 27.9091
 */
public val SfSymbols.Dualtone.SFBoltSlash: ImageVector
    get() {
        if (_sFBoltSlash != null) {
            return _sFBoltSlash!!
        }
        _sFBoltSlash = sfIcon(
            name = "Dualtone.SFBoltSlash",
            viewportWidth = 23.9038f,
            viewportHeight = 27.9091f
        ) {
            addSfPath("M7.48838 12.699L5.80933 14.9067L9.69605 14.9067L12.3569 17.5676L9.9891 22.9653L13.3416 18.5523L14.5558 19.7665L8.72925 27.2407C8.0066 28.1684 6.81519 27.5727 7.25464 26.4399L11.0535 16.518L3.8855 16.518C3.42652 16.518 3.11402 16.2055 3.11402 15.7954C3.11402 15.5415 3.22144 15.3266 3.41675 15.0825L6.24448 11.4551ZM16.2878 1.26413L12.489 11.186L19.657 11.186C20.116 11.186 20.4187 11.4985 20.4187 11.8989C20.4187 12.1528 20.3211 12.3774 20.1257 12.6216L17.2754 16.2779L16.0215 15.0247L17.7136 12.7973L13.793 12.7973L11.1621 10.1678L13.545 4.73559L10.1687 9.17488L8.96216 7.96898L14.8132 0.463352C15.5359-0.474148 16.7273 0.121555 16.2878 1.26413Z", fillAlpha = 0.425f)
            addSfPath("M20.7117 23.9985C21.0339 24.3208 21.5711 24.3208 21.8933 23.9985C22.2058 23.686 22.2156 23.1391 21.8933 22.8169L2.78199 3.70554C2.46949 3.39304 1.91285 3.38327 1.59058 3.70554C1.27808 4.01804 1.27808 4.56491 1.59058 4.88718Z", fillAlpha = 0.85f)
        }
        return _sFBoltSlash!!
    }

private var _sFBoltSlash: ImageVector? = null
