package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.PackagingStyle
import com.example.data.model.Pharmacy
import com.example.data.model.Product
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
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
