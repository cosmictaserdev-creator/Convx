/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convx.music.widget

import android.graphics.Color
import android.os.Build

/**
 * Samsung One UI Home's native wallpaper blur for third-party widgets, the frosted glass behind Samsung's
 * own Weather and Clock widgets.
 *
 * The widget never blurs anything itself. It opts in with two Samsung attributes in its provider XML
 * (`app:widgetStyle="colorful"` and `app:widgetSize`) and gives the root view, the one tagged
 * `@android:id/background`, a translucent background. The launcher then draws the blurred wallpaper under
 * that view and the translucent colour tints it. Technique from the MIT-licensed
 * thatjoshguy67/blur-widget-demo.
 */
object OneUiBlur {
    /** One UI 7 is Android 15; older One UI builds ignore the opt-in. */
    val isSupported: Boolean
        get() = Build.MANUFACTURER.equals("samsung", ignoreCase = true) && Build.VERSION.SDK_INT >= 35

    /**
     * The tint over the blur. The alpha has to stay inside 1..254: fully opaque hides the blur and fully
     * transparent is read as "no background", and the launcher skips the effect in both cases.
     */
    fun tint(night: Boolean): Int = if (night) Color.argb(128, 0, 0, 0) else Color.argb(102, 255, 255, 255)
}
