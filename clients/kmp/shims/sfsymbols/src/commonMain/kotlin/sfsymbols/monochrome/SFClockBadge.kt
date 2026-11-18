package com.composables.sfsymbols.monochrome

import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.sfsymbols.SfSymbols
import com.composables.sfsymbols.addSfPath
import com.composables.sfsymbols.sfIcon

/**
 * SF Symbol: SFClockBadge (monochrome)
 * Viewport: 30.4199 x 29.0527
 */
public val SfSymbols.Monochrome.SFClockBadge: ImageVector
    get() {
        if (_sFClockBadge != null) {
            return _sFClockBadge!!
        }
        _sFClockBadge = sfIcon(
            name = "Monochrome.SFClockBadge",
            viewportWidth = 30.4199f,
            viewportHeight = 29.0527f
        ) {
            addSfPath("M19.7368 2.7087C19.407 3.21626 19.1593 3.78027 19.0128 4.38307C17.7825 3.88917 16.4374 3.62305 15.0293 3.62305C9.00391 3.62305 4.13086 8.49609 4.13086 14.5215C4.13086 20.5469 9.00391 25.4199 15.0293 25.4199C21.0547 25.4199 25.9277 20.5469 25.9277 14.5215C25.9277 13.4777 25.7815 12.4684 25.4995 11.5167C26.1167 11.4282 26.7016 11.2345 27.2366 10.9548C27.5763 12.0842 27.7539 13.2823 27.7539 14.5215C27.7539 21.543 22.0508 27.2363 15.0293 27.2363C8.00781 27.2363 2.31445 21.543 2.31445 14.5215C2.31445 7.5 8.00781 1.79688 15.0293 1.79688C16.6935 1.79688 18.2836 2.11724 19.7368 2.7087Z", fillAlpha = 0.85f)
            addSfPath("M15.7812 6.32812L15.7812 15.0391C15.7812 15.4688 15.4492 15.8008 15.0195 15.8008L8.27148 15.8008C7.8418 15.8008 7.51953 15.4688 7.51953 15.0391C7.51953 14.6191 7.8418 14.2871 8.27148 14.2871L14.2676 14.2871L14.2676 6.32812C14.2676 5.9082 14.6094 5.57617 15.0195 5.57617C15.4492 5.57617 15.7812 5.9082 15.7812 6.32812Z", fillAlpha = 0.85f)
            addSfPath("M24.6191 10.0488C26.9434 10.0488 28.8574 8.13477 28.8574 5.81055C28.8574 3.48633 26.9434 1.57227 24.6191 1.57227C22.2949 1.57227 20.3809 3.48633 20.3809 5.81055C20.3809 8.13477 22.2949 10.0488 24.6191 10.0488Z", fillAlpha = 0.85f)
        }
        return _sFClockBadge!!
    }

private var _sFClockBadge: ImageVector? = null
