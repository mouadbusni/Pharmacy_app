package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.PharmacyOrder
import com.example.ui.components.BackCircleButton
import com.example.ui.components.ErrorStateCard
import com.example.ui.components.HeaderBadgeIndicator
import com.example.ui.components.LoadingStateCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.MintTeal
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusClosedBg
import com.example.ui.theme.StatusClosedRed
import com.example.ui.theme.StatusDutyAmber
import com.example.ui.theme.StatusDutyBg
import com.example.ui.theme.StatusOpenBg
import com.example.ui.theme.StatusOpenGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.UiState

private val ORDER_PIPELINE = listOf("confirmed", "preparing", "delivering", "delivered")

@Composable
fun OrdersScreen(
    ordersState: UiState<List<PharmacyOrder>>,
    onBack: () -> Unit,
    onUpdateOrderStatus: (orderId: String, nextStatus: String) -> Unit,
    onReorderOrder: (PharmacyOrder) -> Unit,
    onBrowseProducts: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val orders = (ordersState as? UiState.Success)?.data ?: emptyList()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        val maxContentWidth = if (maxWidth >= 600.dp) 820.dp else 600.dp
        LazyColumn(
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .fillMaxSize(),
            contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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
                        text = strings.myOrdersTitle,
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                    HeaderBadgeIndicator(
                        contentDescription = strings.myOrdersTitle,
                        badgeCount = orders.size
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            when (ordersState) {
                UiState.Loading -> item { LoadingStateCard() }
                is UiState.Error -> item {
                    ErrorStateCard(errorMessage = ordersState.message, onRetry = onRetry)
                }
                is UiState.Success -> {
                    if (orders.isEmpty()) {
                        item {
                            EmptyOrdersCard(onBrowseProducts = onBrowseProducts)
                        }
                    } else {
                        items(orders, key = { it.id }) { order ->
                            OrderTimelineCard(
                                order = order,
                                onUpdateOrderStatus = onUpdateOrderStatus,
                                onReorderOrder = onReorderOrder
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyOrdersCard(onBrowseProducts: () -> Unit) {
    val strings = LocalAppStrings.current
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = MintSoft,
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = strings.emptyOrdersTitle,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = strings.emptyOrdersBody,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(18.dp))
            Button(
                onClick = onBrowseProducts,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) {
                Text(strings.browseProducts, color = PureWhite)
            }
        }
    }
}

@Composable
private fun OrderTimelineCard(
    order: PharmacyOrder,
    onUpdateOrderStatus: (orderId: String, nextStatus: String) -> Unit,
    onReorderOrder: (PharmacyOrder) -> Unit
) {
    val strings = LocalAppStrings.current
    val methodLabel = when (order.paymentMethod) {
        "visa" -> strings.visa
        "mastercard" -> strings.mastercard
        else -> strings.cashLabel
    }
    val cardSuffix = order.cardLast4?.let { " ••$it" }.orEmpty()
    val locDisplay = PharmacyCatalog.localizeLocation(order.deliveryLocation, strings.isEnglish)
    val localizedSummary = remember(order.id, order.itemsSummary, strings.isEnglish) {
        PharmacyCatalog.formatLocalizedOrderSummary(order, strings.isEnglish, strings.productsWord)
    }
    val canReorder = remember(order.itemsSummary) {
        PharmacyCatalog.parseOrderItemsSummary(order.itemsSummary).isNotEmpty()
    }
    val currentStageIndex = ORDER_PIPELINE.indexOf(order.status)
    val nextStage = if (currentStageIndex in 0 until ORDER_PIPELINE.lastIndex) {
        ORDER_PIPELINE[currentStageIndex + 1]
    } else null

    Surface(
        shape = RoundedCornerShape(22.dp),
        color = PureWhite,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .testTag("order_card_${order.id}")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${strings.orderNumberPrefix}${order.id.takeLast(6).uppercase()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${order.itemCount} ${strings.productsWord} • $methodLabel$cardSuffix",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                Text(
                    text = strings.formatPrice(order.totalUsd),
                    style = MaterialTheme.typography.titleLarge,
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            if (localizedSummary.isNotBlank()) {
                Text(
                    text = localizedSummary,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Filled.LocationOn, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(16.dp))
                val fullAddress = listOfNotNull(
                    locDisplay,
                    order.deliveryAddress?.takeIf { it.isNotBlank() }
                ).joinToString(" — ")
                Text(
                    text = fullAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            if (!order.contactPhone.isNullOrBlank() || order.hasPrescription) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!order.contactPhone.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NeutralBackground
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Phone, contentDescription = null, tint = ForestGreen, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(order.contactPhone, style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                            }
                        }
                    }
                    if (order.hasPrescription) {
                        val rxBg = if (order.prescriptionAttached) StatusOpenBg else StatusDutyBg
                        val rxFg = if (order.prescriptionAttached) StatusOpenGreen else StatusDutyAmber
                        Surface(shape = RoundedCornerShape(10.dp), color = rxBg) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.MedicalInformation, contentDescription = null, tint = rxFg, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (order.prescriptionAttached) strings.rxAttachedBadge else strings.rxPendingBadge,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = rxFg,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            if (order.status == "cancelled") {
                Surface(shape = RoundedCornerShape(12.dp), color = StatusClosedBg, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Cancel, contentDescription = null, tint = StatusClosedRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(strings.statusCancelled, style = MaterialTheme.typography.labelLarge, color = StatusClosedRed, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                OrderStatusTimelineRow(currentStageIndex = if (currentStageIndex < 0) 0 else currentStageIndex)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (nextStage != null) {
                    OutlinedButton(
                        onClick = { onUpdateOrderStatus(order.id, nextStage) },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ForestGreen),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("order_advance_${order.id}")
                    ) {
                        Text(strings.advanceStatusAction, color = ForestGreen, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    TextButton(
                        onClick = { onUpdateOrderStatus(order.id, "cancelled") },
                        modifier = Modifier.testTag("order_cancel_${order.id}")
                    ) {
                        Text(strings.cancelOrderAction, color = StatusClosedRed, maxLines = 1)
                    }
                }
                if (canReorder) {
                    Button(
                        onClick = { onReorderOrder(order) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        modifier = Modifier
                            .then(if (nextStage == null) Modifier.fillMaxWidth() else Modifier.weight(1f))
                            .testTag("order_reorder_${order.id}")
                    ) {
                        Icon(Icons.Filled.Replay, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.reorderAction, color = PureWhite, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderStatusTimelineRow(currentStageIndex: Int) {
    val strings = LocalAppStrings.current
    val labels = listOf(
        strings.statusConfirmed,
        strings.statusPreparing,
        strings.statusDelivering,
        strings.statusDelivered
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        labels.forEachIndexed { index, label ->
            val reached = index <= currentStageIndex
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(
                                if (index == 0) androidx.compose.ui.graphics.Color.Transparent
                                else if (reached) MintTeal else BorderSubtle
                            )
                    )
                    Surface(
                        shape = CircleShape,
                        color = if (reached) MintTeal else NeutralBackground,
                        border = BorderStroke(1.5.dp, if (reached) MintTeal else BorderSubtle),
                        modifier = Modifier.size(22.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            if (reached) {
                                Icon(Icons.Filled.Check, contentDescription = null, tint = PureWhite, modifier = Modifier.size(13.dp))
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(
                                if (index == labels.lastIndex) androidx.compose.ui.graphics.Color.Transparent
                                else if (index < currentStageIndex) MintTeal else BorderSubtle
                            )
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (reached) ForestGreen else TextSecondary,
                    fontWeight = if (reached) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
