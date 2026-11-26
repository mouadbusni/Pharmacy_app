package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.PackagingStyle
import com.example.data.model.Pharmacy
import com.example.data.model.PharmacyCategory
import com.example.data.model.PharmacyScheduleFilter
import com.example.data.model.PrescriptionFilter
import com.example.data.model.Product
import com.example.data.update.AppUpdateInfo
import com.example.data.update.AppUpdateState
import com.example.ui.theme.AppStrings
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
import com.example.ui.theme.WarmGold

@Composable
fun HeaderCircleIconButton(
    onClick: () -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    backgroundColor: Color = PureWhite,
    iconTint: Color = ForestGreen,
    icon: @Composable () -> Unit
) {
    Box(
        modifier = modifier.minimumInteractiveComponentSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = backgroundColor,
            shadowElevation = 2.dp,
            border = BorderStroke(1.dp, BorderSubtle.copy(alpha = 0.7f)),
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                icon()
            }
        }
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(MintTeal),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (badgeCount > 9) "9+" else badgeCount.toString(),
                    color = PureWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun BackCircleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    darkSurface: Boolean = false
) {
    val strings = LocalAppStrings.current
    HeaderCircleIconButton(
        onClick = onClick,
        contentDescription = strings.back,
        backgroundColor = if (darkSurface) PureWhite.copy(alpha = 0.22f) else PureWhite,
        iconTint = if (darkSurface) PureWhite else ForestGreen,
        modifier = modifier.testTag("back_button")
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = strings.back,
            tint = if (darkSurface) PureWhite else ForestGreen,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SectionHeaderRow(
    title: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val resolvedAction = actionLabel ?: if (onActionClick != null) strings.seeMore else null
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary
        )
        if (resolvedAction != null && onActionClick != null) {
            Text(
                text = resolvedAction,
                style = MaterialTheme.typography.labelLarge,
                color = ForestGreen,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable(onClick = onActionClick)
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
fun CategoryCircleItem(
    category: PharmacyCategory,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val accent = Color(category.accentColorHex)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(62.dp)
                .clip(CircleShape)
                .background(if (selected) MintSoft else PureWhite)
                .border(
                    width = if (selected) 2.dp else 1.dp,
                    color = if (selected) ForestGreen else BorderSubtle,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            CategoryVectorArt(categoryId = category.id, accent = accent)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = category.localizedTitle(strings.isEnglish),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) ForestGreen else TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CategoryVectorArt(categoryId: String, accent: Color) {
    Canvas(modifier = Modifier.size(34.dp)) {
        when (categoryId) {
            "all" -> {
                drawRoundRect(
                    color = ForestGreen,
                    topLeft = Offset(size.width * 0.12f, size.height * 0.12f),
                    size = Size(size.width * 0.76f, size.height * 0.76f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                drawRect(
                    color = PureWhite,
                    topLeft = Offset(size.width * 0.43f, size.height * 0.27f),
                    size = Size(size.width * 0.14f, size.height * 0.46f)
                )
                drawRect(
                    color = PureWhite,
                    topLeft = Offset(size.width * 0.27f, size.height * 0.43f),
                    size = Size(size.width * 0.46f, size.height * 0.14f)
                )
            }
            "analgesics" -> {
                drawRoundRect(
                    color = Color(0xFFFEE2E2),
                    topLeft = Offset(size.width * 0.14f, size.height * 0.28f),
                    size = Size(size.width * 0.72f, size.height * 0.44f),
                    cornerRadius = CornerRadius(24f, 24f)
                )
                drawRoundRect(
                    color = accent,
                    topLeft = Offset(size.width * 0.48f, size.height * 0.28f),
                    size = Size(size.width * 0.38f, size.height * 0.44f),
                    cornerRadius = CornerRadius(22f, 22f)
                )
                drawCircle(color = PureWhite, radius = 3.5f, center = Offset(size.width * 0.66f, size.height * 0.44f))
            }
            "antibiotics" -> {
                drawRoundRect(
                    color = Color(0xFFD5ECE6),
                    topLeft = Offset(size.width * 0.24f, size.height * 0.24f),
                    size = Size(size.width * 0.52f, size.height * 0.66f),
                    cornerRadius = CornerRadius(14f, 14f)
                )
                drawRoundRect(
                    color = accent,
                    topLeft = Offset(size.width * 0.30f, size.height * 0.08f),
                    size = Size(size.width * 0.40f, size.height * 0.18f),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                drawRect(
                    color = ForestGreen,
                    topLeft = Offset(size.width * 0.45f, size.height * 0.44f),
                    size = Size(size.width * 0.10f, size.height * 0.26f)
                )
                drawRect(
                    color = ForestGreen,
                    topLeft = Offset(size.width * 0.37f, size.height * 0.52f),
                    size = Size(size.width * 0.26f, size.height * 0.10f)
                )
            }
            "chronic" -> {
                drawRoundRect(
                    color = Color(0xFFEDE9FE),
                    topLeft = Offset(size.width * 0.14f, size.height * 0.20f),
                    size = Size(size.width * 0.72f, size.height * 0.60f),
                    cornerRadius = CornerRadius(14f, 14f)
                )
                drawCircle(color = accent, radius = 6f, center = Offset(size.width * 0.36f, size.height * 0.44f))
                drawCircle(color = accent, radius = 6f, center = Offset(size.width * 0.64f, size.height * 0.44f))
                drawRoundRect(
                    color = accent,
                    topLeft = Offset(size.width * 0.28f, size.height * 0.58f),
                    size = Size(size.width * 0.44f, size.height * 0.12f),
                    cornerRadius = CornerRadius(6f, 6f)
                )
            }
            "respiratory" -> {
                drawRoundRect(
                    color = Color(0xFFE0F2FE),
                    topLeft = Offset(size.width * 0.26f, size.height * 0.16f),
                    size = Size(size.width * 0.36f, size.height * 0.68f),
                    cornerRadius = CornerRadius(12f, 12f)
                )
                drawRoundRect(
                    color = accent,
                    topLeft = Offset(size.width * 0.26f, size.height * 0.58f),
                    size = Size(size.width * 0.56f, size.height * 0.26f),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                drawCircle(color = accent.copy(alpha = 0.7f), radius = 3.5f, center = Offset(size.width * 0.84f, size.height * 0.44f))
            }
            "vitamins" -> {
                drawRoundRect(
                    color = accent,
                    topLeft = Offset(size.width * 0.20f, size.height * 0.20f),
                    size = Size(size.width * 0.60f, size.height * 0.34f),
                    cornerRadius = CornerRadius(24f, 24f)
                )
                drawRoundRect(
                    color = Color(0xFF4FB9F0),
                    topLeft = Offset(size.width * 0.46f, size.height * 0.20f),
                    size = Size(size.width * 0.34f, size.height * 0.34f),
                    cornerRadius = CornerRadius(20f, 20f)
                )
                drawCircle(color = MintTeal, radius = 4.5f, center = Offset(size.width * 0.35f, size.height * 0.74f))
                drawCircle(color = WarmGold, radius = 4f, center = Offset(size.width * 0.55f, size.height * 0.78f))
                drawCircle(color = accent, radius = 3.5f, center = Offset(size.width * 0.72f, size.height * 0.68f))
            }
            else -> {
                drawRoundRect(
                    color = Color(0xFFE7F2FA),
                    topLeft = Offset(size.width * 0.14f, size.height * 0.28f),
                    size = Size(size.width * 0.72f, size.height * 0.56f),
                    cornerRadius = CornerRadius(12f, 12f)
                )
                drawRect(
                    color = accent,
                    topLeft = Offset(size.width * 0.44f, size.height * 0.42f),
                    size = Size(size.width * 0.12f, size.height * 0.28f)
                )
                drawRect(
                    color = accent,
                    topLeft = Offset(size.width * 0.36f, size.height * 0.50f),
                    size = Size(size.width * 0.28f, size.height * 0.12f)
                )
            }
        }
    }
}

@Composable
fun ProductPackageArt(
    product: Product,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val drawableRes = when (product.packagingStyle) {
        PackagingStyle.DOLIPRANE_CRIMSON -> R.drawable.img_med_doliprane_1795642686983
        PackagingStyle.AUGMENTIN_TEAL -> R.drawable.img_med_augmentin_1795642687518
        PackagingStyle.DOLIGRIPPE_BLUE -> R.drawable.img_med_doligrippe_1795642688470
        PackagingStyle.GLUCOPHAGE_PURPLE -> R.drawable.img_med_glucophage_1795642711277
        PackagingStyle.VENTOLINE_SKY -> R.drawable.img_med_ventoline_1795642689541
        PackagingStyle.VITAC_CITRUS -> R.drawable.img_med_vitac_1795642689580
        PackagingStyle.VOLTAREN_AMBER -> R.drawable.img_med_voltaren_1795642713306
        PackagingStyle.LEVOTHYROX_ROSE -> R.drawable.img_med_levothyrox_1795642712902
        PackagingStyle.LOSEC_INDIGO -> R.drawable.img_med_omeprazole_1795642713391
        PackagingStyle.AUREOMYCINE_EMERALD -> R.drawable.img_med_aureomycine_1795642713154
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(PureWhite)
            .border(1.dp, BorderSubtle.copy(alpha = 0.75f), RoundedCornerShape(14.dp))
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = product.localizedName(strings.isEnglish),
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ProductCard(
    product: Product,
    isFavorite: Boolean,
    onCardClick: () -> Unit,
    onAddClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    Card(
        onClick = onCardClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, BorderSubtle.copy(alpha = 0.7f)),
        modifier = modifier.testTag("product_card_${product.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(108.dp)
            ) {
                ProductPackageArt(
                    product = product,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .minimumInteractiveComponentSize()
                        .testTag("fav_btn_${product.id}")
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PureWhite.copy(alpha = 0.9f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (isFavorite) strings.removeFavorite else strings.addFavorite,
                                tint = if (isFavorite) StatusClosedRed else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (product.requiresPrescription) StatusClosedBg else MintSoft,
                    border = BorderStroke(
                        0.8.dp,
                        if (product.requiresPrescription) StatusClosedRed.copy(alpha = 0.35f) else ForestGreen.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = if (product.requiresPrescription) strings.rxShort else strings.otcShort,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (product.requiresPrescription) StatusClosedRed else ForestGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.localizedName(strings.isEnglish),
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = strings.ratingLabel,
                            tint = WarmGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = product.rating.toString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = strings.formatPrice(product.priceUsd),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier.minimumInteractiveComponentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        onClick = onAddClick,
                        shape = RoundedCornerShape(10.dp),
                        color = ForestGreen,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("add_cart_${product.id}")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = strings.addToCartShort,
                                tint = PureWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PharmacyAvatarBadge(
    pharmacy: Pharmacy,
    size: Dp = 52.dp,
    modifier: Modifier = Modifier
) {
    val accent = Color(pharmacy.accentColorHex)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(PureWhite)
            .border(1.5.dp, accent.copy(alpha = 0.28f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_pharmacy_cross),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(size * 0.68f)
        )
    }
}

@Composable
fun PharmacyListCard(
    pharmacy: Pharmacy,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, BorderSubtle.copy(alpha = 0.7f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("pharmacy_card_${pharmacy.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                PharmacyAvatarBadge(pharmacy = pharmacy, size = 54.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = pharmacy.localizedName(strings.isEnglish),
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = pharmacy.localizedArea(strings.isEnglish),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = pharmacy.localizedAddress(strings.isEnglish),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val badgeBg = when {
                        !pharmacy.openNow -> StatusClosedBg
                        pharmacy.isAlways24h -> StatusOpenBg
                        else -> StatusDutyBg
                    }
                    val badgeFg = when {
                        !pharmacy.openNow -> StatusClosedRed
                        pharmacy.isAlways24h -> StatusOpenGreen
                        else -> StatusDutyAmber
                    }
                    val badgeLabel = when {
                        !pharmacy.openNow -> strings.closedNow
                        pharmacy.isAlways24h -> strings.schedule24h
                        else -> strings.scheduleDayDuty
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = badgeBg
                    ) {
                        Text(
                            text = "• $badgeLabel",
                            style = MaterialTheme.typography.labelSmall,
                            color = badgeFg,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = NeutralBackground
            ) {
                Text(
                    text = pharmacy.localizedDeliveryTime(strings.isEnglish),
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun PharmacyScheduleFilterRow(
    selected: PharmacyScheduleFilter,
    onSelect: (PharmacyScheduleFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val options = listOf(
        PharmacyScheduleFilter.ALL to strings.filterAllPharmacies,
        PharmacyScheduleFilter.ALWAYS_24H to strings.schedule24h,
        PharmacyScheduleFilter.DAY_DUTY to strings.scheduleDayDuty
    )
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options, key = { it.first.name }) { (filter, label) ->
            val isSelected = selected == filter
            val activeBg = when (filter) {
                PharmacyScheduleFilter.DAY_DUTY -> StatusDutyBg
                else -> MintSoft
            }
            val activeFg = when (filter) {
                PharmacyScheduleFilter.DAY_DUTY -> StatusDutyAmber
                else -> ForestGreen
            }
            Surface(
                onClick = { onSelect(filter) },
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) activeBg else PureWhite,
                border = BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) activeFg else BorderSubtle
                ),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("schedule_filter_${filter.name.lowercase()}")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) activeFg else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun PrescriptionFilterRow(
    selected: PrescriptionFilter,
    onSelect: (PrescriptionFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val options = listOf(
        PrescriptionFilter.ALL to strings.filterAllProducts,
        PrescriptionFilter.OTC to strings.otcLabel,
        PrescriptionFilter.RX_REQUIRED to strings.prescriptionRequiredLabel
    )
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options, key = { it.first.name }) { (filter, label) ->
            val isSelected = selected == filter
            val activeBg = when (filter) {
                PrescriptionFilter.RX_REQUIRED -> StatusClosedBg
                else -> MintSoft
            }
            val activeFg = when (filter) {
                PrescriptionFilter.RX_REQUIRED -> StatusClosedRed
                else -> ForestGreen
            }
            Surface(
                onClick = { onSelect(filter) },
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) activeBg else PureWhite,
                border = BorderStroke(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    color = if (isSelected) activeFg else BorderSubtle
                ),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("prescription_filter_${filter.name.lowercase()}")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) activeFg else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun HomeQuickShortcutsRow(
    onOpenPharmaciesList: () -> Unit,
    onOpen24hPharmacies: () -> Unit,
    onOpenChats: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item(key = "shortcut_pharmacies") {
            QuickShortcutPill(
                label = strings.quickDirectory,
                icon = Icons.Filled.LocalPharmacy,
                accentColor = ForestGreen,
                containerColor = PureWhite,
                onClick = onOpenPharmaciesList,
                modifier = Modifier.testTag("home_shortcut_directory")
            )
        }
        item(key = "shortcut_24h") {
            QuickShortcutPill(
                label = strings.quickOpen24h,
                icon = Icons.Filled.AccessTime,
                accentColor = StatusOpenGreen,
                containerColor = PureWhite,
                onClick = onOpen24hPharmacies,
                modifier = Modifier.testTag("home_shortcut_24h")
            )
        }
        item(key = "shortcut_chats") {
            QuickShortcutPill(
                label = strings.quickPharmacistChat,
                icon = Icons.AutoMirrored.Outlined.Chat,
                accentColor = ForestGreen,
                containerColor = MintSoft,
                onClick = onOpenChats,
                modifier = Modifier.testTag("home_shortcut_chats")
            )
        }
    }
}

@Composable
private fun QuickShortcutPill(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = containerColor,
        border = BorderStroke(1.dp, BorderSubtle),
        shadowElevation = 1.dp,
        modifier = modifier.minimumInteractiveComponentSize()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

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
