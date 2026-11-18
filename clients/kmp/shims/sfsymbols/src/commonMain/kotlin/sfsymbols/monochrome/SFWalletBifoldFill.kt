package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFWalletBifoldFill (monochrome)
 * Viewport: 29.7852 x 25.752
 */
public val SfSymbols.Monochrome.SFWalletBifoldFill: ImageVector
    get() {
        if (_sFWalletBifoldFill != null) {
            return _sFWalletBifoldFill!!
        }
        _sFWalletBifoldFill = sfIcon(
            name = "Monochrome.SFWalletBifoldFill",
            viewportWidth = 29.7852f,
            viewportHeight = 25.752f
        ) {
            addSfPath("M3.61328 25.752L24.1602 25.752C26.5625 25.752 27.7734 24.5312 27.7734 22.1777L27.7734 20.2148L23.2422 20.2148C20.1855 20.2148 18.3203 18.3398 18.3203 15.6445C18.3203 12.9395 20.1855 11.0645 23.2422 11.0645L27.7734 11.0645L27.7734 9.10156C27.7734 6.74805 26.5625 5.52734 24.1602 5.52734L3.61328 5.52734C1.2207 5.52734 0 6.73828 0 9.10156L0 22.1777C0 24.541 1.2207 25.752 3.61328 25.752ZM22.9199 16.9629C22.1875 16.9629 21.5918 16.3672 21.5918 15.6445C21.5918 14.9121 22.1875 14.3262 22.9199 14.3262C23.6523 14.3262 24.2383 14.9121 24.2383 15.6445C24.2383 16.3672 23.6523 16.9629 22.9199 16.9629ZM23.2422 18.7305L27.666 18.7305C28.7695 18.7305 29.4238 17.9785 29.4238 16.9922L29.4238 14.2871C29.4238 13.3008 28.7695 12.5488 27.666 12.5488L23.2422 12.5488C21.1328 12.5488 19.8047 13.6523 19.8047 15.6445C19.8047 17.6367 21.1328 18.7305 23.2422 18.7305ZM7.8125 3.97461L23.5938 3.97461C23.5938 3.79883 23.5938 3.62305 23.5938 3.44727C23.4961 0.927734 21.6895 0.0488281 19.1406 0.78125Z", fillAlpha = 0.85f)
        }
        return _sFWalletBifoldFill!!
    }

private var _sFWalletBifoldFill: ImageVector? = null
