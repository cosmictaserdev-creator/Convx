/*
 * Copyright (c) 2023-2024. Compose Cupertino project and open source contributors.
 * Copyright (c) 2025. Scott Lanoue.
 * Copyright (c) 2026. IENGROUND of IENLAB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package zone.ien.hig.section

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

enum class SectionStyle(
    val inset: Boolean,
    val grouped: Boolean,
) {
    Sidebar(true, true),
    InsetGrouped(true, true),
    Grouped(false, true),
    Inset(true, false),
    Plain(false, false),
}

/**
 * Style of the [section]s and [CupertinoSection]s. Defaults to [SectionStyle.InsetGrouped].
 *
 * Can be used to provide the same style for all sections on page / in application.
 * */
val LocalSectionStyle =
    compositionLocalOf {
        SectionStyle.InsetGrouped
    }

@Composable
fun ProvideSectionStyle(
    style: SectionStyle,
    content: @Composable () -> Unit,
) = CompositionLocalProvider(LocalSectionStyle provides style, content = content)

/** Uppercases section titles for grouped styles. */
@Composable
fun String.sectionTitle(style: SectionStyle = LocalSectionStyle.current) = if (style.shouldCapsTitle) uppercase() else this

/** Applies the background color for this section style. */
fun Modifier.sectionContainerBackground(style: SectionStyle? = null) =
    composed {
        background(
            CupertinoSectionDefaults.containerColor(style ?: LocalSectionStyle.current),
        )
    }

internal val SectionStyle.shouldCapsTitle
    get() = this != SectionStyle.Sidebar && grouped

internal val SectionStyle.shouldFillContainer
    get() = !grouped
