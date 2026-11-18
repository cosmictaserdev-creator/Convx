package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFDeskclock (monochrome)
 * Viewport: 25.8008 x 25.752
 */
public val SfSymbols.Monochrome.SFDeskclock: ImageVector
    get() {
        if (_sFDeskclock != null) {
            return _sFDeskclock!!
        }
        _sFDeskclock = sfIcon(
            name = "Monochrome.SFDeskclock",
            viewportWidth = 25.8008f,
            viewportHeight = 25.752f
        ) {
            addSfPath("M0.117188 19.4141L0.117188 23.7207C0.117188 25.1074 0.791016 25.752 2.1875 25.752L6.69922 25.752C3.80859 24.4238 1.5918 22.2754 0.117188 19.4141ZM25.3613 19.4141C23.8867 22.2754 21.6699 24.4238 18.7695 25.752L23.291 25.752C24.6875 25.752 25.3613 25.1074 25.3613 23.7207ZM12.7148 25.5859C19.7363 25.5859 25.4395 19.8926 25.4395 12.8711C25.4395 5.84961 19.7363 0.146484 12.7148 0.146484C5.69336 0.146484 0 5.84961 0 12.8711C0 19.8926 5.69336 25.5859 12.7148 25.5859ZM12.7148 23.7695C6.68945 23.7695 1.81641 18.8965 1.81641 12.8711C1.81641 6.8457 6.68945 1.97266 12.7148 1.97266C18.7402 1.97266 23.6133 6.8457 23.6133 12.8711C23.6133 18.8965 18.7402 23.7695 12.7148 23.7695Z", fillAlpha = 0.85f)
            addSfPath("M8.42773 9.66797L12.207 13.4473C12.5195 13.7598 12.9883 13.7598 13.291 13.4473L19.248 7.49023C19.5508 7.19727 19.5605 6.72852 19.248 6.42578C18.9355 6.13281 18.4863 6.12305 18.1738 6.42578L12.7441 11.8555L9.51172 8.60352C9.19922 8.29102 8.74023 8.30078 8.4375 8.60352C8.13477 8.89648 8.125 9.36523 8.42773 9.66797Z", fillAlpha = 0.85f)
        }
        return _sFDeskclock!!
    }

private var _sFDeskclock: ImageVector? = null
