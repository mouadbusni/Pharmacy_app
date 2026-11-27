package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.CartItem
import com.example.ui.components.ProductPackageArt
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.MintTeal
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusClosedBg
import com.example.ui.theme.StatusClosedRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmGold

@Composable
internal fun CartItemRow(
    item: CartItem,
    onUpdateQty: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit
) {
    val strings = LocalAppStrings.current
    val catalogProd = remember(item.productId) {
        PharmacyCatalog.products.firstOrNull { it.id == item.productId }
    }
    val displayProd = catalogProd ?: PharmacyCatalog.products.first()
    val displayName = catalogProd?.localizedName(strings.isEnglish) ?: item.productName
    val docId = item.id.ifBlank { item.productId }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = PureWhite,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                ProductPackageArt(
                    product = displayProd,
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = displayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = strings.ratingLabel,
                                tint = WarmGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = item.rating.toString(),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (displayProd.requiresPrescription) StatusClosedBg else MintSoft
                        ) {
                            Text(
                                text = if (displayProd.requiresPrescription) strings.rxShort else strings.otcShort,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (displayProd.requiresPrescription) StatusClosedRed else ForestGreen,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = strings.formatPrice(item.priceUsd * item.quantity),
                            style = MaterialTheme.typography.titleMedium,
                            color = if (displayProd.hasDiscount) ForestGreen else TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        val oldUnit = displayProd.originalPriceUsd
                        if (displayProd.hasDiscount && oldUnit != null) {
                            Text(
                                text = strings.formatPrice(oldUnit * item.quantity),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    textDecoration = TextDecoration.LineThrough
                                ),
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = { onRemoveItem(docId) },
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = Icons.Filled.DeleteOutline,
                        contentDescription = strings.removeItem,
                        tint = StatusClosedRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            onClick = { onUpdateQty(docId, (item.quantity + 1).toInt()) },
                            shape = CircleShape,
                            color = ForestGreen,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = strings.increaseQty,
                                    tint = PureWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = item.quantity.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Box(
                        modifier = Modifier.minimumInteractiveComponentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            onClick = { onUpdateQty(docId, (item.quantity - 1).toInt()) },
                            shape = CircleShape,
                            color = NeutralBackground,
                            border = BorderStroke(1.dp, BorderSubtle),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.Remove,
                                    contentDescription = strings.decreaseQty,
                                    tint = ForestGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun CartSummaryCard(
    subtotal: Double,
    deliveryFee: Double,
    total: Double,
    savings: Double = 0.0
) {
    val strings = LocalAppStrings.current
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = NeutralBackground,
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SummaryRow(label = strings.subtotalLabel, value = strings.formatPrice(subtotal))
            if (savings > 0.0) {
                SummaryRow(
                    label = strings.youSaveLabel,
                    value = "-${strings.formatPrice(savings)}",
                    highlight = false,
                    accentValue = true
                )
            }
            SummaryRow(label = strings.deliveryLabel, value = strings.formatPrice(deliveryFee))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderSubtle)
            )
            SummaryRow(
                label = strings.totalLabel,
                value = strings.formatPrice(total),
                highlight = true
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    highlight: Boolean = false,
    accentValue: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (highlight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            color = TextPrimary,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium
        )
        Text(
            text = value,
            style = if (highlight) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
            color = if (highlight || accentValue) ForestGreen else TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
internal fun PaymentMethodOption(
    title: String,
    badgeText: String,
    badgeColor: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) MintSoft.copy(alpha = 0.55f) else NeutralBackground,
        border = BorderStroke(1.dp, if (selected) ForestGreen else BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = selected,
                    onClick = onClick,
                    colors = RadioButtonDefaults.colors(selectedColor = ForestGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = badgeColor.copy(alpha = 0.14f)
            ) {
                Text(
                    text = badgeText,
                    color = badgeColor,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
internal fun CheckoutSuccessDialog(
    localizedDeliveryLocation: String,
    onTrackOrder: () -> Unit,
    onDone: () -> Unit
) {
    val strings = LocalAppStrings.current
    AlertDialog(
        onDismissRequest = onDone,
        containerColor = PureWhite,
        icon = {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = MintTeal,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = strings.orderConfirmedTitle,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "${strings.orderConfirmedBodyPrefix} ($localizedDeliveryLocation)",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onTrackOrder,
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                modifier = Modifier.testTag("checkout_track_order_btn")
            ) {
                Text(strings.trackOrderCta, color = PureWhite)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDone,
                border = BorderStroke(1.dp, BorderSubtle)
            ) {
                Text(strings.backToHome, color = TextPrimary)
            }
        }
    )
}
