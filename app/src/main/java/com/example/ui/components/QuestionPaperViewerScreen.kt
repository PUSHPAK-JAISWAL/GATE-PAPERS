package com.example.ui.components

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaperEntity
import com.example.data.model.fileSizeText
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SolidAmber
import com.example.ui.theme.SolidEmerald
import com.example.ui.theme.SolidGateCyan
import com.example.ui.theme.SolidGateOrange
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.PdfManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun QuestionPaperViewerScreen(
    paper: PaperEntity,
    onBack: () -> Unit,
    onToggleFinished: () -> Unit,
    onTogglePending: () -> Unit,
    onSaveNotes: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isCS = paper.section == PaperEntity.SECTION_CS
    val sectionAccentColor = if (isCS) SolidGateOrange else SolidGateCyan
    val isFinished = paper.status == PaperEntity.STATUS_FINISHED
    val isPending = paper.status == PaperEntity.STATUS_PENDING || paper.isFlaggedToRevisit

    var showNotesEditor by remember { mutableStateOf(false) }
    var notesText by remember(paper.notes) { mutableStateOf(paper.notes) }

    // Full Screen Mode state
    var isFullScreen by remember { mutableStateOf(false) }

    // PDF Download & Render State
    var pdfFile by remember { mutableStateOf<File?>(null) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var downloadError by remember { mutableStateOf<String?>(null) }
    var pageCount by remember { mutableIntStateOf(0) }

    val listState = rememberLazyListState()

    // Global zoom state for full screen viewer
    var zoomScale by remember { mutableFloatStateOf(1f) }
    var zoomOffset by remember { mutableStateOf(Offset.Zero) }

    // Load or download the real PDF
    val loadPdf = {
        scope.launch {
            isDownloading = true
            downloadError = null
            val file = PdfManager.downloadOrGetCachedPdf(
                context = context,
                rawUrl = paper.rawFileUrl,
                fileName = paper.githubFileName,
                onProgress = { p -> downloadProgress = p }
            )
            isDownloading = false
            if (file != null && file.exists()) {
                pdfFile = file
                val pages = withContext(Dispatchers.IO) { PdfManager.getPageCount(file) }
                pageCount = pages
            } else {
                downloadError = "Could not download PDF from GitHub. Check connection or try external app."
            }
        }
    }

    LaunchedEffect(paper.id) {
        loadPdf()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            if (isFullScreen) {
                // Sleek Fullscreen Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xE60D1520))
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        IconButton(
                            onClick = { isFullScreen = false },
                            modifier = Modifier.size(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FullscreenExit,
                                contentDescription = "Exit Fullscreen",
                                tint = TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = paper.githubFileName.ifEmpty { paper.title },
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (pageCount > 0) {
                                Text(
                                    text = "$pageCount Pages • Pinch to Zoom",
                                    color = sectionAccentColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Zoom and utility controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                zoomScale = (zoomScale - 0.25f).coerceAtLeast(1f)
                                if (zoomScale == 1f) zoomOffset = Offset.Zero
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomOut,
                                contentDescription = "Zoom Out",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                zoomScale = (zoomScale + 0.35f).coerceAtMost(3.5f)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomIn,
                                contentDescription = "Zoom In",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val current = pdfFile
                                if (current != null && current.exists()) {
                                    PdfManager.printPdfDocument(context, current, paper.title)
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = "Print",
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                val current = pdfFile
                                if (current != null && current.exists()) {
                                    PdfManager.openWithExternalApp(context, current)
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Open in PDF App",
                                tint = TextPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            } else {
                // Normal Mode Top Bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurface)
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .size(38.dp)
                                    .testTag("viewer_back_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = TextPrimary
                                )
                            }

                            Column(modifier = Modifier.padding(start = 4.dp)) {
                                Text(
                                    text = paper.githubFileName.ifEmpty { paper.title },
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${paper.section} • ${paper.fileSizeText}",
                                    color = sectionAccentColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                            }
                        }

                        // Top Action Icons: Fullscreen, Print, External App
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            // Full screen toggle button
                            IconButton(
                                onClick = {
                                    isFullScreen = true
                                    zoomScale = 1f
                                    zoomOffset = Offset.Zero
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("viewer_fullscreen_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = "Full Screen Reader",
                                    tint = sectionAccentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val current = pdfFile
                                    if (current != null && current.exists()) {
                                        PdfManager.printPdfDocument(context, current, paper.title)
                                    } else {
                                        Toast.makeText(context, "Downloading PDF first...", Toast.LENGTH_SHORT).show()
                                        loadPdf()
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("viewer_print_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Print,
                                    contentDescription = "Print Document",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val current = pdfFile
                                    if (current != null && current.exists()) {
                                        PdfManager.openWithExternalApp(context, current)
                                    } else {
                                        Toast.makeText(context, "Downloading PDF first...", Toast.LENGTH_SHORT).show()
                                        loadPdf()
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("viewer_open_external_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = "Open in PDF App",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (!isFullScreen) {
                // Bottom Action Bar: Print Paper & Download
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurface)
                        .border(width = 1.dp, color = DarkBorder)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                val current = pdfFile
                                if (current != null && current.exists()) {
                                    PdfManager.printPdfDocument(context, current, paper.title)
                                } else {
                                    Toast.makeText(context, "Please wait, PDF is downloading...", Toast.LENGTH_SHORT).show()
                                    loadPdf()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 46.dp)
                                .testTag("viewer_bottom_print_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = sectionAccentColor,
                                contentColor = if (isCS) Color.White else Color(0xFF0A1E29)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Print Paper",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val current = pdfFile
                                if (current != null && current.exists()) {
                                    scope.launch {
                                        val saved = PdfManager.savePdfToPublicDownloads(context, current, paper.githubFileName)
                                        if (saved) {
                                            Toast.makeText(context, "Saved to Downloads/GATE_Papers", Toast.LENGTH_LONG).show()
                                        } else {
                                            PdfManager.openWithExternalApp(context, current)
                                        }
                                    }
                                } else {
                                    loadPdf()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = 46.dp)
                                .testTag("viewer_bottom_download_button"),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TextPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Save PDF",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = if (isFullScreen) 4.dp else 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!isFullScreen) {
                item {
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // --- Compact Preparation Progress Tracker Card ---
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f, fill = false)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                when {
                                                    isFinished -> SolidEmerald
                                                    isPending -> SolidAmber
                                                    else -> TextMuted
                                                },
                                                CircleShape
                                            )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = when {
                                            isFinished -> "Finished ✓"
                                            isPending -> "Pending Revisit ⏳"
                                            else -> "Not Yet Attempted"
                                        },
                                        color = when {
                                            isFinished -> SolidEmerald
                                            isPending -> SolidAmber
                                            else -> TextSecondary
                                        },
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }

                                IconButton(
                                    onClick = { showNotesEditor = !showNotesEditor },
                                    modifier = Modifier
                                        .size(32.dp)
                                        .background(DarkSurfaceVariant, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.EditNote,
                                        contentDescription = "Study Notes",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Two distinct progress toggle buttons (spacious, never clipped)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // "Finished" Button
                                Button(
                                    onClick = onToggleFinished,
                                    modifier = Modifier
                                        .weight(1f)
                                        .defaultMinSize(minHeight = 44.dp)
                                        .testTag("viewer_mark_finished_btn"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isFinished) SolidEmerald else DarkSurfaceVariant,
                                        contentColor = if (isFinished) Color.White else TextSecondary
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isFinished) "Finished ✓" else "Mark Finished",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }

                                // "Keep Pending" Button
                                Button(
                                    onClick = onTogglePending,
                                    modifier = Modifier
                                        .weight(1f)
                                        .defaultMinSize(minHeight = 44.dp)
                                        .testTag("viewer_mark_pending_btn"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isPending) SolidAmber else DarkSurfaceVariant,
                                        contentColor = if (isPending) Color(0xFF2A1B02) else TextSecondary
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HourglassTop,
                                        contentDescription = null,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isPending) "Pending ⏳" else "Keep Pending",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }

                            // Notes expandable area
                            AnimatedVisibility(visible = showNotesEditor || paper.notes.isNotEmpty()) {
                                Column(modifier = Modifier.padding(top = 10.dp)) {
                                    Text(
                                        text = "Study Notes & Revision Doubts:",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    OutlinedTextField(
                                        value = notesText,
                                        onValueChange = {
                                            notesText = it
                                            onSaveNotes(it)
                                        },
                                        placeholder = {
                                            Text(
                                                "e.g. Need to revise questions 15-20 on Algorithms...",
                                                fontSize = 11.sp,
                                                color = TextMuted
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("notes_input_field"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = sectionAccentColor,
                                            unfocusedBorderColor = DarkBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // --- GitHub Repo Source Info Card with un-squashed Page Count badge ---
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                Text(
                                    text = paper.githubFileName,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "github.com/${paper.githubRepo}",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            if (pdfFile != null && pageCount > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .background(sectionAccentColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "$pageCount Pages",
                                        color = sectionAccentColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                // --- Fullscreen Reader Trigger Banner ---
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .border(1.dp, sectionAccentColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .clickable {
                                isFullScreen = true
                                zoomScale = 1f
                                zoomOffset = Offset.Zero
                            }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = null,
                                    tint = sectionAccentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Full Screen Reader Mode",
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Distraction-free edge-to-edge view with pinch zoom",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .background(sectionAccentColor, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "OPEN",
                                    color = if (isCS) Color.White else Color(0xFF0A1E29),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // --- Downloading indicator if active ---
            if (isDownloading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(
                                color = sectionAccentColor,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "Fetching PDF from GitHub repository...",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (downloadProgress > 0f) {
                                LinearProgressIndicator(
                                    progress = { downloadProgress },
                                    modifier = Modifier
                                        .fillMaxWidth(0.8f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = sectionAccentColor,
                                    trackColor = DarkSurfaceVariant
                                )
                                Text(
                                    text = "${(downloadProgress * 100).toInt()}% downloaded",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // --- Error State with Retry Button ---
            if (downloadError != null && !isDownloading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkBorder, RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = downloadError ?: "Download failed",
                                color = SolidAmber,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Button(
                                onClick = { loadPdf() },
                                colors = ButtonDefaults.buttonColors(containerColor = sectionAccentColor),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retry Download", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // --- PDF Rendered Pages ---
            val currentFile = pdfFile
            if (currentFile != null && currentFile.exists() && pageCount > 0) {
                if (!isFullScreen) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Official Paper Pages ($pageCount)",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "Tap page to view",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                items(count = pageCount) { pageIndex ->
                    ZoomablePdfPageCard(
                        pdfFile = currentFile,
                        pageIndex = pageIndex,
                        totalCount = pageCount,
                        isFullScreen = isFullScreen,
                        zoomScale = if (isFullScreen) zoomScale else 1f,
                        zoomOffset = if (isFullScreen) zoomOffset else Offset.Zero,
                        onTransform = { s, o ->
                            zoomScale = (zoomScale * s).coerceIn(1f, 4f)
                            zoomOffset += o
                        },
                        onOpenExternal = {
                            PdfManager.openWithExternalApp(context, currentFile)
                        }
                    )
                }
            } else if (!isDownloading && downloadError == null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(DarkSurface)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = sectionAccentColor,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "PDF is ready to view",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Button(
                                onClick = {
                                    currentFile?.let { PdfManager.openWithExternalApp(context, it) }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = sectionAccentColor),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Open in PDF Reader")
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(if (isFullScreen) 10.dp else 24.dp))
            }
        }
    }
}

@Composable
private fun ZoomablePdfPageCard(
    pdfFile: File,
    pageIndex: Int,
    totalCount: Int,
    isFullScreen: Boolean,
    zoomScale: Float,
    zoomOffset: Offset,
    onTransform: (scaleChange: Float, offsetChange: Offset) -> Unit,
    onOpenExternal: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pageBitmap by remember(pdfFile.path, pageIndex) { mutableStateOf<Bitmap?>(null) }
    var isLoadingPage by remember { mutableStateOf(true) }

    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        onTransform(zoomChange, panChange)
    }

    LaunchedEffect(pdfFile.path, pageIndex) {
        isLoadingPage = true
        val bmp = withContext(Dispatchers.IO) {
            // Render at higher resolution for crisp reading of equations and code
            PdfManager.renderPageToBitmap(pdfFile, pageIndex, targetWidth = 1440)
        }
        pageBitmap = bmp
        isLoadingPage = false
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = !isFullScreen, onClick = onOpenExternal),
        shape = RoundedCornerShape(if (isFullScreen) 0.dp else 10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = if (isFullScreen) null else androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column {
            // Page Header strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Page ${pageIndex + 1} of $totalCount",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isFullScreen) "Pinch to zoom" else "Tap for external reader",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }
            }

            // Rendered Image with optional pinch-to-zoom
            val bmp = pageBitmap
            if (bmp != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(0.dp))
                        .then(
                            if (isFullScreen) {
                                Modifier.transformable(state = transformState)
                            } else {
                                Modifier
                            }
                        )
                ) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = "Question Paper Page ${pageIndex + 1}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer(
                                scaleX = zoomScale,
                                scaleY = zoomScale,
                                translationX = zoomOffset.x,
                                translationY = zoomOffset.y
                            ),
                        contentScale = ContentScale.FillWidth
                    )
                }
            } else if (isLoadingPage) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = SolidGateOrange,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color.White)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Unable to render page preview. Tap to open in PDF reader app.",
                        color = Color.DarkGray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
