package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFAppwindowSwipeRectangle (monochrome)
 * Viewport: 29.9512 x 22.959
 */
public val SfSymbols.Monochrome.SFAppwindowSwipeRectangle: ImageVector
    get() {
        if (_sFAppwindowSwipeRectangle != null) {
            return _sFAppwindowSwipeRectangle!!
        }
        _sFAppwindowSwipeRectangle = sfIcon(
            name = "Monochrome.SFAppwindowSwipeRectangle",
            viewportWidth = 29.9512f,
            viewportHeight = 22.959f
        ) {
            addSfPath("M29.5898 3.76953L29.5898 19.1992C29.5898 21.6797 28.3105 22.959 25.7812 22.959L3.79883 22.959C1.2793 22.959 0 21.6992 0 19.1992L0 3.76953C0 1.26953 1.2793 0 3.79883 0L25.7812 0C28.3105 0 29.5898 1.2793 29.5898 3.76953ZM3.83789 1.72852C2.67 1.72852 1.95818 2.26297 1.77735 3.29102L1.30859 3.29102C1.06445 3.29102 0.869141 3.48633 0.869141 3.73047L0.869141 19.2285C0.869141 19.4727 1.07422 19.668 1.31836 19.668L1.77676 19.668C1.95674 20.7021 2.66882 21.2305 3.83789 21.2305L25.752 21.2305C26.896 21.2305 27.6187 20.7021 27.8023 19.668L28.3203 19.668C28.5645 19.668 28.7598 19.4727 28.7598 19.2285L28.7598 3.73047C28.7598 3.48633 28.5645 3.29102 28.3203 3.29102L27.8017 3.29102C27.6172 2.26297 26.8948 1.72852 25.752 1.72852Z", fillAlpha = 0.85f)
            addSfPath("M1.31836 19.668L17.7051 19.668C18.4961 19.668 18.9355 19.2188 18.9355 18.4277L18.9355 4.54102C18.9355 3.74023 18.4961 3.29102 17.7051 3.29102L1.30859 3.29102C1.06445 3.29102 0.869141 3.48633 0.869141 3.73047L0.869141 19.2285C0.869141 19.4727 1.07422 19.668 1.31836 19.668ZM21.7188 19.668L28.3203 19.668C28.5645 19.668 28.7598 19.4727 28.7598 19.2285L28.7598 3.73047C28.7598 3.48633 28.5645 3.29102 28.3203 3.29102L21.7188 3.29102C20.9375 3.29102 20.4883 3.74023 20.4883 4.54102L20.4883 18.4277C20.4883 19.2188 20.9375 19.668 21.7188 19.668Z", fillAlpha = 0.85f)
        }
        return _sFAppwindowSwipeRectangle!!
    }

private var _sFAppwindowSwipeRectangle: ImageVector? = null
