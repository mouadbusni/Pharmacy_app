package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.ChatMessage
import com.example.data.model.Pharmacy
import com.example.ui.components.BackCircleButton
import com.example.ui.components.HeaderCircleIconButton
import com.example.ui.components.PharmacyAvatarBadge
import com.example.ui.components.SectionHeaderRow
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ChatBubbleGray
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.MintSoft
import com.example.ui.theme.MintTeal
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusClosedRed
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
    var searchQuery by remember { mutableStateOf("") }
    val filteredPharmacies = remember(searchQuery) {
        if (searchQuery.isBlank()) PharmacyCatalog.pharmacies
        else PharmacyCatalog.pharmacies.filter {
            it.nameAr.contains(searchQuery.trim(), ignoreCase = true) ||
                it.areaAr.contains(searchQuery.trim(), ignoreCase = true)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 600.dp)
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
                        text = "الدردشات",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                    HeaderCircleIconButton(
                        onClick = onOpenNotifications,
                        contentDescription = "الإشعارات"
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "الإشعارات",
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
                            text = "أبحث عن صيدلية للدردشة....",
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
                                    contentDescription = "بحث",
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
                        title = "جميع الصيدليات",
                        actionLabel = "عرض المزيد",
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
                                    text = pharmacy.nameAr,
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
                    title = "الدردشات",
                    actionLabel = null,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
            }

            items(filteredPharmacies, key = { it.id }) { pharmacy ->
                Surface(
                    onClick = { onOpenLiveChat(pharmacy) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (pharmacy.openNow && pharmacy.id == "pharm_mariam" || pharmacy.id == "pharm_ola") {
                        StatusOpenBg.copy(alpha = 0.7f)
                    } else {
                        PureWhite
                    },
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
                                    text = pharmacy.nameAr,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (pharmacy.openNow) "متاح الآن" else "مغلق الآن",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (pharmacy.openNow) StatusOpenGreen else TextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

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

@Composable
fun LiveChatScreen(
    pharmacy: Pharmacy,
    chatState: UiState<List<ChatMessage>>,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
        // Deep Green Chat Header
        Surface(
            color = ForestGreen,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackCircleButton(onClick = onBack, darkSurface = true)
                    Spacer(modifier = Modifier.width(10.dp))
                    PharmacyAvatarBadge(pharmacy = pharmacy, size = 44.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = pharmacy.nameAr,
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite
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
                                text = pharmacy.areaAr,
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
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Call,
                        contentDescription = "اتصال بالصيدلية",
                        tint = PureWhite
                    )
                }
            }
        }

        // Messages Stream
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .widthIn(max = 600.dp)
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
                            modifier = Modifier.widthIn(max = 280.dp)
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

        // Quick Pharmacy Message Suggestions
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .widthIn(max = 600.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            val quickReplies = listOf(
                "هل الدواء متوفر الآن؟",
                "كم مدة التوصيل للموقع الحالي؟",
                "أحتاج استشارة حول الجرعة"
            )
            items(quickReplies) { quick ->
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
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }

        // Bottom Message Input Bar matching mockup
        Surface(
            color = PureWhite,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                            contentDescription = "إرسال",
                            tint = PureWhite,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }

                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    placeholder = {
                        Text(
                            text = "مراسلة....",
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
                    onClick = { onSendMessage("تم إرفاق صورة الوصفة الطبية") }
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt,
                        contentDescription = "إرفاق وصفة طبية",
                        tint = ForestGreen
                    )
                }
            }
        }
    }
}
