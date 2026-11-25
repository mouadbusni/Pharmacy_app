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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.CartItem
import com.example.ui.components.BackCircleButton
import com.example.ui.components.ErrorStateCard
import com.example.ui.components.HeaderCircleIconButton
import com.example.ui.components.LoadingStateCard
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
import com.example.viewmodel.UiState

@Composable
fun CartScreen(
    cartState: UiState<List<CartItem>>,
    onBack: () -> Unit,
    onUpdateQty: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onProceedCheckout: () -> Unit,
    onBrowseProducts: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val cartItems = (cartState as? UiState.Success)?.data ?: emptyList()
    val subtotal = cartItems.sumOf { it.priceUsd * it.quantity }
    val deliveryFee = if (cartItems.isEmpty()) 0.0 else PharmacyCatalog.DELIVERY_FEE_MAD
    val total = subtotal + deliveryFee

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite),
        contentAlignment = Alignment.TopCenter
    ) {
        val isWide = maxWidth >= 600.dp
        val maxContentWidth = if (isWide) 900.dp else 600.dp

        Column(
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackCircleButton(onClick = onBack)
                Text(
                    text = strings.cartTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary
                )
                HeaderCircleIconButton(
                    onClick = {},
                    contentDescription = strings.itemCountDesc,
                    badgeCount = cartItems.sumOf { it.quantity }.toInt()
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingBag,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            when (cartState) {
                UiState.Loading -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingStateCard()
                    }
                }
                is UiState.Error -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorStateCard(
                            errorMessage = cartState.message,
                            onRetry = onRetry
                        )
                    }
                }
                is UiState.Success -> {
                    if (cartItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(
                                    shape = CircleShape,
                                    color = MintSoft,
                                    modifier = Modifier.size(84.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.ShoppingBag,
                                            contentDescription = null,
                                            tint = ForestGreen,
                                            modifier = Modifier.size(38.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = strings.emptyCartTitle,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextPrimary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = strings.emptyCartBody,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(20.dp))
                                Button(
                                    onClick = onBrowseProducts,
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                                ) {
                                    Text(strings.browseProducts, color = PureWhite)
                                }
                            }
                        }
                    } else if (isWide) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            LazyColumn(
                                modifier = Modifier.weight(1.35f),
                                contentPadding = PaddingValues(bottom = 24.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(cartItems, key = { it.productId.ifBlank { it.id } }) { item ->
                                    CartItemRow(
                                        item = item,
                                        onUpdateQty = onUpdateQty,
                                        onRemoveItem = onRemoveItem
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .weight(0.85f)
                                    .navigationBarsPadding(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CartSummaryCard(
                                    subtotal = subtotal,
                                    deliveryFee = deliveryFee,
                                    total = total
                                )
                                Button(
                                    onClick = onProceedCheckout,
                                    shape = RoundedCornerShape(18.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(54.dp)
                                        .testTag("proceed_checkout_button")
                                ) {
                                    Text(
                                        text = strings.proceedCheckout,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = PureWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(cartItems, key = { it.productId.ifBlank { it.id } }) { item ->
                                CartItemRow(
                                    item = item,
                                    onUpdateQty = onUpdateQty,
                                    onRemoveItem = onRemoveItem
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(4.dp))
                                CartSummaryCard(
                                    subtotal = subtotal,
                                    deliveryFee = deliveryFee,
                                    total = total
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(horizontal = 18.dp, vertical = 14.dp)
                        ) {
                            Button(
                                onClick = onProceedCheckout,
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("proceed_checkout_button")
                            ) {
                                Text(
                                    text = strings.proceedCheckout,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PureWhite,
                                    fontWeight = FontWeight.Bold
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
private fun CartItemRow(
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
                    Text(
                        text = strings.formatPrice(item.priceUsd * item.quantity),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
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
private fun CartSummaryCard(
    subtotal: Double,
    deliveryFee: Double,
    total: Double
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
private fun SummaryRow(label: String, value: String, highlight: Boolean = false) {
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
            color = if (highlight) ForestGreen else TextPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CheckoutScreen(
    cartItems: List<CartItem>,
    deliveryLocation: String,
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onConfirmOrder: (paymentMethod: String, cardLast4: String?, onSuccess: () -> Unit) -> Unit,
    onOrderCompleteGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var selectedMethod by remember { mutableStateOf("visa") }
    var holderName by remember { mutableStateOf("") }
    var cardDigits by remember { mutableStateOf("") }
    var expiryDigits by remember { mutableStateOf("") }
    var cvvDigits by remember { mutableStateOf("") }
    var validationAttempted by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val formattedCardNumber = remember(cardDigits) {
        cardDigits.chunked(4).joinToString(" ")
    }
    val formattedExpiry = remember(expiryDigits) {
        if (expiryDigits.length <= 2) expiryDigits
        else "${expiryDigits.take(2)}/${expiryDigits.drop(2)}"
    }

    val isHolderValid = holderName.trim().length >= 2
    val isCardValid = cardDigits.length == 16
    val expiryMonth = expiryDigits.take(2).toIntOrNull() ?: 0
    val isExpiryValid = expiryDigits.length == 4 && expiryMonth in 1..12
    val isCvvValid = cvvDigits.length in 3..4

    val total = cartItems.sumOf { it.priceUsd * it.quantity } +
        if (cartItems.isEmpty()) 0.0 else PharmacyCatalog.DELIVERY_FEE_MAD
    val localizedDeliveryLocation = PharmacyCatalog.localizeLocation(deliveryLocation, strings.isEnglish)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite),
        contentAlignment = Alignment.TopCenter
    ) {
        val maxContentWidth = if (maxWidth >= 600.dp) 700.dp else 600.dp

        Column(
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BackCircleButton(onClick = onBack)
                Text(
                    text = strings.checkoutTitle,
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary
                )
                HeaderCircleIconButton(
                    onClick = {},
                    contentDescription = strings.orderTotalDesc,
                    badgeCount = cartItems.sumOf { it.quantity }.toInt()
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingBag,
                        contentDescription = null,
                        tint = ForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = strings.choosePaymentMethod,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )

                PaymentMethodOption(
                    title = strings.mastercard,
                    badgeText = "MC",
                    badgeColor = Color(0xFFEB5757),
                    selected = selectedMethod == "mastercard",
                    onClick = { selectedMethod = "mastercard" }
                )
                PaymentMethodOption(
                    title = strings.visa,
                    badgeText = "VISA",
                    badgeColor = Color(0xFF1A4B9C),
                    selected = selectedMethod == "visa",
                    onClick = { selectedMethod = "visa" }
                )
                PaymentMethodOption(
                    title = strings.cashOnDelivery,
                    badgeText = strings.cashLabel,
                    badgeColor = ForestGreen,
                    selected = selectedMethod == "cash",
                    onClick = {
                        selectedMethod = "cash"
                        validationAttempted = false
                    }
                )

                if (selectedMethod != "cash") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(178.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0A4738),
                                        Color(0xFF287D6B),
                                        Color(0xFF7CAFA4)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = PureWhite.copy(alpha = 0.25f),
                                    modifier = Modifier.size(width = 38.dp, height = 26.dp)
                                ) {}
                                Text(
                                    text = if (selectedMethod == "visa") "VISA" else "Mastercard",
                                    color = PureWhite,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Text(
                                text = "••••  ••••  ••••  ${cardDigits.takeLast(4).padStart(4, '•')}",
                                color = PureWhite,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = holderName.ifBlank { strings.cardHolderDefault },
                                    color = PureWhite.copy(alpha = 0.92f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = formattedExpiry.ifBlank { strings.expiryPlaceholder },
                                    color = PureWhite.copy(alpha = 0.92f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = holderName,
                        onValueChange = { holderName = it },
                        label = { Text(strings.cardHolderLabel) },
                        placeholder = { Text(strings.cardHolderPlaceholder) },
                        isError = validationAttempted && !isHolderValid,
                        supportingText = if (validationAttempted && !isHolderValid) {
                            { Text(strings.errCardHolder) }
                        } else null,
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_holder_input")
                    )

                    OutlinedTextField(
                        value = formattedCardNumber,
                        onValueChange = { input ->
                            cardDigits = input.filter { it.isDigit() }.take(16)
                        },
                        label = { Text(strings.cardNumberLabel) },
                        placeholder = { Text(strings.cardNumberPlaceholder) },
                        isError = validationAttempted && !isCardValid,
                        supportingText = if (validationAttempted && !isCardValid) {
                            { Text(strings.errCardNumber) }
                        } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("checkout_card_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = formattedExpiry,
                            onValueChange = { input ->
                                expiryDigits = input.filter { it.isDigit() }.take(4)
                            },
                            label = { Text(strings.expiryLabel) },
                            placeholder = { Text(strings.expiryPlaceholder) },
                            isError = validationAttempted && !isExpiryValid,
                            supportingText = if (validationAttempted && !isExpiryValid) {
                                { Text(strings.errExpiry) }
                            } else null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForestGreen,
                                unfocusedBorderColor = BorderSubtle
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("checkout_expiry_input")
                        )
                        OutlinedTextField(
                            value = cvvDigits,
                            onValueChange = { input ->
                                cvvDigits = input.filter { it.isDigit() }.take(4)
                            },
                            label = { Text(strings.cvvLabel) },
                            placeholder = { Text(strings.cvvPlaceholder) },
                            isError = validationAttempted && !isCvvValid,
                            supportingText = if (validationAttempted && !isCvvValid) {
                                { Text(strings.errCvv) }
                            } else null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForestGreen,
                                unfocusedBorderColor = BorderSubtle
                            ),
                            modifier = Modifier
                                .weight(0.75f)
                                .testTag("checkout_cvv_input")
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MintSoft,
                        border = BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalShipping,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = strings.cashPaymentTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ForestGreen
                                )
                                Text(
                                    text = "${strings.cashPaymentEtaPrefix} ($localizedDeliveryLocation)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                Button(
                    onClick = {
                        if (selectedMethod != "cash") {
                            validationAttempted = true
                            if (!isHolderValid || !isCardValid || !isExpiryValid || !isCvvValid) {
                                return@Button
                            }
                        }
                        val last4 = if (selectedMethod == "cash") null else cardDigits.takeLast(4)
                        onConfirmOrder(selectedMethod, last4) {
                            showSuccessDialog = true
                        }
                    },
                    enabled = cartItems.isNotEmpty() && !isSubmitting,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("confirm_order_button")
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            color = PureWhite,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.CreditCard,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${strings.confirmOrder} (${strings.formatPrice(total)})",
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onOrderCompleteGoHome()
            },
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
                    onClick = {
                        showSuccessDialog = false
                        onOrderCompleteGoHome()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                ) {
                    Text(strings.backToHome, color = PureWhite)
                }
            }
        )
    }
}

@Composable
private fun PaymentMethodOption(
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
