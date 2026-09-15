package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.SolidAmber
import com.example.ui.theme.SolidEmerald
import com.example.ui.theme.SolidGateCyan
import com.example.ui.theme.SolidGateOrange
import com.example.ui.theme.SolidPurple
import com.example.ui.theme.SolidSlate
import com.example.ui.viewmodel.PapersUiState
import com.example.ui.viewmodel.ProgressStats

@Composable
fun SectionCardsView(
    stats: ProgressStats,
    selectedSection: String,
    onSelectSection: (String) -> Unit,
    onSelectStatusFilter: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- Card 1: GATE CS Section (Solid Orange, reference "Spotlight" card) ---
        HeroSectionCard(
            title = "GATE CS",
            subtitle = "Computer Science & Information Tech",
            countText = "${stats.csFinished} / ${stats.csTotal} Finished",
            percentage = stats.csPercentage,
            icon = Icons.Default.Code,
            backgroundColor = SolidGateOrange,
            textColor = Color.White,
            isSelected = selectedSection == PapersUiState.SECTION_CS,
            testTag = "section_card_cs",
            onClick = {
                if (selectedSection == PapersUiState.SECTION_CS) {
                    onSelectSection(PapersUiState.SECTION_ALL)
                } else {
                    onSelectSection(PapersUiState.SECTION_CS)
                }
            }
        )

        // --- Card 2: GATE DA Section (Solid Cyan, reference "Popular Now" card) ---
        HeroSectionCard(
            title = "GATE DA",
            subtitle = "Data Science & Artificial Intelligence",
            countText = "${stats.daFinished} / ${stats.daTotal} Finished",
            percentage = stats.daPercentage,
            icon = Icons.Default.Psychology,
            backgroundColor = SolidGateCyan,
            textColor = Color(0xFF0A1E29),
            isSelected = selectedSection == PapersUiState.SECTION_DA,
            testTag = "section_card_da",
            onClick = {
                if (selectedSection == PapersUiState.SECTION_DA) {
                    onSelectSection(PapersUiState.SECTION_ALL)
                } else {
                    onSelectSection(PapersUiState.SECTION_DA)
                }
            }
        )

        // --- Card 3: Preparation Progress Banner (Solid Purple, reference "New & Trending" card) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SolidPurple)
                .padding(18.dp)
                .testTag("progress_overview_card")
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PROGRESS TRACKER",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Overall Preparation",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${stats.overallPercentage}% Done",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Solid Progress bar
                LinearProgressIndicator(
                    progress = { if (stats.totalPapers > 0) stats.finishedCount.toFloat() / stats.totalPapers.toFloat() else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.25f),
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${stats.finishedCount} Finished",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${stats.pendingCount} Pending to revisit",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${stats.unattemptedCount} Left",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // --- Bottom Two Mini Cards (Reference two smaller bottom cards) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Revisit Later Quick Filter Card (Solid Amber)
            MiniStatusCard(
                title = "Pending Revisit",
                count = stats.pendingCount,
                icon = Icons.Default.PendingActions,
                backgroundColor = SolidAmber,
                textColor = Color(0xFF2A1B02),
                modifier = Modifier.weight(1f),
                testTag = "filter_pending_card",
                onClick = { onSelectStatusFilter(PapersUiState.STATUS_PENDING) }
            )

            // Completed Papers Quick Filter Card (Solid Emerald)
            MiniStatusCard(
                title = "Finished",
                count = stats.finishedCount,
                icon = Icons.Default.CheckCircle,
                backgroundColor = SolidEmerald,
                textColor = Color(0xFF042616),
                modifier = Modifier.weight(1f),
                testTag = "filter_finished_card",
                onClick = { onSelectStatusFilter(PapersUiState.STATUS_FINISHED) }
            )
        }
    }
}

@Composable
private fun HeroSectionCard(
    title: String,
    subtitle: String,
    countText: String,
    percentage: Int,
    icon: ImageVector,
    backgroundColor: Color,
    textColor: Color,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(20.dp)
            .testTag(testTag)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(textColor.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = textColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            color = textColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = subtitle,
                            color = textColor.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(textColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open $title",
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(textColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = countText,
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .background(textColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "FILTER ACTIVE",
                            color = backgroundColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = "$percentage% Complete",
                        color = textColor.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniStatusCard(
    title: String,
    count: Int,
    icon: ImageVector,
    backgroundColor: Color,
    textColor: Color,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag(testTag)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = textColor,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = "$count",
                    color = textColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = textColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
