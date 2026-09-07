/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.convxy.music.ui.component

import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File

/**
 * Every switch and value slider in the app is meant to reach the glass controls through an
 * import alias — `GlassSwitchCompat as Switch`, `GlassSlider as Slider` — which is how the
 * settings screens were converted without rewriting hundreds of call sites.
 *
 * The alias is invisible where it is used: the call site still reads `Switch(checked = …)`.
 * So a merge that restores `import androidx.compose.material3.Switch` reverts a whole screen
 * to Material and nothing at the call site looks different. That is the same class of
 * regression as the settings toggle that quietly stopped taking the liquid path, and it is
 * not visible in a diff review either. These tests walk the sources instead.
 *
 * A file that calls one of these controls must either carry the alias or appear in the
 * exemption list below with the reason it has to stay Material. The lists are checked for
 * staleness too, so they cannot quietly accumulate entries that no longer apply.
 */
class GlassControlRoutingTest {

    /**
     * Switches that deliberately stay Material.
     *
     * - `WelcomeActivity` runs in its own Activity, before the app's backdrop exists, so a
     *   glass switch there has nothing to refract and would only be a painted approximation.
     */
    private val switchExemptions = mapOf(
        "WelcomeActivity.kt" to "onboarding Activity, no app backdrop to sample",
    )

    /**
     * Sliders that deliberately stay Material.
     *
     * The glass slider only implements Material's simple overload. Slot-based call sites
     * (custom `thumb`/`track`) cannot be aliased at all, and sites that pass `colors` are
     * declined by [glassSliderEligible], so aliasing them would change nothing.
     */
    private val sliderExemptions = mapOf(
        "ui/component/GlassSlider.kt" to "the Material fallback the glass slider delegates to",
        "ui/component/VolumeSlider.kt" to "custom thumb/track slots drawing the volume icons",
        "ui/component/ThumbnailCornerRadiusSelector.kt" to "passes colors, so it is declined anyway",
        "ui/player/Player.kt" to
            "slot-based seek/volume sliders plus the legacy style branches; a file-wide " +
            "alias would hijack all four call sites",
        "ui/screens/equalizer/axion/AxionEqScreen.kt" to
            "vertical EQ band built by swapping the measure constraints in a layout{} modifier",
        "ui/screens/youtube/YouTubeWatchScreen.kt" to "passes colors, so it is declined anyway",
    )

    @Test
    fun `every switch call site routes through the glass control`() {
        assertRouted("Switch", "GlassSwitchCompat as Switch", switchExemptions)
    }

    @Test
    fun `every slider call site routes through the glass control`() {
        assertRouted("Slider", "GlassSlider as Slider", sliderExemptions)
    }

    /**
     * Walks `src/main/kotlin` and reports every file that calls [control] without the alias
     * and without an exemption. Also fails when an exemption no longer matches reality, so
     * the lists stay honest instead of accumulating dead entries.
     */
    private fun assertRouted(control: String, alias: String, exemptions: Map<String, String>) {
        val found = sourceRoot()
        assumeTrue("app sources not found from ${File(".").absolutePath}", found != null)
        val root = found ?: return

        val call = Regex("(?<![\\w.])$control\\s*\\(")
        val declaration = Regex("^\\s*(?:private |internal |public )*fun\\s+$control\\s*\\(")

        val offenders = mutableListOf<String>()
        val callers = mutableSetOf<String>()

        root.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .forEach { file ->
                val relative = file.relativeTo(root).path.replace(File.separatorChar, '/')
                val text = file.readText()
                val calls = text.lineSequence().count { line ->
                    val trimmed = line.trim()
                    !trimmed.startsWith("*") &&
                        !trimmed.startsWith("//") &&
                        !declaration.containsMatchIn(line) &&
                        call.containsMatchIn(line)
                }
                if (calls == 0) return@forEach
                callers += relative
                if (!text.contains(alias) && !exemptions.containsKey(relative)) {
                    offenders += "$relative ($calls call site${if (calls == 1) "" else "s"})"
                }
            }

        val stale = exemptions.keys.filterNot { callers.contains(it) }

        assertTrue(
            buildString {
                if (offenders.isNotEmpty()) {
                    append("\n$control call sites that bypass the glass control — add ")
                    append("`import com.convxy.music.ui.component.$alias`, or list the file ")
                    append("with a reason if it must stay Material:\n  ")
                    append(offenders.joinToString("\n  "))
                }
                if (stale.isNotEmpty()) {
                    append("\nExemptions that no longer call $control (remove them): ")
                    append(stale.joinToString(", "))
                }
            },
            offenders.isEmpty() && stale.isEmpty(),
        )
    }

    /** The `com/convxy/music` package directory, found by walking up from the test's cwd. */
    private fun sourceRoot(): File? {
        var dir: File? = File(".").absoluteFile
        while (dir != null) {
            val candidate = File(dir, "src/main/kotlin/com/convxy/music")
            if (candidate.isDirectory) return candidate
            dir = dir.parentFile
        }
        return null
    }
}
