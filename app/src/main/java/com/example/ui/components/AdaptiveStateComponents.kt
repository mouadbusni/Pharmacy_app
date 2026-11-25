package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.MintTeal
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusClosedBg
import com.example.ui.theme.StatusClosedRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LoadingStateCard(
    modifier: Modifier = Modifier,
    message: String? = null
) {
    val strings = LocalAppStrings.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = ForestGreen,
                strokeWidth = 3.dp,
                modifier = Modifier.size(38.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message ?: strings.loadingData,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun ErrorStateCard(
    errorMessage: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = StatusClosedBg,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.ErrorOutline,
                        contentDescription = null,
                        tint = StatusClosedRed,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Text(
                text = strings.errorLoadingData,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    tint = PureWhite,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(strings.retry, color = PureWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PharmacyNavigationRail(
    currentRoute: String,
    cartCount: Int,
    onNavigateTab: (BottomTab) -> Unit,
    onOpenCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    NavigationRail(
        containerColor = PureWhite,
        contentColor = ForestGreen,
        modifier = modifier
            .fillMaxHeight()
            .statusBarsPadding()
            .border(1.dp, BorderSubtle),
        header = {
            Box(modifier = Modifier.padding(top = 14.dp, bottom = 8.dp)) {
                Surface(
                    onClick = onOpenCart,
                    shape = RoundedCornerShape(16.dp),
                    color = ForestGreen,
                    shadowElevation = 6.dp,
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("rail_cart_fab")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingBag,
                            contentDescription = strings.cartTitle,
                            tint = PureWhite,
                            modifier = Modifier.size(22.dp)
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
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        BottomTab.entries.forEach { tab ->
            val selected = currentRoute == tab.route
            val label = tab.localizedLabel(strings)
            val icon = when (tab) {
                BottomTab.HOME -> if (selected) Icons.Filled.Home else Icons.Outlined.Home
                BottomTab.NOTIFICATIONS -> if (selected) Icons.Filled.Notifications else Icons.Outlined.Notifications
                BottomTab.FAVORITES -> if (selected) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder
                BottomTab.PROFILE -> if (selected) Icons.Filled.Settings else Icons.Outlined.Settings
            }
            NavigationRailItem(
                selected = selected,
                onClick = { onNavigateTab(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = ForestGreen,
                    selectedTextColor = ForestGreen,
                    indicatorColor = MintSoft,
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("rail_tab_${tab.route}")
            )
        }
    }
}
