package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.Pharmacy
import com.example.data.model.Product
import com.example.ui.components.BackCircleButton
import com.example.ui.components.CategoryCircleItem
import com.example.ui.components.HeaderCircleIconButton
import com.example.ui.components.PharmacyAvatarBadge
import com.example.ui.components.ProductCard
import com.example.ui.components.SectionHeaderRow
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.StatusClosedBg
import com.example.ui.theme.StatusClosedRed
import com.example.ui.theme.StatusDutyAmber
import com.example.ui.theme.StatusDutyBg
import com.example.ui.theme.WarmGold

@Composable
fun PharmacyProfileScreen(
    pharmacy: Pharmacy,
    favoriteProductIds: Set<String>,
    cartCount: Int,
    onBack: () -> Unit,
    onOpenCart: () -> Unit,
    onOpenChat: (Pharmacy) -> Unit,
    onOpenChatsList: () -> Unit,
    onOpenProduct: (Product) -> Unit,
    onOpenSearch: (String) -> Unit,
    onAddToCart: (Product) -> Unit,
    onToggleFavorite: (Product, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf("all") }

    val products = remember(selectedCategory) {
        if (selectedCategory == "all") PharmacyCatalog.products
        else PharmacyCatalog.products.filter { it.categoryId == selectedCategory }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        val isWide = maxWidth >= 600.dp
        val maxContentWidth = if (isWide) 860.dp else 600.dp
        val columns = if (isWide) 3 else 2

        LazyColumn(
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 116.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_pharmacy_storefront),
                        contentDescription = pharmacy.localizedName(strings.isEnglish),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(92.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.36f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BackCircleButton(onClick = onBack)
                        HeaderCircleIconButton(
                            onClick = onOpenCart,
                            contentDescription = strings.cartTitle,
                            badgeCount = cartCount
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingBag,
                                contentDescription = strings.cartTitle,
                                tint = ForestGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(26.dp),
                        color = ForestGreen,
                        shadowElevation = 6.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 158.dp, start = 16.dp, end = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 44.dp, bottom = 18.dp, start = 18.dp, end = 18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val scheduleBg = when {
                                    !pharmacy.openNow -> StatusClosedBg
                                    pharmacy.isAlways24h -> ForestGreenLight
                                    else -> StatusDutyBg
                                }
                                val scheduleFg = when {
                                    !pharmacy.openNow -> StatusClosedRed
                                    pharmacy.isAlways24h -> PureWhite
                                    else -> StatusDutyAmber
                                }
                                val scheduleText = when {
                                    !pharmacy.openNow -> strings.closedNow
                                    pharmacy.isAlways24h -> strings.schedule24h
                                    else -> strings.scheduleDayDuty
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = scheduleBg
                                ) {
                                    Text(
                                        text = scheduleText,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = scheduleFg,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = pharmacy.rating.toString(),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = PureWhite
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    for (i in 1..5) {
                                        val icon = when {
                                            pharmacy.rating >= i.toDouble() -> Icons.Filled.Star
                                            pharmacy.rating >= i - 0.5 -> Icons.AutoMirrored.Filled.StarHalf
                                            else -> Icons.Filled.Star
                                        }
                                        val tint = if (pharmacy.rating >= i - 0.5) WarmGold else BorderSubtle.copy(alpha = 0.4f)
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = tint,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = pharmacy.localizedName(strings.isEnglish),
                                style = MaterialTheme.typography.headlineSmall,
                                color = PureWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = PureWhite.copy(alpha = 0.8f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${pharmacy.localizedArea(strings.isEnglish)}  •  ${pharmacy.localizedDistance(strings.isEnglish)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PureWhite.copy(alpha = 0.88f)
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "${strings.pharmacyAddressLabel}: ${pharmacy.localizedAddress(strings.isEnglish)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = PureWhite.copy(alpha = 0.78f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth(0.88f)
                            ) {
                                Surface(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${pharmacy.phoneNumber}")
                                        }
                                        runCatching { context.startActivity(intent) }
                                    },
                                    shape = RoundedCornerShape(14.dp),
                                    color = PureWhite.copy(alpha = 0.16f),
                                    border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.2f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .height(48.dp)
                                        .testTag("pharmacy_call_button")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Call,
                                            contentDescription = strings.callUs,
                                            tint = PureWhite,
                                            modifier = Modifier.size(17.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = strings.callUs,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = PureWhite
                                        )
                                    }
                                }

                                Surface(
                                    onClick = { onOpenChat(pharmacy) },
                                    shape = RoundedCornerShape(14.dp),
                                    color = PureWhite.copy(alpha = 0.16f),
                                    border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.2f)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .minimumInteractiveComponentSize()
                                        .height(48.dp)
                                        .testTag("pharmacy_chat_button")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Chat,
                                            contentDescription = strings.messageUs,
                                            tint = PureWhite,
                                            modifier = Modifier.size(17.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = strings.messageUs,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = PureWhite
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = 122.dp)
                    ) {
                        PharmacyAvatarBadge(pharmacy = pharmacy, size = 74.dp)
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SectionHeaderRow(
                        title = strings.sections,
                        onActionClick = { onOpenSearch(selectedCategory) },
                        modifier = Modifier.padding(horizontal = 18.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(PharmacyCatalog.categories, key = { it.id }) { category ->
                            CategoryCircleItem(
                                category = category,
                                selected = selectedCategory == category.id,
                                onClick = { selectedCategory = category.id }
                            )
                        }
                    }
                }
            }

            item {
                SectionHeaderRow(
                    title = strings.bestSellerBadge,
                    onActionClick = { onOpenSearch(selectedCategory) },
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
            }

            items(products.chunked(columns)) { rowProducts ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowProducts.forEach { prod ->
                        val isFav = favoriteProductIds.contains(prod.id)
                        ProductCard(
                            product = prod,
                            isFavorite = isFav,
                            onCardClick = { onOpenProduct(prod) },
                            onAddClick = { onAddToCart(prod) },
                            onToggleFavorite = { onToggleFavorite(prod, isFav) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    repeat(columns - rowProducts.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .widthIn(max = maxContentWidth)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Button(
                onClick = onOpenChatsList,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ForestGreen,
                    contentColor = PureWhite
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("pharmacy_all_chats_cta")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = strings.chatPharmaciesCta,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
