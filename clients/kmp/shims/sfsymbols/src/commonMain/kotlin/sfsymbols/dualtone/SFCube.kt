package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFCube (dualtone)
 * Viewport: 25.2344 x 27.9248
 */
public val SfSymbols.Dualtone.SFCube: ImageVector
    get() {
        if (_sFCube != null) {
            return _sFCube!!
        }
        _sFCube = sfIcon(
            name = "Dualtone.SFCube",
            viewportWidth = 25.2344f,
            viewportHeight = 27.9248f
        ) {
            addSfPath("M1.34766 21.9067L11.0938 27.5513C11.9531 28.0493 12.9297 28.0493 13.7793 27.5513L23.5352 21.9067C24.375 21.4185 24.873 20.5688 24.873 19.5825L24.873 8.32275C24.873 7.34619 24.375 6.48682 23.5352 5.99854L13.7793 0.373535C12.9297-0.124512 11.9531-0.124512 11.0938 0.373535L1.34766 5.99854C0.498047 6.48682 0 7.34619 0 8.32275L0 19.5825C0 20.5688 0.498047 21.4185 1.34766 21.9067ZM11.5723 25.813C11.4844 25.7739 11.4551 25.7544 11.3574 25.6958L2.43164 20.5298C1.99219 20.2759 1.72852 19.8267 1.72852 19.3188L1.72852 9.28955L11.5723 14.8169ZM12.4414 13.2935L2.25586 7.55127C2.32422 7.46338 2.37305 7.41455 2.5 7.33643L11.7383 1.99463C12.1777 1.74072 12.6953 1.74072 13.1348 1.99463L22.373 7.33643C22.5 7.41455 22.5586 7.46338 22.6172 7.55127ZM13.3008 25.813L13.3008 14.8169L23.1445 9.28955L23.1445 19.3188C23.1445 19.8267 22.8906 20.2759 22.4414 20.5298L13.5742 25.6665C13.4473 25.7349 13.418 25.7544 13.3008 25.813Z", fillAlpha = 0.85f)
        }
        return _sFCube!!
    }

private var _sFCube: ImageVector? = null
