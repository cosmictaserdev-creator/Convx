/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.convxy.music.R
import com.convxy.music.WelcomeActivity

/**
 * The launcher icon variants shipped with the app.
 *
 * Each one is an `<activity-alias>` in the manifest aliasing `WelcomeActivity`; exactly one
 * alias is enabled at a time, and the enabled one is what the launcher draws. Component
 * enablement is stored by the system, so it survives reinstall-free updates on its own — the
 * DataStore copy exists only so the picker can show a checkmark without querying PackageManager
 * thirteen times per frame.
 *
 * [previewRes] is the adaptive icon's *foreground* layer; the picker composes it over
 * [R.color.ic_launcher_tile] to reproduce what the launcher shows.
 *
 * The default (WHITE in the enum, labelled "Color") is the full-colour Convxy mark —
 * the same adaptive icon as `@mipmap/ic_launcher`. The alias is still called
 * `.LauncherWhite` so upgrades keep the enabled component and just swap the asset.
 */
enum class AppIcon(
    val id: String,
    val alias: String,
    @DrawableRes val previewRes: Int,
    @StringRes val labelRes: Int,
) {
    WHITE("white", ".LauncherWhite", R.mipmap.ic_launcher_foreground, R.string.app_icon_color),
    AZURE("azure", ".LauncherAzure", R.mipmap.ic_launcher_azure_fg, R.string.app_icon_azure),
    SKY("sky", ".LauncherSky", R.mipmap.ic_launcher_sky_fg, R.string.app_icon_sky),
    TEAL("teal", ".LauncherTeal", R.mipmap.ic_launcher_teal_fg, R.string.app_icon_teal),
    SLATE("slate", ".LauncherSlate", R.mipmap.ic_launcher_slate_fg, R.string.app_icon_slate),
    SILVER("silver", ".LauncherSilver", R.mipmap.ic_launcher_silver_fg, R.string.app_icon_silver),
    PERIWINKLE(
        "periwinkle",
        ".LauncherPeriwinkle",
        R.mipmap.ic_launcher_periwinkle_fg,
        R.string.app_icon_periwinkle,
    ),
    VIOLET("violet", ".LauncherViolet", R.mipmap.ic_launcher_violet_fg, R.string.app_icon_violet),
    MIDNIGHT(
        "midnight",
        ".LauncherMidnight",
        R.mipmap.ic_launcher_midnight_fg,
        R.string.app_icon_midnight,
    ),
    MAUVE("mauve", ".LauncherMauve", R.mipmap.ic_launcher_mauve_fg, R.string.app_icon_mauve),
    ROSE("rose", ".LauncherRose", R.mipmap.ic_launcher_rose_fg, R.string.app_icon_rose),
    COPPER("copper", ".LauncherCopper", R.mipmap.ic_launcher_copper_fg, R.string.app_icon_copper),
    RUST("rust", ".LauncherRust", R.mipmap.ic_launcher_rust_fg, R.string.app_icon_rust),
    ;

    companion object {
        val DEFAULT = WHITE

        fun fromId(id: String?): AppIcon = entries.firstOrNull { it.id == id } ?: DEFAULT

        /**
         * Enables [icon]'s alias and disables every other one.
         *
         * The new alias is enabled *first* so there is never an instant with no enabled
         * LAUNCHER component — a gap there makes the app vanish from the launcher until the
         * next package scan. [PackageManager.DONT_KILL_APP] keeps playback alive; the launcher
         * usually redraws within a second or two on its own.
         */
        fun apply(context: Context, icon: AppIcon) {
            val pm = context.packageManager
            pm.setComponentEnabledSetting(
                componentFor(context, icon),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP,
            )
            entries.filter { it != icon }.forEach {
                pm.setComponentEnabledSetting(
                    componentFor(context, it),
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP,
                )
            }
        }

        /**
         * The manifest's relative alias names resolve against the module's *namespace*, not the
         * applicationId — and debug builds carry an `applicationIdSuffix`, so the two differ.
         * Deriving the prefix from a real class keeps both variants pointing at the right
         * component instead of silently no-op'ing on debug.
         */
        private fun componentFor(context: Context, icon: AppIcon): ComponentName {
            val namespace = WelcomeActivity::class.java.name.substringBeforeLast('.')
            return ComponentName(context.packageName, namespace + icon.alias)
        }
    }
}
