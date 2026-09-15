package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaperEntity
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.SolidAmber
import com.example.ui.theme.SolidEmerald
import com.example.ui.theme.SolidGateCyan
import com.example.ui.theme.SolidGateOrange
import com.example.ui.theme.SolidSlate
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.PaperPrintHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaperCardItem(
    paper: PaperEntity,
    onViewPaper: () -> Unit,
    onToggleFinished: () -> Unit,
    onTogglePending: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isCS = paper.section == PaperEntity.SECTION_CS
    val sectionAccentColor = if (isCS) SolidGateOrange else SolidGateCyan

    val isFinished = paper.status == PaperEntity.STATUS_FINISHED
    val isPending = paper.status == PaperEntity.STATUS_PENDING || paper.isFlaggedToRevisit

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
            .testTag("paper_card_${paper.id}")
    ) {
        Column {
            // Top row: Year & Section Badge + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Section pill
                    Box(
                        modifier = Modifier
                            .background(sectionAccentColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = paper.section,
                            color = sectionAccentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Year badge
                    Text(
                        text = "${paper.year}",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Status Badge
                when {
                    isFinished -> {
                        Box(
                            modifier = Modifier
                                .background(SolidEmerald, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("status_finished_${paper.id}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "FINISHED",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                    isPending -> {
                        Box(
                            modifier = Modifier
                                .background(SolidAmber, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("status_pending_${paper.id}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = Color(0xFF2A1B02),
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "REVISIT LATER",
                                    color = Color(0xFF2A1B02),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                    else -> {
                        Box(
                            modifier = Modifier
                                .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "UNATTEMPTED",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = paper.title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onViewPaper)
            )

            if (paper.subtitle.isNotEmpty()) {
                Text(
                    text = paper.subtitle,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            if (paper.coreTopics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = paper.coreTopics,
                    color = TextMuted,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Meta specs row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${paper.durationMinutes} min",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = "•",
                    color = DarkBorder,
                    fontSize = 11.sp
                )
                Text(
                    text = "${paper.totalQuestions} questions",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = "•",
                    color = DarkBorder,
                    fontSize = 11.sp
                )
                Text(
                    text = "${paper.totalMarks} marks",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action row 1: View paper & Print & Download
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // View Paper Primary Button
                Button(
                    onClick = onViewPaper,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 46.dp)
                        .testTag("view_paper_button_${paper.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = sectionAccentColor,
                        contentColor = if (isCS) Color.White else Color(0xFF0A1E29)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "View Paper",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }

                // Print Button
                OutlinedButton(
                    onClick = { PaperPrintHelper.printQuestionPaper(context, paper) },
                    modifier = Modifier
                        .defaultMinSize(minHeight = 46.dp)
                        .testTag("print_button_${paper.id}"),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "Print",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Print",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }

                // Download direct from repo button
                OutlinedButton(
                    onClick = { PaperPrintHelper.downloadQuestionPaper(context, paper) },
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("download_button_${paper.id}"),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextSecondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Download Paper",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Tracker Action Row: Finished vs Keep Pending
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "Finished" Toggle Button
                Button(
                    onClick = onToggleFinished,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 44.dp)
                        .testTag("mark_finished_btn_${paper.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isFinished) SolidEmerald else DarkSurfaceVariant,
                        contentColor = if (isFinished) Color.White else TextSecondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 8.dp)
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
                        fontWeight = if (isFinished) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    )
                }

                // "Keep Pending / Revisit" Toggle Button
                Button(
                    onClick = onTogglePending,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 44.dp)
                        .testTag("mark_pending_btn_${paper.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isPending) SolidAmber else DarkSurfaceVariant,
                        contentColor = if (isPending) Color(0xFF2A1B02) else TextSecondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 8.dp)
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
                        fontWeight = if (isPending) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
