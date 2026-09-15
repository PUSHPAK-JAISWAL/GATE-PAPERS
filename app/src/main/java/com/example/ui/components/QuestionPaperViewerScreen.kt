package com.example.ui.components

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaperEntity
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

    // PDF Download & Render State
    var pdfFile by remember { mutableStateOf<File?>(null) }
    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableFloatStateOf(0f) }
    var downloadError by remember { mutableStateOf<String?>(null) }
    var pageCount by remember { mutableIntStateOf(0) }

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
                downloadError = "Could not download PDF from GitHub repository. Check connection or try external view."
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
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
                            modifier = Modifier.testTag("viewer_back_button")
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
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = "${paper.section} • ${paper.subtitle.ifEmpty { paper.githubRepo }}",
                                color = sectionAccentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }

                    // Print, External App & Download action buttons in top bar
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                            modifier = Modifier.testTag("viewer_print_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = "Print Document",
                                tint = TextPrimary
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
                            modifier = Modifier.testTag("viewer_open_external_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Open in PDF App",
                                tint = TextPrimary
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            // Bottom Action Bar: Print for Practice & Download
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .border(width = 1.dp, color = DarkBorder)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                            .weight(1.2f)
                            .height(48.dp)
                            .testTag("viewer_bottom_print_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = sectionAccentColor,
                            contentColor = if (isCS) Color.White else Color(0xFF0A1E29)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Print for Practice",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
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
                            .height(48.dp)
                            .testTag("viewer_bottom_download_button"),
                        shape = RoundedCornerShape(12.dp),
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
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
            }

            // --- Status & Progress Action Box ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                        .border(1.dp, DarkBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "PREPARATION PROGRESS TRACKER",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = when {
                                        isFinished -> "Finished ✓"
                                        isPending -> "Pending to Revisit Later ⏳"
                                        else -> "Pending / Not Yet Attempted"
                                    },
                                    color = when {
                                        isFinished -> SolidEmerald
                                        isPending -> SolidAmber
                                        else -> TextSecondary
                                    },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = { showNotesEditor = !showNotesEditor },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(DarkSurfaceVariant, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = "Study Notes",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Two distinct progress toggle buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // "Finished" Button
                            Button(
                                onClick = onToggleFinished,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("viewer_mark_finished_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isFinished) SolidEmerald else DarkSurfaceVariant,
                                    contentColor = if (isFinished) Color.White else TextSecondary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isFinished) "Finished ✓" else "Mark Finished",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // "Keep Pending" Button
                            Button(
                                onClick = onTogglePending,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(42.dp)
                                    .testTag("viewer_mark_pending_btn"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isPending) SolidAmber else DarkSurfaceVariant,
                                    contentColor = if (isPending) Color(0xFF2A1B02) else TextSecondary
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isPending) "Pending ⏳" else "Keep Pending",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Notes expandable area
                        AnimatedVisibility(visible = showNotesEditor || paper.notes.isNotEmpty()) {
                            Column(modifier = Modifier.padding(top = 12.dp)) {
                                Text(
                                    text = "Study Notes & Revision Doubts:",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = notesText,
                                    onValueChange = {
                                        notesText = it
                                        onSaveNotes(it)
                                    },
                                    placeholder = {
                                        Text(
                                            "e.g. Need to practice questions 15-20 on Algorithms...",
                                            fontSize = 12.sp,
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

            // --- GitHub Repo Source Info Card ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceVariant)
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "GitHub File: ${paper.githubFileName}",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Repo: https://github.com/${paper.githubRepo}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        if (pdfFile != null && pageCount > 0) {
                            Box(
                                modifier = Modifier
                                    .background(sectionAccentColor.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "$pageCount Pages",
                                    color = sectionAccentColor,
                                    fontSize = 11.sp,
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
                                text = "Fetching authentic PDF from GitHub repo...",
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
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Official Paper Pages ($pageCount)",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Tap to open in PDF app",
                            color = sectionAccentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable {
                                PdfManager.openWithExternalApp(context, currentFile)
                            }
                        )
                    }
                }

                items(count = pageCount) { pageIndex ->
                    PdfPageCard(
                        pdfFile = currentFile,
                        pageIndex = pageIndex,
                        totalCount = pageCount,
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
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PdfPageCard(
    pdfFile: File,
    pageIndex: Int,
    totalCount: Int,
    onOpenExternal: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pageBitmap by remember(pdfFile.path, pageIndex) { mutableStateOf<Bitmap?>(null) }
    var isLoadingPage by remember { mutableStateOf(true) }

    LaunchedEffect(pdfFile.path, pageIndex) {
        isLoadingPage = true
        val bmp = withContext(Dispatchers.IO) {
            PdfManager.renderPageToBitmap(pdfFile, pageIndex, targetWidth = 1080)
        }
        pageBitmap = bmp
        isLoadingPage = false
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenExternal),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
    ) {
        Column {
            // Page Header strip
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
                        text = "Tap to expand",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }
            }

            // Rendered Image
            val bmp = pageBitmap
            if (bmp != null) {
                Image(
                    bitmap = bmp.asImageBitmap(),
                    contentDescription = "Question Paper Page ${pageIndex + 1}",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
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
                        text = "Unable to render page preview. Tap to open in PDF app.",
                        color = Color.DarkGray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
