package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFSpatialCaptureFill (monochrome)
 * Viewport: 25.2344 x 27.9248
 */
public val SfSymbols.Monochrome.SFSpatialCaptureFill: ImageVector
    get() {
        if (_sFSpatialCaptureFill != null) {
            return _sFSpatialCaptureFill!!
        }
        _sFSpatialCaptureFill = sfIcon(
            name = "Monochrome.SFSpatialCaptureFill",
            viewportWidth = 25.2344f,
            viewportHeight = 27.9248f
        ) {
            addSfPath("M13.1445 25.9204C12.7051 26.1743 12.1777 26.1743 11.7285 25.9204L2.43164 20.5298C2.04102 20.3052 1.79688 19.9146 1.75781 19.4849L1.75781 19.3677L4.55078 16.8091C5.07812 16.3208 5.625 16.0864 6.21094 16.0864C6.83594 16.0864 7.42188 16.3306 7.95898 16.8286L10.3809 19.0259L16.2305 13.8013C16.8359 13.2544 17.4707 13.0005 18.2031 13.0005C18.916 13.0005 19.5898 13.2739 20.1758 13.811L23.1445 16.5747L23.1445 19.3188C23.1445 19.8267 22.8906 20.2759 22.4414 20.5298ZM8.20312 13.8989C6.52344 13.8989 5.14648 12.522 5.14648 10.8423C5.14648 9.18213 6.52344 7.79541 8.20312 7.79541C9.86328 7.79541 11.2305 9.18213 11.2305 10.8423C11.2305 12.522 9.86328 13.8989 8.20312 13.8989ZM1.34766 21.9067L11.0938 27.5513C11.9531 28.0493 12.9297 28.0493 13.7793 27.5513L23.5352 21.9067C24.375 21.4185 24.873 20.5688 24.873 19.5825L24.873 8.32275C24.873 7.34619 24.375 6.48682 23.5352 5.99854L13.7793 0.373535C12.9297-0.124512 11.9531-0.124512 11.0938 0.373535L1.34766 5.99854C0.498047 6.48682 0 7.34619 0 8.32275L0 19.5825C0 20.5688 0.498047 21.4185 1.34766 21.9067Z", fillAlpha = 0.85f)
        }
        return _sFSpatialCaptureFill!!
    }

private var _sFSpatialCaptureFill: ImageVector? = null
