package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.ChatMessage
import com.example.data.model.Pharmacy
import com.example.ui.components.BackCircleButton
import com.example.ui.components.ErrorStateCard
import com.example.ui.components.HeaderCircleIconButton
import com.example.ui.components.LoadingStateCard
import com.example.ui.components.PharmacyAvatarBadge
import com.example.ui.components.SectionHeaderRow
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ChatBubbleGray
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusClosedRed
import com.example.ui.theme.StatusDutyAmber
import com.example.ui.theme.StatusOpenBg
import com.example.ui.theme.StatusOpenGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.UiState

@Composable
fun ChatListScreen(
    onBack: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenLiveChat: (Pharmacy) -> Unit,
    onOpenPharmaciesList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var searchQuery by remember { mutableStateOf("") }
    val filteredPharmacies = remember(searchQuery) {
        val trimmed = searchQuery.trim()
        if (trimmed.isBlank()) PharmacyCatalog.pharmacies
        else PharmacyCatalog.pharmacies.filter {
            it.nameAr.contains(trimmed, ignoreCase = true) ||
                it.nameEn.contains(trimmed, ignoreCase = true) ||
                it.areaAr.contains(trimmed, ignoreCase = true) ||
                it.areaEn.contains(trimmed, ignoreCase = true) ||
                it.addressAr.contains(trimmed, ignoreCase = true) ||
                it.addressEn.contains(trimmed, ignoreCase = true)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        val maxContentWidth = if (maxWidth >= 600.dp) 800.dp else 600.dp

        LazyColumn(
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .fillMaxSize(),
            contentPadding = PaddingValues(top = 12.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackCircleButton(onClick = onBack)
                    Text(
                        text = strings.chatsTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                    HeaderCircleIconButton(
                        onClick = onOpenNotifications,
                        contentDescription = strings.notifications
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

            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = strings.searchChatPlaceholder,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    },
                    trailingIcon = {
                        Surface(
                            shape = CircleShape,
                            color = ForestGreen,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = strings.searchAction,
                                    tint = PureWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PureWhite,
                        unfocusedContainerColor = PureWhite,
                        focusedBorderColor = ForestGreen,
                        unfocusedBorderColor = BorderSubtle
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                        .testTag("chat_search_input")
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeaderRow(
                        title = strings.allPharmacies,
                        onActionClick = onOpenPharmaciesList,
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(PharmacyCatalog.pharmacies, key = { "avatar_${it.id}" }) { pharmacy ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { onOpenLiveChat(pharmacy) }
                                    .padding(4.dp)
                            ) {
                                Box {
                                    PharmacyAvatarBadge(pharmacy = pharmacy, size = 58.dp)
                                    Box(
                                        modifier = Modifier
                                            .size(13.dp)
                                            .align(Alignment.BottomEnd)
                                            .clip(CircleShape)
                                            .background(if (pharmacy.openNow) StatusOpenGreen else StatusClosedRed)
                                            .border(2.dp, PureWhite, CircleShape)
                                    )
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = pharmacy.localizedName(strings.isEnglish),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }

            item {
                SectionHeaderRow(
                    title = strings.chatsTitle,
                    actionLabel = null,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
            }

            if (filteredPharmacies.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PureWhite,
                        border = BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                    ) {
                        Text(
                            text = strings.noMatchingPharmacies,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                }
            } else {
                items(filteredPharmacies, key = { it.id }) { pharmacy ->
                    val highlightActive = pharmacy.isAlways24h &&
                        (pharmacy.id == "pharm_du_lys" || pharmacy.id == "pharm_les_iris")
                    Surface(
                        onClick = { onOpenLiveChat(pharmacy) },
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
            }
        }
    }
}

@Composable
fun LiveChatScreen(
    pharmacy: Pharmacy,
    chatState: UiState<List<ChatMessage>>,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    var messageInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val messages = (chatState as? UiState.Success)?.data ?: emptyList()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite)
            .imePadding()
    ) {
        Surface(
            color = ForestGreen,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .widthIn(max = 800.dp)
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        BackCircleButton(onClick = onBack, darkSurface = true)
                        Spacer(modifier = Modifier.width(10.dp))
                        PharmacyAvatarBadge(pharmacy = pharmacy, size = 44.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = pharmacy.localizedName(strings.isEnglish),
                                style = MaterialTheme.typography.titleMedium,
                                color = PureWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = PureWhite.copy(alpha = 0.8f),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = pharmacy.localizedArea(strings.isEnglish),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PureWhite.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${pharmacy.phoneNumber}")
                            }
                            runCatching { context.startActivity(intent) }
                        },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Call,
                            contentDescription = strings.callPharmacy,
                            tint = PureWhite
                        )
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            when (chatState) {
                UiState.Loading -> {
                    LoadingStateCard(modifier = Modifier.align(Alignment.Center))
                }
                is UiState.Error -> {
                    ErrorStateCard(
                        errorMessage = chatState.message,
                        onRetry = onRetry,
                        modifier = Modifier
                            .widthIn(max = 600.dp)
                            .align(Alignment.Center)
                    )
                }
                is UiState.Success -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .widthIn(max = 800.dp)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(messages, key = { it.id.ifBlank { "${it.senderType}_${it.text.hashCode()}" } }) { msg ->
                            val isPharmacy = msg.senderType == "PHARMACY"
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
                                    Text(
                                        text = msg.text,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (isPharmacy) PureWhite else TextPrimary,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .widthIn(max = 800.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            items(strings.quickReplies) { quick ->
                Surface(
                    onClick = { onSendMessage(quick) },
                    shape = RoundedCornerShape(14.dp),
                    color = MintSoft,
                    border = BorderStroke(1.dp, BorderSubtle)
                ) {
                    Text(
                        text = quick,
                        style = MaterialTheme.typography.labelMedium,
                        color = ForestGreen,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Surface(
            color = PureWhite,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .widthIn(max = 800.dp)
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            onClick = {
                                if (messageInput.isNotBlank()) {
                                    onSendMessage(messageInput)
                                    messageInput = ""
                                }
                            },
                            shape = CircleShape,
                            color = ForestGreen,
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("chat_send_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = strings.send,
                                    tint = PureWhite,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        placeholder = {
                            Text(
                                text = strings.messagePlaceholder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = NeutralBackground,
                            unfocusedContainerColor = NeutralBackground,
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_message_input")
                    )

                    IconButton(
                        onClick = { onSendMessage(strings.attachPhotoMessage) },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = strings.attachPrescription,
                            tint = ForestGreen
                        )
                    }
                }
            }
        }
    }
}
