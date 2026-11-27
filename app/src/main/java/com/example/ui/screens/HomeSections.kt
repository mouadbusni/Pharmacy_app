package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.catalog.PharmacyCatalog
import com.example.ui.components.HeaderCircleIconButton
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.MintTeal
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
internal fun HomeTopBar(
    localizedSelectedLocation: String,
    unreadNotificationsCount: Int,
    onOpenLocationSheet: () -> Unit,
    onOpenChats: () -> Unit,
    onOpenNotifications: () -> Unit
) {
    val strings = LocalAppStrings.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f, fill = false)
                .minimumInteractiveComponentSize()
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onOpenLocationSheet)
                .padding(vertical = 6.dp, horizontal = 6.dp)
                .testTag("location_selector")
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = strings.deliverTo,
                tint = ForestGreen,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = strings.deliverTo,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = localizedSelectedLocation,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HeaderCircleIconButton(
                onClick = onOpenChats,
                contentDescription = strings.chatsTitle,
                modifier = Modifier.testTag("home_chats_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = strings.chatsTitle,
                    tint = ForestGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            HeaderCircleIconButton(
                onClick = onOpenNotifications,
                contentDescription = strings.notifications,
                badgeCount = unreadNotificationsCount,
                modifier = Modifier.testTag("home_notifications_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = strings.notifications,
                    tint = ForestGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
internal fun HomePromoBanner(
    isWide: Boolean,
    onOpenVitamins: () -> Unit
) {
    val strings = LocalAppStrings.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .height(if (isWide) 172.dp else 156.dp)
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onOpenVitamins)
            .testTag("home_promo_banner")
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_promo_banner),
            contentDescription = strings.promoDiscount,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = if (strings.isEnglish) {
                            listOf(ForestGreen.copy(alpha = 0.88f), Color.Transparent)
                        } else {
                            listOf(Color.Transparent, ForestGreen.copy(alpha = 0.82f))
                        }
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxWidth(if (isWide) 0.56f else 0.66f)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MintTeal
            ) {
                Text(
                    text = strings.promoDiscount,
                    style = MaterialTheme.typography.labelMedium,
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Column {
                Text(
                    text = strings.promoTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = PureWhite,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = strings.promoSubtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = PureWhite.copy(alpha = 0.92f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeDeliveryLocationSheet(
    selectedLocation: String,
    onSelectLocation: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = PureWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = strings.chooseDeliveryLocation,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            PharmacyCatalog.deliveryLocations.forEach { loc ->
                val isSelected = loc == selectedLocation
                val displayLoc = PharmacyCatalog.localizeLocation(loc, strings.isEnglish)
                Surface(
                    onClick = {
                        onSelectLocation(loc)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) MintSoft else NeutralBackground,
                    border = if (isSelected) BorderStroke(1.5.dp, ForestGreen) else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = if (isSelected) ForestGreen else TextSecondary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = displayLoc,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
