package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.CartItem
import com.example.ui.components.BackCircleButton
import com.example.ui.components.HeaderBadgeIndicator
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CheckoutScreen(
    cartItems: List<CartItem>,
    deliveryLocation: String,
    isSubmitting: Boolean,
    onBack: () -> Unit,
    onConfirmOrder: (
        paymentMethod: String,
        cardLast4: String?,
        deliveryAddress: String,
        contactPhone: String,
        hasPrescription: Boolean,
        prescriptionAttached: Boolean,
        onSuccess: () -> Unit
    ) -> Unit,
    onTrackOrder: () -> Unit,
    onOrderCompleteGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    var selectedMethod by rememberSaveable { mutableStateOf("visa") }
    var streetAddress by rememberSaveable { mutableStateOf("") }
    var phoneDigits by rememberSaveable { mutableStateOf("") }
    var rxPhotoUri by rememberSaveable { mutableStateOf<String?>(null) }
    var holderName by remember { mutableStateOf("") }
    var cardDigits by remember { mutableStateOf("") }
    var expiryDigits by remember { mutableStateOf("") }
    var cvvDigits by remember { mutableStateOf("") }
    var validationAttempted by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val rxPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            rxPhotoUri = uri.toString()
        }
    }

    val hasPrescription = remember(cartItems) {
        cartItems.any { item ->
            PharmacyCatalog.products.firstOrNull { it.id == item.productId }?.requiresPrescription == true
        }
    }

    val formattedCardNumber = remember(cardDigits) {
        cardDigits.chunked(4).joinToString(" ")
    }
    val formattedExpiry = remember(expiryDigits) {
        if (expiryDigits.length <= 2) expiryDigits
        else "${expiryDigits.take(2)}/${expiryDigits.drop(2)}"
    }

    val isAddressValid = streetAddress.trim().length >= 4
    val isPhoneValid = phoneDigits.count { it.isDigit() } >= 9
    val isRxValid = !hasPrescription || !rxPhotoUri.isNullOrBlank()
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
                HeaderBadgeIndicator(
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
                CheckoutOrderSummaryCard(
                    cartItems = cartItems,
                    total = total
                )

                CheckoutDeliveryFields(
                    streetAddress = streetAddress,
                    onStreetAddressChange = { streetAddress = it },
                    phoneDigits = phoneDigits,
                    onPhoneDigitsChange = { phoneDigits = it },
                    validationAttempted = validationAttempted,
                    isAddressValid = isAddressValid,
                    isPhoneValid = isPhoneValid
                )

                if (hasPrescription) {
                    CheckoutRxVerificationCard(
                        rxPhotoUri = rxPhotoUri,
                        validationAttempted = validationAttempted,
                        onPickPhotoClick = {
                            rxPhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                    )
                }

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
                    onClick = { selectedMethod = "cash" }
                )

                if (selectedMethod != "cash") {
                    CheckoutCardPreview(
                        selectedMethod = selectedMethod,
                        cardDigits = cardDigits,
                        holderName = holderName,
                        formattedExpiry = formattedExpiry
                    )
                    CheckoutCardFields(
                        holderName = holderName,
                        onHolderNameChange = { holderName = it },
                        formattedCardNumber = formattedCardNumber,
                        onCardDigitsChange = { cardDigits = it },
                        formattedExpiry = formattedExpiry,
                        onExpiryDigitsChange = { expiryDigits = it },
                        cvvDigits = cvvDigits,
                        onCvvDigitsChange = { cvvDigits = it },
                        validationAttempted = validationAttempted,
                        isHolderValid = isHolderValid,
                        isCardValid = isCardValid,
                        isExpiryValid = isExpiryValid,
                        isCvvValid = isCvvValid
                    )
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
                        validationAttempted = true
                        val cardFieldsOk = selectedMethod == "cash" ||
                            (isHolderValid && isCardValid && isExpiryValid && isCvvValid)
                        if (!isAddressValid || !isPhoneValid || !isRxValid || !cardFieldsOk) {
                            return@Button
                        }
                        val last4 = if (selectedMethod == "cash") null else cardDigits.takeLast(4)
                        onConfirmOrder(
                            selectedMethod,
                            last4,
                            streetAddress.trim(),
                            phoneDigits.trim(),
                            hasPrescription,
                            !rxPhotoUri.isNullOrBlank()
                        ) {
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
        CheckoutSuccessDialog(
            localizedDeliveryLocation = localizedDeliveryLocation,
            onTrackOrder = {
                showSuccessDialog = false
                onTrackOrder()
            },
            onDone = {
                showSuccessDialog = false
                onOrderCompleteGoHome()
            }
        )
    }
}
