package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.PureWhite

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
