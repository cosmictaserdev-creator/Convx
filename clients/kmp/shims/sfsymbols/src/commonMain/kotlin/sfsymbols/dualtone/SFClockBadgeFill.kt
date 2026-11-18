package com.composables.sfsymbols.dualtone

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFClockBadgeFill (dualtone)
 * Viewport: 30.4199 x 29.0527
 */
public val SfSymbols.Dualtone.SFClockBadgeFill: ImageVector
    get() {
        if (_sFClockBadgeFill != null) {
            return _sFClockBadgeFill!!
        }
        _sFClockBadgeFill = sfIcon(
            name = "Dualtone.SFClockBadgeFill",
            viewportWidth = 30.4199f,
            viewportHeight = 29.0527f
        ) {
            addSfPath("M19.7354 2.71094C19.16 3.60521 18.8281 4.66708 18.8281 5.80078C18.8281 8.98438 21.4453 11.5918 24.6191 11.5918C25.5578 11.5918 26.4486 11.3637 27.2352 10.9555C27.5757 12.0852 27.7539 13.2831 27.7539 14.5215C27.7539 21.5234 22.041 27.2363 15.0293 27.2363C8.02734 27.2363 2.31445 21.5234 2.31445 14.5215C2.31445 7.50977 8.02734 1.79688 15.0293 1.79688C16.6921 1.79688 18.2819 2.11818 19.7354 2.71094ZM14.2676 6.32812L14.2676 14.2871L8.27148 14.2871C7.8418 14.2871 7.51953 14.6191 7.51953 15.0391C7.51953 15.4688 7.8418 15.8008 8.27148 15.8008L15.0195 15.8008C15.4492 15.8008 15.7812 15.4688 15.7812 15.0391L15.7812 6.32812C15.7812 5.9082 15.4492 5.57617 15.0195 5.57617C14.6094 5.57617 14.2676 5.9082 14.2676 6.32812Z", fillAlpha = 0.425f)
            addSfPath("M24.6191 10.0488C26.9434 10.0488 28.8574 8.13477 28.8574 5.81055C28.8574 3.48633 26.9434 1.57227 24.6191 1.57227C22.2949 1.57227 20.3809 3.48633 20.3809 5.81055C20.3809 8.13477 22.2949 10.0488 24.6191 10.0488Z", fillAlpha = 0.85f)
        }
        return _sFClockBadgeFill!!
    }

private var _sFClockBadgeFill: ImageVector? = null
