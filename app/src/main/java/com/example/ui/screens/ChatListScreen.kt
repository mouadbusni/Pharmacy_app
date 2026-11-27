package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.Pharmacy
import com.example.ui.components.BackCircleButton
import com.example.ui.components.HeaderCircleIconButton
import com.example.ui.components.PharmacyAvatarBadge
import com.example.ui.components.SectionHeaderRow
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusClosedRed
import com.example.ui.theme.StatusOpenGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ChatListScreen(
    onBack: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenLiveChat: (Pharmacy) -> Unit,
    onOpenPharmaciesList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var searchQuery by rememberSaveable { mutableStateOf("") }
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("chat_search_clear")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = strings.clearSearch,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
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
                    EmptyFilterStateCard(
                        message = strings.noMatchingPharmacies,
                        resetTestTag = "chat_reset_filters",
                        onResetFilters = { searchQuery = "" }
                    )
                }
            } else {
                items(filteredPharmacies, key = { it.id }) { pharmacy ->
                    ChatPharmacyRowCard(
                        pharmacy = pharmacy,
                        onClick = { onOpenLiveChat(pharmacy) }
                    )
                }
            }
        }
    }
}
