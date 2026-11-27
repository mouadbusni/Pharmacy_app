package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.ChatMessage
import com.example.data.model.Pharmacy
import com.example.ui.components.BackCircleButton
import com.example.ui.components.ErrorStateCard
import com.example.ui.components.LoadingStateCard
import com.example.ui.components.PharmacyAvatarBadge
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.UiState

@Composable
fun LiveChatScreen(
    pharmacy: Pharmacy,
    chatState: UiState<List<ChatMessage>>,
    onBack: () -> Unit,
    onSendMessage: (text: String, attachmentUri: String?, attachmentLabel: String?) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    var messageInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onSendMessage(
                strings.attachPhotoMessage,
                uri.toString(),
                "prescription_photo.jpg"
            )
        }
    }

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
                            ChatMessageBubble(message = msg)
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
                    onClick = { onSendMessage(quick, null, null) },
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
                                    onSendMessage(messageInput, null, null)
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
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("chat_attach_photo_button")
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
