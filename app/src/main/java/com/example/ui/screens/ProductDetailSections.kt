package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.components.SectionHeaderRow
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintTeal
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusClosedBg
import com.example.ui.theme.StatusClosedRed
import com.example.ui.theme.StatusDutyAmber
import com.example.ui.theme.StatusDutyBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.WarmGold

@Composable
internal fun ProductRatingSummaryCard(product: Product) {
    val strings = LocalAppStrings.current
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = NeutralBackground,
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${product.reviewCount} ${strings.reviewsSuffix} • ${product.rating}",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                DynamicStarRow(rating = product.rating)
            }

            Row(horizontalArrangement = Arrangement.spacedBy((-10).dp)) {
                val colors = listOf(
                    Color(0xFF1EAF8F),
                    Color(0xFFF2994A),
                    Color(0xFF5D3A8E),
                    Color(0xFF00402E)
                )
                val letters = if (strings.isEnglish) {
                    listOf("N", "S", "M", "A")
                } else {
                    listOf("ن", "س", "م", "ع")
                }
                colors.forEachIndexed { idx, bg ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(bg)
                            .border(2.dp, PureWhite, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = letters[idx],
                            color = PureWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun ProductDosageSection(product: Product) {
    val strings = LocalAppStrings.current
    Column(
        modifier = Modifier.padding(horizontal = 18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SectionHeaderRow(
            title = strings.dosageUsageTitle,
            actionLabel = null
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = NeutralBackground,
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = MintTeal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = product.localizedDosageTime(strings.isEnglish),
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                }
            }
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = NeutralBackground,
                border = BorderStroke(1.dp, BorderSubtle),
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Restaurant,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = product.localizedMealTiming(strings.isEnglish),
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
internal fun ProductSafetyNoteCard(product: Product) {
    val strings = LocalAppStrings.current
    val noteBg = if (product.requiresPrescription) StatusClosedBg.copy(alpha = 0.7f) else StatusDutyBg.copy(alpha = 0.65f)
    val noteAccent = if (product.requiresPrescription) StatusClosedRed else StatusDutyAmber
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = noteBg,
        border = BorderStroke(1.dp, noteAccent.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .testTag("pharmacist_safety_note_card")
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (product.requiresPrescription) Icons.Filled.Info else Icons.Filled.VerifiedUser,
                contentDescription = strings.pharmacistSafetyNoteTitle,
                tint = noteAccent,
                modifier = Modifier.size(22.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = strings.pharmacistSafetyNoteTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = noteAccent,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = product.localizedSafetyNote(strings.isEnglish),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun DynamicStarRow(rating: Double) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        for (i in 1..5) {
            val thresholdFull = i.toDouble()
            val thresholdHalf = i - 0.5
            when {
                rating >= thresholdFull -> {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = WarmGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
                rating >= thresholdHalf -> {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.StarHalf,
                        contentDescription = null,
                        tint = WarmGold,
                        modifier = Modifier.size(18.dp)
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = BorderSubtle,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
