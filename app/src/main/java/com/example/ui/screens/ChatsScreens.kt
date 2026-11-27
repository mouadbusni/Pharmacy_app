package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.model.ChatMessage
import com.example.data.model.Pharmacy
import com.example.ui.components.PharmacyAvatarBadge
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ChatBubbleGray
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusDutyAmber
import com.example.ui.theme.StatusOpenBg
import com.example.ui.theme.StatusOpenGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
internal fun ChatPharmacyRowCard(
    pharmacy: Pharmacy,
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    val highlightActive = pharmacy.isAlways24h &&
        (pharmacy.id == "pharm_du_lys" || pharmacy.id == "pharm_les_iris")
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (highlightActive) StatusOpenBg.copy(alpha = 0.7f) else PureWhite,
        border = BorderStroke(1.dp, BorderSubtle),
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .testTag("chat_item_${pharmacy.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                PharmacyAvatarBadge(pharmacy = pharmacy, size = 50.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = pharmacy.localizedName(strings.isEnglish),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val scheduleTag = if (pharmacy.isAlways24h) strings.schedule24h else strings.scheduleDayDuty
                    val statusText = if (pharmacy.openNow) "${strings.availableNow} • $scheduleTag" else strings.closedNow
                    val statusColor = when {
                        !pharmacy.openNow -> TextSecondary
                        pharmacy.isAlways24h -> StatusOpenGreen
                        else -> StatusDutyAmber
                    }
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = pharmacy.lastMessageTime,
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
internal fun ChatMessageBubble(message: ChatMessage) {
    val isPharmacy = message.senderType == "PHARMACY"
    val strings = LocalAppStrings.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isPharmacy) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isPharmacy) 18.dp else 4.dp,
                bottomEnd = if (isPharmacy) 4.dp else 18.dp
            ),
            color = if (isPharmacy) ForestGreen else ChatBubbleGray,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                val attachmentUri = message.attachmentUri
                if (!attachmentUri.isNullOrBlank()) {
                    AsyncImage(
                        model = attachmentUri,
                        contentDescription = message.attachmentLabel ?: strings.attachPrescription,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(136.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isPharmacy) PureWhite else TextPrimary
                )
            }
        }
    }
}
