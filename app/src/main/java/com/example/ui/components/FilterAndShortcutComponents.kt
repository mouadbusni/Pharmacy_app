package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.PharmacyScheduleFilter
import com.example.data.model.PrescriptionFilter
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusClosedBg
import com.example.ui.theme.StatusClosedRed
import com.example.ui.theme.StatusDutyAmber
import com.example.ui.theme.StatusDutyBg
import com.example.ui.theme.StatusOpenGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PharmacyScheduleFilterRow(
    selected: PharmacyScheduleFilter,
    onSelect: (PharmacyScheduleFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val options = listOf(
        PharmacyScheduleFilter.ALL to strings.filterAllPharmacies,
        PharmacyScheduleFilter.ALWAYS_24H to strings.schedule24h,
        PharmacyScheduleFilter.DAY_DUTY to strings.scheduleDayDuty
    )
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options, key = { it.first.name }) { (filter, label) ->
            val isSelected = selected == filter
            val activeBg = when (filter) {
                PharmacyScheduleFilter.DAY_DUTY -> StatusDutyBg
                else -> MintSoft
            }
            val activeFg = when (filter) {
                PharmacyScheduleFilter.DAY_DUTY -> StatusDutyAmber
                else -> ForestGreen
            }
            Surface(
                onClick = { onSelect(filter) },
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) activeBg else PureWhite,
                border = BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) activeFg else BorderSubtle
                ),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("schedule_filter_${filter.name.lowercase()}")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) activeFg else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun PrescriptionFilterRow(
    selected: PrescriptionFilter,
    onSelect: (PrescriptionFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val options = listOf(
        PrescriptionFilter.ALL to strings.filterAllProducts,
        PrescriptionFilter.OTC to strings.otcLabel,
        PrescriptionFilter.RX_REQUIRED to strings.prescriptionRequiredLabel
    )
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options, key = { it.first.name }) { (filter, label) ->
            val isSelected = selected == filter
            val activeBg = when (filter) {
                PrescriptionFilter.RX_REQUIRED -> StatusClosedBg
                else -> MintSoft
            }
            val activeFg = when (filter) {
                PrescriptionFilter.RX_REQUIRED -> StatusClosedRed
                else -> ForestGreen
            }
            Surface(
                onClick = { onSelect(filter) },
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) activeBg else PureWhite,
                border = BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) activeFg else BorderSubtle
                ),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("prescription_filter_${filter.name.lowercase()}")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) activeFg else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun HomeQuickShortcutsRow(
    onOpenPharmaciesList: () -> Unit,
    onOpen24hPharmacies: () -> Unit,
    onOpenChats: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(key = "shortcut_pharmacies") {
            QuickShortcutPill(
                label = strings.quickDirectory,
                icon = Icons.Filled.LocalPharmacy,
                accentColor = ForestGreen,
                containerColor = PureWhite,
                onClick = onOpenPharmaciesList,
                modifier = Modifier.testTag("home_shortcut_directory")
            )
        }
        item(key = "shortcut_24h") {
            QuickShortcutPill(
                label = strings.quickOpen24h,
                icon = Icons.Filled.AccessTime,
                accentColor = StatusOpenGreen,
                containerColor = PureWhite,
                onClick = onOpen24hPharmacies,
                modifier = Modifier.testTag("home_shortcut_24h")
            )
        }
        item(key = "shortcut_chats") {
            QuickShortcutPill(
                label = strings.quickPharmacistChat,
                icon = Icons.AutoMirrored.Outlined.Chat,
                accentColor = ForestGreen,
                containerColor = MintSoft,
                onClick = onOpenChats,
                modifier = Modifier.testTag("home_shortcut_chats")
            )
        }
    }
}

@Composable
private fun QuickShortcutPill(
    label: String,
    icon: ImageVector,
    accentColor: Color,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(1.dp, BorderSubtle),
        shadowElevation = 1.dp,
        modifier = modifier.minimumInteractiveComponentSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}
