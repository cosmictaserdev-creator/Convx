package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFBoltBadgeClock (monochrome)
 * Viewport: 31.4258 x 31.3184
 */
public val SfSymbols.Monochrome.SFBoltBadgeClock: ImageVector
    get() {
        if (_sFBoltBadgeClock != null) {
            return _sFBoltBadgeClock!!
        }
        _sFBoltBadgeClock = sfIcon(
            name = "Monochrome.SFBoltBadgeClock",
            viewportWidth = 31.4258f,
            viewportHeight = 31.3184f
        ) {
            addSfPath("M20.0488 2.96875L16.25 12.8906L23.418 12.8906C23.877 12.8906 24.1797 13.2031 24.1797 13.6035C24.1797 13.8574 24.0723 14.082 23.877 14.3262L22.8842 15.6008C21.8578 15.6837 20.8856 15.9722 20.0099 16.4278L21.4746 14.502L13.7695 14.502L17.306 6.4402L9.57031 16.6113L17.2754 16.6113L13.7389 24.6731L15.8244 21.931C15.7246 22.4185 15.6738 22.9226 15.6738 23.4375C15.6738 23.8722 15.7096 24.299 15.7874 24.7121L12.4902 28.9453C11.7676 29.873 10.5762 29.2773 11.0059 28.1445L14.8047 18.2227L7.64648 18.2227C7.17773 18.2227 6.875 17.9102 6.875 17.5C6.875 17.2461 6.98242 17.0312 7.17773 16.7871L18.5645 2.16797C19.2871 1.23047 20.4785 1.82617 20.0488 2.96875Z", fillAlpha = 0.85f)
            addSfPath("M29.8633 23.4375C29.8633 26.8945 26.9824 29.7559 23.5547 29.7559C20.0977 29.7559 17.2363 26.9141 17.2363 23.4375C17.2363 19.9805 20.0977 17.1289 23.5547 17.1289C27.0215 17.1289 29.8633 19.9707 29.8633 23.4375ZM22.8906 19.6582L22.8906 22.9688L20.5273 22.9688C20.1367 22.9688 19.8047 23.291 19.8047 23.6719C19.8047 24.0723 20.127 24.3945 20.5273 24.3945L23.6035 24.3945C24.0137 24.3945 24.3262 24.082 24.3262 23.6719L24.3262 19.6582C24.3262 19.2773 24.0039 18.9648 23.6035 18.9648C23.2129 18.9648 22.8906 19.2773 22.8906 19.6582Z", fillAlpha = 0.85f)
        }
        return _sFBoltBadgeClock!!
    }

private var _sFBoltBadgeClock: ImageVector? = null
