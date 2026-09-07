/**
 * Convx Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */
package com.convxy.music.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.convxy.music.R

/**
 * One sort option: the value to select and the string shown for it.
 */
data class SortOption<T>(val value: T, val labelRes: Int)

/**
 * Compact sort control: an icon that opens a checklist of sort fields, a rule, and a
 * descending toggle at the bottom.
 *
 * The alternative already in the codebase, [SortHeader], is a Material split button
 * roughly 160dp wide that takes a row of its own above every list. This is a single icon that fits in a header alongside other
 * actions, which is what lets the list start at the top of the screen instead of below
 * a control strip.
 *
 * Selection is shown with a tick on the chosen row rather than by changing the button
 * label, so the current sort is legible without opening anything only if the caller
 * also shows it — that is the trade a compact control makes, and why this is offered
 * alongside [SortHeader] rather than replacing it everywhere.
 */
@Composable
fun <T> SortPopupButton(
    options: List<SortOption<T>>,
    selected: T,
    descending: Boolean,
    onSelectedChange: (T) -> Unit,
    onDescendingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    /** Extra rows above the sort fields — grid density, view style, whatever the screen has. */
    leadingSection: (@Composable ColumnScope.() -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                painter = painterResource(R.drawable.tune),
                contentDescription = stringResource(R.string.sort_by),
                modifier = Modifier.size(24.dp),
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            if (leadingSection != null) {
                leadingSection()
                HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
            }

            options.forEach { option ->
                SortMenuRow(
                    label = stringResource(option.labelRes),
                    checked = option.value == selected,
                    onClick = {
                        onSelectedChange(option.value)
                        expanded = false
                    },
                )
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))

            SortMenuRow(
                label = stringResource(R.string.sort_descending),
                checked = descending,
                // Deliberately does NOT dismiss: direction is the one option people flip
                // back and forth to compare, and reopening the menu for each flip is the
                // kind of friction that makes a sort control feel heavy.
                onClick = { onDescendingChange(!descending) },
                trailingIcon = R.drawable.arrow_downward,
                trailingRotated = !descending,
            )
        }
    }
}

@Composable
private fun SortMenuRow(
    label: String,
    checked: Boolean,
    onClick: () -> Unit,
    trailingIcon: Int? = null,
    trailingRotated: Boolean = false,
) {
    DropdownMenuItem(
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (checked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.width(24.dp))
            }
        },
        trailingIcon = {
            when {
                trailingIcon != null -> Icon(
                    painter = painterResource(trailingIcon),
                    contentDescription = null,
                    tint = if (checked) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier
                        .size(18.dp)
                        .graphicsLayer { rotationZ = if (trailingRotated) 180f else 0f },
                )

                checked -> Icon(
                    painter = painterResource(R.drawable.check),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
            }
        },
        onClick = onClick,
    )
}
