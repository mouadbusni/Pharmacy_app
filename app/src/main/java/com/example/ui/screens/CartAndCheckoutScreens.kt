package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.CartItem
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusClosedBg
import com.example.ui.theme.StatusClosedRed
import com.example.ui.theme.StatusOpenBg
import com.example.ui.theme.StatusOpenGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
internal fun CheckoutCardPreview(
    selectedMethod: String,
    cardDigits: String,
    holderName: String,
    formattedExpiry: String
) {
    val strings = LocalAppStrings.current
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
}

@Composable
internal fun CheckoutCardFields(
    holderName: String,
    onHolderNameChange: (String) -> Unit,
    formattedCardNumber: String,
    onCardDigitsChange: (String) -> Unit,
    formattedExpiry: String,
    onExpiryDigitsChange: (String) -> Unit,
    cvvDigits: String,
    onCvvDigitsChange: (String) -> Unit,
    validationAttempted: Boolean,
    isHolderValid: Boolean,
    isCardValid: Boolean,
    isExpiryValid: Boolean,
    isCvvValid: Boolean
) {
    val strings = LocalAppStrings.current
    OutlinedTextField(
        value = holderName,
        onValueChange = onHolderNameChange,
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
            onCardDigitsChange(input.filter { it.isDigit() }.take(16))
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
                onExpiryDigitsChange(input.filter { it.isDigit() }.take(4))
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
                onCvvDigitsChange(input.filter { it.isDigit() }.take(4))
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
}

@Composable
internal fun CheckoutDeliveryFields(
    streetAddress: String,
    onStreetAddressChange: (String) -> Unit,
    phoneDigits: String,
    onPhoneDigitsChange: (String) -> Unit,
    validationAttempted: Boolean,
    isAddressValid: Boolean,
    isPhoneValid: Boolean
) {
    val strings = LocalAppStrings.current
    Text(
        text = strings.deliveryDetailsTitle,
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary
    )
    OutlinedTextField(
        value = streetAddress,
        onValueChange = onStreetAddressChange,
        label = { Text(strings.streetAddressLabel) },
        placeholder = { Text(strings.streetAddressPlaceholder) },
        leadingIcon = {
            Icon(Icons.Filled.HomeWork, contentDescription = null, tint = ForestGreen)
        },
        isError = validationAttempted && !isAddressValid,
        supportingText = if (validationAttempted && !isAddressValid) {
            { Text(strings.errStreetAddress) }
        } else null,
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ForestGreen,
            unfocusedBorderColor = BorderSubtle
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("checkout_address_input")
    )
    OutlinedTextField(
        value = phoneDigits,
        onValueChange = { input ->
            onPhoneDigitsChange(input.filter { it.isDigit() || it == '+' }.take(14))
        },
        label = { Text(strings.contactPhoneLabel) },
        placeholder = { Text(strings.contactPhonePlaceholder) },
        leadingIcon = {
            Icon(Icons.Filled.Phone, contentDescription = null, tint = ForestGreen)
        },
        isError = validationAttempted && !isPhoneValid,
        supportingText = if (validationAttempted && !isPhoneValid) {
            { Text(strings.errContactPhone) }
        } else null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ForestGreen,
            unfocusedBorderColor = BorderSubtle
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("checkout_phone_input")
    )
}

@Composable
internal fun CheckoutRxVerificationCard(
    rxPhotoUri: String?,
    validationAttempted: Boolean,
    onPickPhotoClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    val missingError = validationAttempted && rxPhotoUri.isNullOrBlank()
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (rxPhotoUri != null) StatusOpenBg else StatusClosedBg.copy(alpha = 0.72f),
        border = BorderStroke(
            1.dp,
            when {
                rxPhotoUri != null -> StatusOpenGreen.copy(alpha = 0.45f)
                missingError -> StatusClosedRed
                else -> StatusClosedRed.copy(alpha = 0.35f)
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (rxPhotoUri != null) Icons.Filled.CheckCircle else Icons.Filled.MedicalInformation,
                    contentDescription = null,
                    tint = if (rxPhotoUri != null) StatusOpenGreen else StatusClosedRed,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = strings.rxVerificationTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (rxPhotoUri != null) StatusOpenGreen else StatusClosedRed,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = if (rxPhotoUri != null) strings.rxAttachedSuccess else strings.rxRequiredNotice,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary
            )
            if (rxPhotoUri != null) {
                AsyncImage(
                    model = rxPhotoUri,
                    contentDescription = strings.rxAttachedSuccess,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(124.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
            OutlinedButton(
                onClick = onPickPhotoClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreen),
                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("checkout_attach_rx_button")
            ) {
                Icon(Icons.Filled.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (rxPhotoUri != null) strings.changePhoto else strings.rxAttachHint,
                    fontWeight = FontWeight.Bold
                )
            }
            if (missingError) {
                Text(
                    text = strings.errPrescriptionRequired,
                    style = MaterialTheme.typography.labelSmall,
                    color = StatusClosedRed
                )
            }
        }
    }
}

@Composable
internal fun CheckoutOrderSummaryCard(
    cartItems: List<CartItem>,
    total: Double
) {
    val strings = LocalAppStrings.current
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = NeutralBackground,
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = strings.orderSummaryTitle,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            cartItems.forEach { item ->
                val prod = PharmacyCatalog.products.firstOrNull { it.id == item.productId }
                val name = prod?.localizedName(strings.isEnglish) ?: item.productName
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${item.quantity}× $name",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.formatPrice(item.priceUsd * item.quantity),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderSubtle)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = strings.totalLabel,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = strings.formatPrice(total),
                    style = MaterialTheme.typography.titleMedium,
                    color = ForestGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

