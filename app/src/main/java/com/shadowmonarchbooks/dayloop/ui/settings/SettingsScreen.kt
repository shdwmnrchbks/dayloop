package com.shadowmonarchbooks.dayloop.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.shadowmonarchbooks.dayloop.data.formatDate
import com.shadowmonarchbooks.dayloop.ui.DayloopViewModel
import com.shadowmonarchbooks.dayloop.ui.ProfileUi
import com.shadowmonarchbooks.dayloop.ui.components.EmptyState

/**
 * Settings (docs/PLAN.md §5): advance/reroll/reset the in-game clock, manage
 * per-pack profiles (§3.7), and review orphaned marks (§3.6) instead of
 * dropping them silently.
 */
@Composable
fun SettingsScreen(vm: DayloopViewModel = hiltViewModel()) {
    val state by vm.state.collectAsState()
    val pack = state.selected ?: run {
        EmptyState("No pack selected.")
        return
    }

    var resetConfirmOpen by remember { mutableStateOf(false) }
    var deleteTarget by remember { mutableStateOf<ProfileUi?>(null) }
    var renameTarget by remember { mutableStateOf<ProfileUi?>(null) }
    var createOpen by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        // ---- In-game clock ----
        SectionTitle("In-game clock")
        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                state.activeProfile?.let { profile ->
                    Text(profile.name, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                }
                Text(
                    text = state.currentDate?.let { formatDate(it) } ?: "—",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(onClick = vm::endDay, enabled = state.hasNextDay()) {
                        Text("Advance a day")
                    }
                    OutlinedButton(onClick = vm::rerollDay, enabled = state.hasPreviousDay()) {
                        Text("Reroll")
                    }
                }
                OutlinedButton(onClick = { resetConfirmOpen = true }) {
                    Text("Reset profile")
                }
                if (state.activeProfile != null && state.activeProfile!!.contentVersion != pack.pack.contentVersion) {
                    Text(
                        text = "Pack content was updated after this save was made.",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                }
            }
        }

        // ---- Profiles ----
        SectionTitle("Profiles")
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            state.profiles.forEach { profile ->
                val active = profile.id == state.activeProfile?.id
                Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 4.dp, end = 4.dp)) {
                        RadioButton(selected = active, onClick = { vm.switchProfile(profile.id) })
                        Column(modifier = Modifier.weight(1f)) {
                            Text(profile.name, style = MaterialTheme.typography.bodyLarge, fontWeight = if (active) FontWeight.SemiBold else null)
                            Text(
                                text = "Day ${formatDate(profile.clockDate)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        IconButton(onClick = { renameTarget = profile }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Rename ${profile.name}")
                        }
                        IconButton(
                            onClick = { deleteTarget = profile },
                            enabled = state.profiles.size > 1,
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete ${profile.name}")
                        }
                    }
                }
            }
            TextButton(onClick = { createOpen = true }) {
                Text("+ New profile")
            }
        }

        // ---- Orphaned marks review (docs/PLAN.md §3.6) ----
        if (state.orphans.isNotEmpty()) {
            SectionTitle("Saved marks no longer in content")
            Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.errorContainer, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${state.orphans.size} saved mark(s) point at steps that no longer exist in this pack — content was edited after they were saved. Nothing was dropped; discard them once reviewed.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                    state.orphans.sortedWith(compareBy({ it.date }, { it.index })).take(8).forEach { key ->
                        Text(
                            text = "${formatDate(key.date)} · step ${key.index + 1}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                    if (state.orphans.size > 8) {
                        Text(
                            text = "…and ${state.orphans.size - 8} more",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                    OutlinedButton(onClick = vm::discardOrphans) {
                        Text("Discard these marks")
                    }
                }
            }
        }

        // ---- About this pack's save stamp (docs/PLAN.md §3.6) ----
        Text(
            text = "${pack.pack.title} · save stamp ${pack.pack.packId} @ content v${pack.pack.contentVersion}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    if (resetConfirmOpen) {
        ConfirmDialog(
            title = "Reset profile?",
            body = "All checkboxes and the clock return to the start of ${pack.pack.title}. The profile itself is kept.",
            confirmLabel = "Reset",
            onConfirm = {
                vm.resetProfile()
                resetConfirmOpen = false
            },
            onDismiss = { resetConfirmOpen = false },
        )
    }

    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "Delete ${target.name}?",
            body = "Its saved marks and clock position are removed. This cannot be undone.",
            confirmLabel = "Delete",
            onConfirm = {
                vm.deleteProfile(target.id)
                deleteTarget = null
            },
            onDismiss = { deleteTarget = null },
        )
    }

    renameTarget?.let { target ->
        NameDialog(
            title = "Rename profile",
            initial = target.name,
            confirmLabel = "Rename",
            onConfirm = { name ->
                vm.renameProfile(target.id, name)
                renameTarget = null
            },
            onDismiss = { renameTarget = null },
        )
    }

    if (createOpen) {
        NameDialog(
            title = "New profile",
            initial = "Profile ${state.profiles.size + 1}",
            confirmLabel = "Create",
            onConfirm = { name ->
                vm.createProfile(name)
                createOpen = false
            },
            onDismiss = { createOpen = false },
        )
    }
}

@Composable
private fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text = text, style = MaterialTheme.typography.titleMedium, modifier = modifier)
}

@Composable
private fun ConfirmDialog(
    title: String,
    body: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(body) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun NameDialog(
    title: String,
    initial: String,
    confirmLabel: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                label = { Text("Name") },
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name.trim()) }) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
