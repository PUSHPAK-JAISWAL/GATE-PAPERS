package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaperEntity
import com.example.ui.components.PaperCardItem
import com.example.ui.components.QuestionPaperViewerScreen
import com.example.ui.components.SearchBarView
import com.example.ui.components.SectionCardsView
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
import com.example.ui.viewmodel.PaperViewModel
import com.example.ui.viewmodel.PapersUiState

@Composable
fun HomeScreen(
    viewModel: PaperViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    // If a paper is currently open in full viewer, show the viewer screen
    val activePaper = state.activePaperForViewing
    if (activePaper != null) {
        BackHandler {
            viewModel.closePaperViewer()
        }
        QuestionPaperViewerScreen(
            paper = activePaper,
            onBack = { viewModel.closePaperViewer() },
            onToggleFinished = { viewModel.markPaperFinished(activePaper) },
            onTogglePending = { viewModel.markPaperPending(activePaper) },
            onSaveNotes = { notes -> viewModel.saveNotes(activePaper.id, notes) }
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            // Header Top spacing & Insets
            item {
                Spacer(
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .height(10.dp)
                )
            }

            // --- Top Pill Search Bar (From reference design) ---
            item {
                SearchBarView(
                    query = state.searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChange(it) },
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            // --- GitHub Live Repos Sync Bar ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        if (state.isSyncing) SolidAmber else SolidEmerald,
                                        CircleShape
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (state.isSyncing) "Syncing from GitHub repos..." else "Connected to gatecs & gateda",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = state.syncStatusMessage ?: "Tap to refresh question papers",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.syncWithGitHub() },
                            enabled = !state.isSyncing,
                            modifier = Modifier
                                .size(32.dp)
                                .background(DarkSurfaceVariant, CircleShape)
                                .testTag("github_sync_button")
                        ) {
                            if (state.isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = SolidGateOrange
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Sync,
                                    contentDescription = "Sync Repos",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // --- Section Filter Pills Row ---
            item {
                FilterPillsRow(
                    selectedSection = state.selectedSection,
                    selectedStatus = state.selectedStatusFilter,
                    onSelectSection = { viewModel.selectSection(it) },
                    onSelectStatus = { viewModel.selectStatusFilter(it) }
                )
            }

            // --- Hero Section Cards (CS, DA, Preparation Tracker) ---
            // Only show hero cards when not deeply searching, or show as quick filters
            if (state.searchQuery.isEmpty()) {
                item {
                    SectionCardsView(
                        stats = state.stats,
                        selectedSection = state.selectedSection,
                        onSelectSection = { viewModel.selectSection(it) },
                        onSelectStatusFilter = { viewModel.selectStatusFilter(it) }
                    )
                }
            }

            // --- Papers List Section Header ---
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (state.selectedSection) {
                                PapersUiState.SECTION_CS -> "GATE CS Papers"
                                PapersUiState.SECTION_DA -> "GATE DA Papers"
                                else -> "All Question Papers"
                            },
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "${state.filteredPapers.size}",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (state.selectedStatusFilter != PapersUiState.STATUS_ALL ||
                        state.selectedSection != PapersUiState.SECTION_ALL ||
                        state.searchQuery.isNotEmpty()
                    ) {
                        Text(
                            text = "Reset Filter",
                            color = SolidGateOrange,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    viewModel.selectSection(PapersUiState.SECTION_ALL)
                                    viewModel.selectStatusFilter(PapersUiState.STATUS_ALL)
                                    viewModel.onSearchQueryChange("")
                                }
                                .padding(4.dp)
                                .testTag("reset_filters_btn")
                        )
                    }
                }
            }

            // --- Empty State if No Results ---
            if (state.filteredPapers.isEmpty()) {
                item {
                    EmptyPapersView(
                        searchQuery = state.searchQuery,
                        onClearFilters = {
                            viewModel.selectSection(PapersUiState.SECTION_ALL)
                            viewModel.selectStatusFilter(PapersUiState.STATUS_ALL)
                            viewModel.onSearchQueryChange("")
                        }
                    )
                }
            } else {
                // --- List of Question Papers ---
                items(
                    items = state.filteredPapers,
                    key = { it.id }
                ) { paper ->
                    PaperCardItem(
                        paper = paper,
                        onViewPaper = { viewModel.openPaperViewer(paper) },
                        onToggleFinished = { viewModel.markPaperFinished(paper) },
                        onTogglePending = { viewModel.markPaperPending(paper) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterPillsRow(
    selectedSection: String,
    selectedStatus: String,
    onSelectSection: (String) -> Unit,
    onSelectStatus: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Section: All
        FilterPill(
            text = "All Sections",
            isSelected = selectedSection == PapersUiState.SECTION_ALL,
            selectedColor = TextPrimary,
            testTag = "filter_section_all",
            onClick = { onSelectSection(PapersUiState.SECTION_ALL) }
        )

        // Section: GATE CS
        FilterPill(
            text = "GATE CS",
            isSelected = selectedSection == PapersUiState.SECTION_CS,
            selectedColor = SolidGateOrange,
            testTag = "filter_section_cs",
            onClick = { onSelectSection(PapersUiState.SECTION_CS) }
        )

        // Section: GATE DA
        FilterPill(
            text = "GATE DA",
            isSelected = selectedSection == PapersUiState.SECTION_DA,
            selectedColor = SolidGateCyan,
            testTag = "filter_section_da",
            onClick = { onSelectSection(PapersUiState.SECTION_DA) }
        )

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(20.dp)
                .background(DarkBorder)
        )

        // Status: Pending Revisit
        FilterPill(
            text = "Pending Revisit",
            isSelected = selectedStatus == PapersUiState.STATUS_PENDING,
            selectedColor = SolidAmber,
            testTag = "filter_status_pending",
            onClick = {
                if (selectedStatus == PapersUiState.STATUS_PENDING) {
                    onSelectStatus(PapersUiState.STATUS_ALL)
                } else {
                    onSelectStatus(PapersUiState.STATUS_PENDING)
                }
            }
        )

        // Status: Finished
        FilterPill(
            text = "Finished",
            isSelected = selectedStatus == PapersUiState.STATUS_FINISHED,
            selectedColor = SolidEmerald,
            testTag = "filter_status_finished",
            onClick = {
                if (selectedStatus == PapersUiState.STATUS_FINISHED) {
                    onSelectStatus(PapersUiState.STATUS_ALL)
                } else {
                    onSelectStatus(PapersUiState.STATUS_FINISHED)
                }
            }
        )

        // Status: Unattempted
        FilterPill(
            text = "Unattempted",
            isSelected = selectedStatus == PapersUiState.STATUS_UNATTEMPTED,
            selectedColor = TextSecondary,
            testTag = "filter_status_unattempted",
            onClick = {
                if (selectedStatus == PapersUiState.STATUS_UNATTEMPTED) {
                    onSelectStatus(PapersUiState.STATUS_ALL)
                } else {
                    onSelectStatus(PapersUiState.STATUS_UNATTEMPTED)
                }
            }
        )
    }
}

@Composable
private fun FilterPill(
    text: String,
    isSelected: Boolean,
    selectedColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) selectedColor else DarkSurface
    val textColor = if (isSelected) {
        if (selectedColor == SolidGateCyan || selectedColor == SolidAmber) Color(0xFF111827) else Color.White
    } else {
        TextSecondary
    }
    val borderColor = if (isSelected) selectedColor else DarkBorder

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag(testTag)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun EmptyPapersView(
    searchQuery: String,
    onClearFilters: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(DarkSurfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (searchQuery.isNotEmpty()) "No papers matching '$searchQuery'" else "No question papers found",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Try adjusting your search keywords or reset filter chips.",
                color = TextMuted,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClearFilters,
                colors = ButtonDefaults.buttonColors(containerColor = SolidGateOrange),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Show All Papers",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
