package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.update.AppUpdateInfo
import com.example.data.update.AppUpdateState
import com.example.ui.theme.AppStrings
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.MintTeal
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextSecondary

@Composable
fun AppUpdateBannerCard(
    updateState: AppUpdateState,
    onUpdateNow: (AppUpdateInfo) -> Unit,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val available = updateState as? AppUpdateState.UpdateAvailable
    val visible = available != null && !available.isDismissed

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        if (available != null) {
            val info = available.info
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = MintSoft,
                border = BorderStroke(1.2.dp, ForestGreen.copy(alpha = 0.35f)),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .testTag("home_update_banner")
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ForestGreen,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.SystemUpdate,
                                    contentDescription = strings.updateAvailableTitle,
                                    tint = PureWhite,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "${strings.updateAvailableTitle} • ${info.tagName}",
                                style = MaterialTheme.typography.titleSmall,
                                color = ForestGreen,
                                fontWeight = FontWeight.Bold
                            )
                            if (info.releaseNotes.isNotBlank()) {
                                Text(
                                    text = info.releaseNotes,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { onDismiss(info.tagName) },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("update_later_button")
                        ) {
                            Text(
                                text = strings.laterAction,
                                color = TextSecondary,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = { onUpdateNow(info) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("update_now_button")
                        ) {
                            Text(
                                text = strings.updateNowAction,
                                color = PureWhite,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

enum class BottomTab(val route: String) {
    HOME("home"),
    NOTIFICATIONS("notifications"),
    FAVORITES("favorites"),
    PROFILE("profile");

    fun localizedLabel(strings: AppStrings): String = when (this) {
        HOME -> strings.tabHome
        NOTIFICATIONS -> strings.tabNotifications
        FAVORITES -> strings.tabFavorites
        PROFILE -> strings.tabProfile
    }
}

@Composable
fun PharmacyBottomBar(
    currentRoute: String,
    cartCount: Int,
    onNavigateTab: (BottomTab) -> Unit,
    onOpenCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PureWhite,
            shadowElevation = 8.dp,
            border = BorderStroke(1.dp, BorderSubtle),
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomBarTabItem(
                    tab = BottomTab.HOME,
                    selected = currentRoute == BottomTab.HOME.route,
                    onClick = { onNavigateTab(BottomTab.HOME) }
                )
                BottomBarTabItem(
                    tab = BottomTab.NOTIFICATIONS,
                    selected = currentRoute == BottomTab.NOTIFICATIONS.route,
                    onClick = { onNavigateTab(BottomTab.NOTIFICATIONS) }
                )
                Spacer(modifier = Modifier.width(56.dp))
                BottomBarTabItem(
                    tab = BottomTab.FAVORITES,
                    selected = currentRoute == BottomTab.FAVORITES.route,
                    onClick = { onNavigateTab(BottomTab.FAVORITES) }
                )
                BottomBarTabItem(
                    tab = BottomTab.PROFILE,
                    selected = currentRoute == BottomTab.PROFILE.route,
                    onClick = { onNavigateTab(BottomTab.PROFILE) }
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp)
        ) {
            Surface(
                onClick = onOpenCart,
                shape = RoundedCornerShape(18.dp),
                color = ForestGreen,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .size(56.dp)
                    .testTag("bottom_cart_fab")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingBag,
                        contentDescription = strings.cartTitle,
                        tint = PureWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            if (cartCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MintTeal)
                        .border(1.5.dp, PureWhite, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (cartCount > 9) "9+" else cartCount.toString(),
                        color = PureWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomBarTabItem(
    tab: BottomTab,
    selected: Boolean,
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    val label = tab.localizedLabel(strings)
    val icon = when (tab) {
        BottomTab.HOME -> if (selected) Icons.Filled.Home else Icons.Outlined.Home
        BottomTab.NOTIFICATIONS -> if (selected) Icons.Filled.Notifications else Icons.Outlined.Notifications
        BottomTab.FAVORITES -> if (selected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
        BottomTab.PROFILE -> if (selected) Icons.Filled.Settings else Icons.Outlined.Settings
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("tab_${tab.route}")
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) ForestGreen else TextSecondary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) ForestGreen else TextSecondary,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}
