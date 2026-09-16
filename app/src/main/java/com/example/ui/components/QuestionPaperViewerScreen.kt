package com.example.ui.components

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FitScreen
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
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.input.pointer.pointerInput
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

    if (isFullScreen && pdfFile != null && pageCount > 0) {
        FullScreenDocumentReader(
            paper = paper,
            pdfFile = pdfFile!!,
            pageCount = pageCount,
            sectionAccentColor = sectionAccentColor,
            isCS = isCS,
            onClose = { isFullScreen = false },
            onPrint = {
                pdfFile?.let { PdfManager.printPdfDocument(context, it, paper.title) }
            },
            onOpenExternal = {
                pdfFile?.let { PdfManager.openWithExternalApp(context, it) }
            }
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
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
        },
        bottomBar = {
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
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(sectionAccentColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = null,
                                    tint = sectionAccentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f, fill = false)) {
                                Text(
                                    text = "Full Screen Reader Mode",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Continuous document stream with zoom",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Spacious, horizontal Open Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(sectionAccentColor)
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Open",
                                    color = if (isCS) Color.White else Color(0xFF0A1E29),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = if (isCS) Color.White else Color(0xFF0A1E29),
                                    modifier = Modifier.size(16.dp)
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
                            text = "Tap any page for full screen",
                            color = sectionAccentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                items(count = pageCount) { pageIndex ->
                    NormalModePdfPageCard(
                        pdfFile = currentFile,
                        pageIndex = pageIndex,
                        totalCount = pageCount,
                        sectionAccentColor = sectionAccentColor,
                        onTap = { isFullScreen = true }
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
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * FullScreenDocumentReader renders the authentic PDF as a single, continuous,
 * cohesive document stream with whole-document pinch-to-zoom and panning.
 */
@Composable
private fun FullScreenDocumentReader(
    paper: PaperEntity,
    pdfFile: File,
    pageCount: Int,
    sectionAccentColor: Color,
    isCS: Boolean,
    onClose: () -> Unit,
    onPrint: () -> Unit,
    onOpenExternal: () -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var zoomScale by remember { mutableFloatStateOf(1f) }
    var zoomOffset by remember { mutableStateOf(Offset.Zero) }
    var showHud by remember { mutableStateOf(true) }

    val currentPage by remember {
        derivedStateOf {
            (listState.firstVisibleItemIndex + 1).coerceAtMost(pageCount)
        }
    }

    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (zoomScale * zoomChange).coerceIn(1f, 4f)
        zoomScale = newScale
        if (newScale > 1.05f) {
            zoomOffset += panChange
        } else {
            zoomOffset = Offset.Zero
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0F17))
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        if (zoomScale > 1.15f) {
                            zoomScale = 1f
                            zoomOffset = Offset.Zero
                        } else {
                            zoomScale = 2.2f
                        }
                    },
                    onTap = {
                        showHud = !showHud
                    }
                )
            }
            .transformable(
                state = transformState,
                lockRotationOnZoomPan = true
            )
    ) {
        // Continuous Whole Document View
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = zoomScale
                    scaleY = zoomScale
                    translationX = zoomOffset.x
                    translationY = if (zoomScale > 1.05f) zoomOffset.y else 0f
                },
            contentPadding = PaddingValues(
                top = if (showHud) 72.dp else 16.dp,
                bottom = if (showHud) 88.dp else 24.dp,
                start = 0.dp,
                end = 0.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(count = pageCount, key = { it }) { pageIndex ->
                DocumentPageCanvas(
                    pdfFile = pdfFile,
                    pageIndex = pageIndex,
                    sectionAccentColor = sectionAccentColor
                )
            }
        }

        // Top HUD Overlay
        AnimatedVisibility(
            visible = showHud,
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xEE0D1520))
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit Fullscreen",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = paper.githubFileName.ifEmpty { paper.title },
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Page $currentPage of $pageCount • Full Document",
                            color = sectionAccentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    if (zoomScale > 1.05f) {
                        IconButton(
                            onClick = {
                                zoomScale = 1f
                                zoomOffset = Offset.Zero
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitScreen,
                                contentDescription = "Fit to Width",
                                tint = sectionAccentColor,
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            zoomScale = (zoomScale - 0.35f).coerceAtLeast(1f)
                            if (zoomScale <= 1.05f) zoomOffset = Offset.Zero
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomOut,
                            contentDescription = "Zoom Out",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            zoomScale = (zoomScale + 0.35f).coerceAtMost(4f)
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomIn,
                            contentDescription = "Zoom In",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    IconButton(
                        onClick = onPrint,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "Print",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onOpenExternal,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Open in External PDF App",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Bottom HUD Overlay (Floating Page Navigation Pill)
        AnimatedVisibility(
            visible = showHud,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xEE1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(24.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                val prev = (currentPage - 2).coerceAtLeast(0)
                                listState.animateScrollToItem(prev)
                            }
                        },
                        enabled = currentPage > 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Page",
                            tint = if (currentPage > 1) Color.White else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Text(
                        text = "Page $currentPage of $pageCount",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    IconButton(
                        onClick = {
                            scope.launch {
                                val next = currentPage.coerceAtMost(pageCount - 1)
                                listState.animateScrollToItem(next)
                            }
                        },
                        enabled = currentPage < pageCount,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Page",
                            tint = if (currentPage < pageCount) Color.White else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Renders a single seamless PDF page inside the continuous full-screen document stream.
 * No individual card headers or bars break up the reading experience.
 */
@Composable
private fun DocumentPageCanvas(
    pdfFile: File,
    pageIndex: Int,
    sectionAccentColor: Color,
    modifier: Modifier = Modifier
) {
    var pageBitmap by remember(pdfFile.path, pageIndex) { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(pdfFile.path, pageIndex) {
        isLoading = true
        val bmp = withContext(Dispatchers.IO) {
            // Render at crisp 1440px width for mathematical formulas and code
            PdfManager.renderPageToBitmap(pdfFile, pageIndex, targetWidth = 1440)
        }
        pageBitmap = bmp
        isLoading = false
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        val bmp = pageBitmap
        if (bmp != null) {
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = "Document Page ${pageIndex + 1}",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
        } else if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f / 1.4142f)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(
                        color = sectionAccentColor,
                        modifier = Modifier.size(26.dp),
                        strokeWidth = 2.5.dp
                    )
                    Text(
                        text = "Loading Page ${pageIndex + 1}...",
                        color = Color.DarkGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f / 1.4142f)
                    .background(Color(0xFFF1F5F9))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Page ${pageIndex + 1} preview unavailable",
                    color = Color.DarkGray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * NormalModePdfPageCard shows a clean document preview card in the normal screen.
 * Tapping it seamlessly opens the Full Screen Reader mode.
 */
@Composable
private fun NormalModePdfPageCard(
    pdfFile: File,
    pageIndex: Int,
    totalCount: Int,
    sectionAccentColor: Color,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pageBitmap by remember(pdfFile.path, pageIndex) { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(pdfFile.path, pageIndex) {
        isLoading = true
        val bmp = withContext(Dispatchers.IO) {
            PdfManager.renderPageToBitmap(pdfFile, pageIndex, targetWidth = 1080)
        }
        pageBitmap = bmp
        isLoading = false
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onTap),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column {
            val bmp = pageBitmap
            if (bmp != null) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Question Paper Page ${pageIndex + 1}",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            } else if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f / 1.4142f)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = sectionAccentColor,
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
                        text = "Tap to open in full screen reader",
                        color = Color.DarkGray,
                        fontSize = 12.sp
                    )
                }
            }

            // Subtle bottom footer bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
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
                        text = "Tap to view full screen ⤢",
                        color = sectionAccentColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
