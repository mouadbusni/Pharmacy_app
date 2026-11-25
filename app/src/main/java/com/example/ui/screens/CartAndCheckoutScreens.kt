package com.example.ui.screens

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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.CartItem
import com.example.ui.components.BackCircleButton
import com.example.ui.components.HeaderCircleIconButton
import com.example.ui.components.ProductPackageArt
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.MintSoft
import com.example.ui.theme.MintTeal
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusClosedRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarmGold

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onBack: () -> Unit,
    onUpdateQty: (String, Int) -> Unit,
    onRemoveItem: (String) -> Unit,
    onProceedCheckout: () -> Unit,
    onBrowseProducts: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subtotal = cartItems.sumOf { it.priceUsd * it.quantity }
    val deliveryFee = if (cartItems.isEmpty()) 0.0 else 5.0
    val total = subtotal + deliveryFee

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
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
                    text = "السلة",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary
                )
                HeaderCircleIconButton(
                    onClick = {},
                    contentDescription = "عدد المنتجات",
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
                            text = "سلة التسوق فارغة",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "تصفح الأدوية والفيتامينات وأضف احتياجاتك للتوصيل خلال 30 دقيقة.",
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
                            Text("تصفح المنتجات", color = PureWhite)
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
                        val catalogProd = remember(item.productId) {
                            PharmacyCatalog.products.firstOrNull { it.id == item.productId }
                                ?: PharmacyCatalog.products.first()
                        }
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
                                        product = catalogProd,
                                        modifier = Modifier.size(72.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = item.productName,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = TextPrimary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.Star,
                                                contentDescription = null,
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
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${(item.priceUsd * item.quantity).toInt()}$",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    IconButton(
                                        onClick = { onRemoveItem(docId) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.DeleteOutline,
                                            contentDescription = "حذف",
                                            tint = StatusClosedRed,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            onClick = {
                                                onUpdateQty(docId, (item.quantity + 1).toInt())
                                            },
                                            shape = CircleShape,
                                            color = ForestGreen,
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Filled.Add,
                                                    contentDescription = "زيادة",
                                                    tint = PureWhite,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = item.quantity.toString(),
                                            style = MaterialTheme.typography.titleSmall,
                                            color = TextPrimary
                                        )
                                        Surface(
                                            onClick = {
                                                onUpdateQty(docId, (item.quantity - 1).toInt())
                                            },
                                            shape = CircleShape,
                                            color = NeutralBackground,
                                            border = BorderStroke(1.dp, BorderSubtle),
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = Icons.Filled.Remove,
                                                    contentDescription = "إنقاص",
                                                    tint = ForestGreen,
                                                    modifier = Modifier.size(15.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(4.dp))
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
                                SummaryRow(label = "• المجموع الفرعي:", value = "${subtotal.toInt()}$")
                                SummaryRow(label = "• التوصيل", value = "${deliveryFee.toInt()}$")
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp)
                                        .background(BorderSubtle)
                                )
                                SummaryRow(
                                    label = "• المجموع الكلي:",
                                    value = "${total.toInt()}$",
                                    highlight = true
                                )
                            }
                        }
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
                            text = "متابعة الدفع",
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
    onBack: () -> Unit,
    onConfirmOrder: (paymentMethod: String, cardLast4: String?) -> Unit,
    onOrderCompleteGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMethod by remember { mutableStateOf("visa") }
    var holderName by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("0000 0000 0000 4423") }
    var expiry by remember { mutableStateOf("17/11") }
    var cvv by remember { mutableStateOf("505") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val total = cartItems.sumOf { it.priceUsd * it.quantity } + if (cartItems.isEmpty()) 0.0 else 5.0

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
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
                    text = "الدفع",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary
                )
                HeaderCircleIconButton(
                    onClick = {},
                    contentDescription = "إجمالي الطلب",
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
                    text = "أختر طريقة الدفع",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )

                PaymentMethodOption(
                    title = "ماستر كارد",
                    badgeText = "MC",
                    badgeColor = Color(0xFFEB5757),
                    selected = selectedMethod == "mastercard",
                    onClick = { selectedMethod = "mastercard" }
                )
                PaymentMethodOption(
                    title = "بطاقة فيزا",
                    badgeText = "VISA",
                    badgeColor = Color(0xFF1A4B9C),
                    selected = selectedMethod == "visa",
                    onClick = { selectedMethod = "visa" }
                )
                PaymentMethodOption(
                    title = "عند الاستلام",
                    badgeText = "نقداً",
                    badgeColor = ForestGreen,
                    selected = selectedMethod == "cash",
                    onClick = { selectedMethod = "cash" }
                )

                if (selectedMethod != "cash") {
                    // 3D Gradient Bank Card Preview matching mockup
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
                                text = "••••  ••••  ••••  ${cardNumber.filter { it.isDigit() }.takeLast(4).ifEmpty { "4423" }}",
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
                                    text = holderName.ifBlank { "CARD HOLDER" },
                                    color = PureWhite.copy(alpha = 0.92f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = expiry.ifBlank { "17/11" },
                                    color = PureWhite.copy(alpha = 0.92f),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = holderName,
                        onValueChange = { holderName = it },
                        label = { Text("اسم حامل البطاقة") },
                        placeholder = { Text("اسم حامل البطاقة") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = cardNumber,
                        onValueChange = { cardNumber = it },
                        label = { Text("رقم البطاقة") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = expiry,
                            onValueChange = { expiry = it },
                            label = { Text("تاريخ أنتهاء الصلاحية") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForestGreen,
                                unfocusedBorderColor = BorderSubtle
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = cvv,
                            onValueChange = { cvv = it },
                            label = { Text("رمز الأمان") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForestGreen,
                                unfocusedBorderColor = BorderSubtle
                            ),
                            modifier = Modifier.weight(0.75f)
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
                                    text = "الدفع نقداً عند الاستلام",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = ForestGreen
                                )
                                Text(
                                    text = "سيصل المندوب إلى ($deliveryLocation) خلال 30 دقيقة",
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
                        val last4 = cardNumber.filter { it.isDigit() }.takeLast(4).ifEmpty { "4423" }
                        onConfirmOrder(selectedMethod, last4)
                        showSuccessDialog = true
                    },
                    enabled = cartItems.isNotEmpty(),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("confirm_order_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.CreditCard,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "تأكيد الطلب (${total.toInt()}$)",
                        style = MaterialTheme.typography.titleMedium,
                        color = PureWhite,
                        fontWeight = FontWeight.Bold
                    )
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
                    text = "تم تأكيد طلبك بنجاح",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "جاري تجهيز أدويتك في أقرب صيدلية وسيتم التوصيل إلى ($deliveryLocation) خلال 30 دقيقة.",
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
                    Text("العودة للرئيسية", color = PureWhite)
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
