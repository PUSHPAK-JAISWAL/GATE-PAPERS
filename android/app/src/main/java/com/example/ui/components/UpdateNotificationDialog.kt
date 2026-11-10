package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.remote.AppReleaseInfo
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SolidGateOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.InAppUpdateDownloader
import com.example.util.UpdateDownloadState
import com.example.util.VersionUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun UpdateNotificationDialog(
    currentVersion: String,
    releaseInfo: AppReleaseInfo,
    onUpdateNow: () -> Unit,
    onLater: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var downloadJob by remember { mutableStateOf<Job?>(null) }
    var downloadState by remember { mutableStateOf<UpdateDownloadState>(UpdateDownloadState.Idle) }

    val normalizedCurrent = VersionUtil.normalize(currentVersion)
    val normalizedRelease = VersionUtil.normalize(releaseInfo.versionName)

    fun startInAppDownload() {
        downloadJob?.cancel()
        downloadState = UpdateDownloadState.Downloading(progressPercent = 0, bytesDownloaded = 0L, totalBytes = 0L)
        downloadJob = coroutineScope.launch {
            val result = InAppUpdateDownloader.downloadApk(
                context = context,
                downloadUrl = releaseInfo.apkDownloadUrl
            ) { percent, downloaded, total ->
                downloadState = UpdateDownloadState.Downloading(
                    progressPercent = percent,
                    bytesDownloaded = downloaded,
                    totalBytes = total
                )
            }

            result.fold(
                onSuccess = { apkFile ->
                    val validation = InAppUpdateDownloader.validateDownloadedApk(context, apkFile)
                    if (validation.hasSignatureConflict) {
                        downloadState = UpdateDownloadState.SignatureConflict(
                            apkFile = apkFile,
                            message = validation.reason ?: "The downloaded release was signed with a different key than this preview build."
                        )
                    } else if (InAppUpdateDownloader.canInstallApk(context)) {
                        downloadState = UpdateDownloadState.ReadyToInstall(apkFile)
                        val launched = InAppUpdateDownloader.launchInstaller(context, apkFile)
                        if (!launched) {
                            Toast.makeText(context, "Tap 'Install Now' to finish update", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        downloadState = UpdateDownloadState.NeedsPermission(apkFile)
                    }
                },
                onFailure = { error ->
                    downloadState = UpdateDownloadState.Failed(error.message ?: "Failed to download update")
                }
            )
        }
    }

    Dialog(
        onDismissRequest = {
            downloadJob?.cancel()
            onLater()
        },
        properties = DialogProperties(
            dismissOnBackPress = downloadState !is UpdateDownloadState.Downloading,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, DarkBorder, RoundedCornerShape(24.dp))
                .testTag("update_notification_dialog"),
            color = DarkSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Icon Badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(SolidGateOrange.copy(alpha = 0.15f))
                        .border(1.5.dp, SolidGateOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (downloadState) {
                            is UpdateDownloadState.ReadyToInstall -> Icons.Default.CheckCircle
                            is UpdateDownloadState.SignatureConflict -> Icons.Default.Warning
                            is UpdateDownloadState.Failed -> Icons.Default.ErrorOutline
                            is UpdateDownloadState.NeedsPermission -> Icons.Default.Security
                            else -> Icons.Default.Download
                        },
                        contentDescription = "Update Icon",
                        tint = when (downloadState) {
                            is UpdateDownloadState.SignatureConflict -> Color(0xFFF59E0B)
                            else -> SolidGateOrange
                        },
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title & Subtitle
                Text(
                    text = when (downloadState) {
                        is UpdateDownloadState.Downloading -> "Downloading Update..."
                        is UpdateDownloadState.ReadyToInstall -> "Ready to Install"
                        is UpdateDownloadState.NeedsPermission -> "Permission Needed"
                        is UpdateDownloadState.SignatureConflict -> "Signature Conflict Detected"
                        is UpdateDownloadState.Failed -> "Download Incomplete"
                        else -> "New Update Available!"
                    },
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Version comparison chips
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .background(DarkSurfaceVariant, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Current: v$normalizedCurrent",
                            fontSize = 11.sp,
                            color = TextMuted,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "➔", color = SolidGateOrange, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .background(SolidGateOrange.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .border(1.dp, SolidGateOrange.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "New: v$normalizedRelease",
                            fontSize = 11.sp,
                            color = SolidGateOrange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Content Area based on Download State
                when (val state = downloadState) {
                    is UpdateDownloadState.Downloading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkBackground)
                                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "In-App Background Download",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = if (state.progressPercent >= 0) "${state.progressPercent}%" else "...",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SolidGateOrange
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                if (state.progressPercent >= 0) {
                                    LinearProgressIndicator(
                                        progress = { state.progressPercent / 100f },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = SolidGateOrange,
                                        trackColor = DarkSurfaceVariant
                                    )
                                } else {
                                    LinearProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        color = SolidGateOrange,
                                        trackColor = DarkSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                val downloadedMb = String.format("%.1f", state.bytesDownloaded / (1024.0 * 1024.0))
                                val totalMb = if (state.totalBytes > 0) {
                                    String.format("%.1f", state.totalBytes / (1024.0 * 1024.0))
                                } else {
                                    "~30.0"
                                }

                                Text(
                                    text = "$downloadedMb MB / $totalMb MB • Overwrites previous APK directly",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                        }
                    }

                    is UpdateDownloadState.ReadyToInstall -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkBackground)
                                .border(1.dp, SolidGateOrange.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Update downloaded successfully!",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4ADE80)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tap 'Install Now' below to open the Android installer prompt. Your existing bookmarks and solved status are safely preserved.",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    is UpdateDownloadState.NeedsPermission -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkBackground)
                                .border(1.dp, Color(0xFFFBBF24).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Install Permission Required",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFBBF24)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "To install updates seamlessly in-app, please allow 'Install unknown apps' for GATE Papers in Android settings, then tap Install.",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    is UpdateDownloadState.Failed -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkBackground)
                                .border(1.dp, Color(0xFFEF4444).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Download encountered an error",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFEF4444)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = state.errorMessage,
                                    fontSize = 11.5.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    is UpdateDownloadState.SignatureConflict -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkBackground)
                                .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = Color(0xFFF59E0B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Why is this happening?",
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF59E0B)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Android protects your device by preventing updates when the downloaded release is signed with a different key than your current installed app.",
                                    fontSize = 11.5.sp,
                                    color = TextSecondary,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "To install cleanly:\n1. Tap 'Save to Downloads' below.\n2. Uninstall this current preview app from phone.\n3. Open Downloads and tap GATE-Papers.apk to install. Future updates will install seamlessly in-app!",
                                    fontSize = 11.sp,
                                    color = SolidGateOrange,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 15.sp
                                )
                                if (state.isCopiedToDownloads) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "✓ Saved to Downloads/GATE-Papers.apk",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4ADE80)
                                    )
                                }
                            }
                        }
                    }

                    UpdateDownloadState.Idle -> {
                        // Release notes / highlights box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkBackground)
                                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Text(
                                    text = if (releaseInfo.releaseTitle.isNotEmpty()) releaseInfo.releaseTitle else "What's New in this release:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                val displayNotes = if (releaseInfo.releaseNotes.isNotBlank()) {
                                    releaseInfo.releaseNotes
                                } else {
                                    "• Authentic GATE papers sync with zero clutter.\n• In-app update installation with automatic progress.\n• Clean semantic versioning (1-x.0-9.0-9)."
                                }

                                Text(
                                    text = displayNotes,
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    lineHeight = 17.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Footnote
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Installs directly in-app — no duplicate APKs in Downloads folder.",
                        fontSize = 10.5.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Dynamic Action Buttons
                when (val state = downloadState) {
                    is UpdateDownloadState.Downloading -> {
                        OutlinedButton(
                            onClick = {
                                downloadJob?.cancel()
                                downloadState = UpdateDownloadState.Idle
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                        ) {
                            Text(text = "Cancel Download", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    is UpdateDownloadState.ReadyToInstall -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onLater,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                            ) {
                                Text(text = "Close", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = {
                                    InAppUpdateDownloader.launchInstaller(context, state.apkFile)
                                },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SolidGateOrange,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Install Now", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    is UpdateDownloadState.NeedsPermission -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = onLater,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                            ) {
                                Text(text = "Later", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = {
                                    InAppUpdateDownloader.requestInstallPermission(context)
                                },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SolidGateOrange,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Allow & Install", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    is UpdateDownloadState.Failed -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    // Fallback to browser
                                    val targetUrl = releaseInfo.apkDownloadUrl.ifBlank { releaseInfo.releasePageUrl }
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                            ) {
                                Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Browser", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = { startInAppDownload() },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SolidGateOrange,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "Retry", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    is UpdateDownloadState.SignatureConflict -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val copied = InAppUpdateDownloader.copyApkToPublicDownloads(context, state.apkFile)
                                        if (copied) {
                                            downloadState = state.copy(isCopiedToDownloads = true)
                                            Toast.makeText(context, "Saved to Downloads/GATE-Papers.apk", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(context, "Failed to copy to Downloads", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .height(46.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SolidGateOrange,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.Download,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (state.isCopiedToDownloads) "Saved to Downloads ✓" else "Save to Downloads",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        InAppUpdateDownloader.launchInstaller(context, state.apkFile)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary)
                                ) {
                                    Text(text = "Try Install", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            OutlinedButton(
                                onClick = onLater,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextMuted)
                            ) {
                                Text(text = "Dismiss", fontSize = 12.sp)
                            }
                        }
                    }

                    UpdateDownloadState.Idle -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Later Button
                            OutlinedButton(
                                onClick = onLater,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(46.dp)
                                    .testTag("update_later_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = TextSecondary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = TextMuted
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Later",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Update Now Button (Initiates In-App Update)
                            Button(
                                onClick = { startInAppDownload() },
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(46.dp)
                                    .testTag("update_now_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SolidGateOrange,
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Update Now",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
