package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SolidAmber
import com.example.ui.theme.SolidEmerald
import com.example.ui.theme.SolidGateCyan
import com.example.ui.theme.SolidGateOrange
import com.example.ui.theme.SolidPurple
import com.example.ui.theme.SolidRose
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PapersUiState

@Composable
fun SettingsDialog(
    state: PapersUiState,
    onDismiss: () -> Unit,
    onCheckUpdates: () -> Unit,
    onOpenUpdatePrompt: () -> Unit,
    onSyncRepos: () -> Unit,
    onResetProgress: () -> Unit
) {
    val context = LocalContext.current
    var showResetConfirmation by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(24.dp))
                .testTag("settings_dialog_surface"),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Settings & Details",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "GATE Papers v${state.appVersionName}",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .background(DarkSurfaceVariant, CircleShape)
                            .testTag("settings_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Settings",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // SECTION 1: APP UPDATES & DEPENDABOT
                    SettingsCard(title = "App Updates & Releases", icon = Icons.Default.NewReleases, iconTint = SolidGateOrange) {
                        // Current version badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Installed Version",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = "v${state.appVersionName} (Build ${state.appVersionCode})",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            if (state.isUpdateAvailable) {
                                Box(
                                    modifier = Modifier
                                        .background(SolidGateOrange.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                        .border(1.dp, SolidGateOrange, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Update Available",
                                        color = SolidGateOrange,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .background(SolidEmerald.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                        .border(1.dp, SolidEmerald.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = SolidEmerald,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Up to date",
                                            color = SolidEmerald,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // Pending update notification box (if user chose "Later" earlier)
                        if (state.isUpdateAvailable && state.updateInfo != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkBackground)
                                    .border(1.dp, SolidGateOrange.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Release ${state.updateInfo.tagName} is ready",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SolidGateOrange
                                        )
                                        Text(
                                            text = "Saved for later",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (state.updateInfo.releaseNotes.isNotBlank()) state.updateInfo.releaseNotes.take(160) + "..." else "New papers, official answers, and performance fixes.",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 15.sp
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = {
                                                launchExternalUrl(context, state.updateInfo.apkDownloadUrl)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = SolidGateOrange),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(36.dp).testTag("settings_update_now_btn")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Download,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Download APK Now",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        OutlinedButton(
                                            onClick = onOpenUpdatePrompt,
                                            shape = RoundedCornerShape(8.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                            modifier = Modifier.height(36.dp)
                                        ) {
                                            Text(
                                                text = "View Details",
                                                fontSize = 11.sp,
                                                color = TextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Check updates button & status (Spacious & Full Width)
                        Button(
                            onClick = onCheckUpdates,
                            enabled = !state.isCheckingUpdate,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkSurfaceVariant,
                                contentColor = TextPrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("check_updates_button")
                        ) {
                            if (state.isCheckingUpdate) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = SolidGateOrange,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text("Checking for updates...", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = SolidGateOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Check for Updates", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (state.updateStatusMessage != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.updateStatusMessage,
                                fontSize = 11.5.sp,
                                color = TextMuted
                            )
                        }
                    }

                    // SECTION 2: DEVELOPER & PROJECT DETAILS
                    SettingsCard(title = "Developer & Project Details", icon = Icons.Default.Person, iconTint = SolidGateCyan) {
                        DetailItem(
                            icon = Icons.Default.Person,
                            label = "Author",
                            value = "Pushpak Jaiswal (@PUSHPAK-JAISWAL)",
                            isClickable = true,
                            onClick = { launchExternalUrl(context, "https://github.com/PUSHPAK-JAISWAL") }
                        )

                        DetailItem(
                            icon = Icons.Default.Email,
                            label = "Contact / Support",
                            value = "pushpakmjaiswal@gmail.com",
                            isClickable = true,
                            onClick = {
                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:pushpakmjaiswal@gmail.com?subject=GATE%20Papers%20App%20Feedback")
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                try {
                                    context.startActivity(emailIntent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "pushpakmjaiswal@gmail.com", Toast.LENGTH_LONG).show()
                                }
                            }
                        )

                        DetailItem(
                            icon = Icons.Default.Code,
                            label = "Main Repository",
                            value = "github.com/PUSHPAK-JAISWAL/gate-papers",
                            isClickable = true,
                            onClick = { launchExternalUrl(context, "https://github.com/PUSHPAK-JAISWAL/gate-papers") }
                        )

                        DetailItem(
                            icon = Icons.Default.Language,
                            label = "Showcase & APK Website",
                            value = "pushpak-jaiswal.github.io/gate-papers",
                            isClickable = true,
                            onClick = { launchExternalUrl(context, "https://pushpak-jaiswal.github.io/gate-papers/") }
                        )

                        DetailItem(
                            icon = Icons.Default.Security,
                            label = "Dependabot & Maintenance",
                            value = "Active: Weekly auto-updates for Gradle, Actions & Web",
                            isClickable = false
                        )
                    }

                    // SECTION 3: DATA & PROGRESS MANAGEMENT
                    SettingsCard(title = "Data & Preparation Tracker", icon = Icons.Default.Sync, iconTint = SolidPurple) {
                        // Cached papers status
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarkSurfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Offline Cached Papers",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                            Text(
                                text = "${state.papers.size} papers ready offline",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sync Repositories Button (Clear & Spacious)
                        OutlinedButton(
                            onClick = onSyncRepos,
                            enabled = !state.isSyncing,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = DarkSurfaceVariant.copy(alpha = 0.4f),
                                contentColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("settings_sync_repos_btn")
                        ) {
                            if (state.isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = SolidEmerald,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Syncing with GitHub...", fontSize = 12.5.sp, color = TextPrimary)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = SolidEmerald
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sync Question Papers from Repos", fontSize = 12.5.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Reset Progress Button (Clear & Spacious)
                        Button(
                            onClick = { showResetConfirmation = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SolidRose.copy(alpha = 0.12f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SolidRose.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("reset_progress_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = null,
                                tint = SolidRose,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reset Solved Preparation Progress",
                                color = SolidRose,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Confirmation dialog before resetting preparation progress
    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            containerColor = DarkSurface,
            title = {
                Text(
                    text = "Reset All Progress?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "This will mark all GATE CS and GATE DA papers as unattempted and clear your revisit flags. Notes will be preserved.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetProgress()
                        showResetConfirmation = false
                        Toast.makeText(context, "Progress reset successfully", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SolidRose)
                ) {
                    Text("Confirm Reset", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmation = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }
}

@Composable
private fun SettingsCard(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkBackground)
            .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(iconTint.copy(alpha = 0.15f), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            content()
        }
    }
}

@Composable
private fun DetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    isClickable: Boolean,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isClickable) Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onClick)
                    .padding(vertical = 6.dp, horizontal = 4.dp)
                else Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 11.sp, color = TextMuted)
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = if (isClickable) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isClickable) SolidGateCyan else TextSecondary
            )
        }
        if (isClickable) {
            Text(text = "↗", color = SolidGateCyan, fontSize = 12.sp)
        }
    }
}

private fun launchExternalUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open link: $url", Toast.LENGTH_SHORT).show()
    }
}
