package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFKeyHorizontal (monochrome)
 * Viewport: 31.3902 x 16.4941
 */
public val SfSymbols.Monochrome.SFKeyHorizontal: ImageVector
    get() {
        if (_sFKeyHorizontal != null) {
            return _sFKeyHorizontal!!
        }
        _sFKeyHorizontal = sfIcon(
            name = "Monochrome.SFKeyHorizontal",
            viewportWidth = 31.3902f,
            viewportHeight = 16.4941f
        ) {
            addSfPath("M0.309377 7.5C-0.0714825 7.94922-0.130076 8.55469 0.299611 8.98438L4.18633 12.8711C4.60625 13.291 5.25078 13.2715 5.66094 12.8613L7.74102 10.7812L10.7098 13.7598C11.1199 14.1699 11.7645 14.1504 12.1941 13.7305L15.0359 10.9082C16.6473 14.4727 19.4598 16.4746 22.8094 16.4746C27.3602 16.4746 31.032 12.793 31.032 8.24219C31.032 3.66211 27.3797 0 22.8094 0C19.4695 0 16.5008 1.94336 15.2215 5.00977L3.24883 5.00977C2.87774 5.00977 2.44805 5.11719 2.16485 5.44922ZM2.0086 8.24219L3.50274 6.73828L16.4617 6.73828C17.1746 3.7793 19.7527 1.72852 22.8094 1.72852C26.3934 1.72852 29.2938 4.62891 29.2938 8.24219C29.2938 11.8457 26.4031 14.7363 22.8094 14.7363C19.782 14.7363 17.1746 12.6758 16.325 9.32617L14.1375 9.32617L11.4422 12.0215L8.64922 9.19922L6.86211 9.19922L4.93828 11.1328ZM23.2684 8.24219C23.2684 9.3457 24.1668 10.2441 25.2801 10.2441C26.3836 10.2441 27.2723 9.3457 27.2723 8.24219C27.2723 7.11914 26.3836 6.24023 25.2801 6.24023C24.1668 6.24023 23.2684 7.12891 23.2684 8.24219Z", fillAlpha = 0.85f)
        }
        return _sFKeyHorizontal!!
    }

private var _sFKeyHorizontal: ImageVector? = null
