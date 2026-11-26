package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.BuildConfig
import com.example.R
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.PharmacyOrder
import com.example.data.model.UserProfile
import com.example.data.update.AppUpdateInfo
import com.example.data.update.AppUpdateState
import com.example.data.update.GitHubReleaseChecker
import com.example.data.update.UpdatePreferencesStore
import com.example.ui.auth.signOut
import com.example.ui.components.BackCircleButton
import com.example.ui.components.ErrorStateCard
import com.example.ui.components.HeaderCircleIconButton
import com.example.ui.components.LoadingStateCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.MintTeal
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.UiState

@Composable
fun ProfileScreen(
    profile: UserProfile?,
    ordersState: UiState<List<PharmacyOrder>>,
    isAdminMode: Boolean = false,
    onBack: () -> Unit,
    onToggleLanguage: (String) -> Unit,
    onToggleNotifications: (Boolean) -> Unit,
    onRetry: () -> Unit,
    onSignedOut: () -> Unit,
    configuredRepo: String = "",
    updateState: AppUpdateState = AppUpdateState.Idle,
    onSaveRepoAndCheck: (String) -> Unit = {},
    onCheckUpdates: () -> Unit = {},
    onDownloadUpdate: (AppUpdateInfo) -> Unit = {},
    scrollState: ScrollState = rememberScrollState(),
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showHelpOrdersDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showAppLinkDialog by remember { mutableStateOf(false) }
    var showUpdatesDialog by remember { mutableStateOf(false) }
    var repoInput by remember(configuredRepo, showUpdatesDialog) { mutableStateOf(configuredRepo) }
    var repoValidationError by remember { mutableStateOf(false) }
    val appShareUrl = stringResource(id = R.string.app_share_url)

    val currentLanguage = profile?.language ?: "ar"
    val notificationsEnabled = profile?.notificationsEnabled ?: true
    val orders = (ordersState as? UiState.Success)?.data ?: emptyList()
    val defaultLocDisplay = PharmacyCatalog.localizeLocation(
        profile?.selectedLocation ?: "الموقع الحالي",
        strings.isEnglish
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite),
        contentAlignment = Alignment.TopCenter
    ) {
        val isWide = maxWidth >= 600.dp
        val maxContentWidth = if (isWide) 680.dp else 600.dp
        val bottomClearance = if (isWide) 28.dp else 112.dp

        Column(
            modifier = Modifier
                .widthIn(max = maxContentWidth)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = bottomClearance)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width, size.height * 0.72f)
                        cubicTo(
                            size.width * 0.7f,
                            size.height * 1.02f,
                            size.width * 0.3f,
                            size.height * 0.56f,
                            0f,
                            size.height * 0.85f
                        )
                        close()
                    }
                    drawPath(path = path, color = ForestGreen)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BackCircleButton(onClick = onBack, darkSurface = true)
                    Text(
                        text = strings.settings,
                        style = MaterialTheme.typography.titleLarge,
                        color = PureWhite
                    )
                    HeaderCircleIconButton(
                        onClick = { showSettingsDialog = true },
                        contentDescription = strings.accountSettingsTitle,
                        backgroundColor = PureWhite.copy(alpha = 0.2f),
                        iconTint = PureWhite
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = strings.settings,
                            tint = PureWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Surface(
                    shape = CircleShape,
                    color = MintSoft,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 14.dp)
                        .size(96.dp)
                        .border(4.dp, PureWhite, CircleShape)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = strings.appPreferences,
                            tint = ForestGreen,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Surface(
                shape = RoundedCornerShape(28.dp),
                color = ForestGreen,
                shadowElevation = 6.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = strings.appPreferences,
                        style = MaterialTheme.typography.headlineSmall,
                        color = PureWhite
                    )
                    if (isAdminMode) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MintTeal.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = strings.adminModeBadge,
                                style = MaterialTheme.typography.labelSmall,
                                color = PureWhite,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    ProfileMenuRow(
                        title = strings.languageRowLabel,
                        icon = Icons.Filled.Language,
                        onClick = { onToggleLanguage(currentLanguage) },
                        modifier = Modifier.testTag("profile_language_row")
                    )

                    ProfileMenuRow(
                        title = strings.settings,
                        icon = Icons.Filled.Settings,
                        onClick = { showSettingsDialog = true },
                        modifier = Modifier.testTag("profile_settings_row")
                    )

                    ProfileMenuRow(
                        title = "${strings.ordersHelpPrefix} (${orders.size})",
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        onClick = { showHelpOrdersDialog = true },
                        modifier = Modifier.testTag("profile_help_row")
                    )

                    ProfileMenuRow(
                        title = strings.appLinkRow,
                        icon = Icons.Filled.Share,
                        onClick = { showAppLinkDialog = true },
                        modifier = Modifier.testTag("profile_app_link_row")
                    )

                    ProfileMenuRow(
                        title = strings.appUpdatesRow,
                        icon = Icons.Filled.SystemUpdate,
                        onClick = {
                            repoValidationError = false
                            showUpdatesDialog = true
                        },
                        modifier = Modifier.testTag("profile_updates_row")
                    )

                    ProfileMenuRow(
                        title = strings.aboutApp,
                        icon = Icons.Filled.Info,
                        onClick = { showAboutDialog = true },
                        modifier = Modifier.testTag("profile_about_row")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        onClick = {
                            if (isAdminMode) {
                                onSignedOut()
                            } else {
                                signOut(
                                    credentialManager = credentialManager,
                                    scope = scope,
                                    onSignOutComplete = onSignedOut
                                )
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = ForestGreenLight,
                        border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.18f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("sign_out_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = strings.signOut,
                                tint = PureWhite,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.signOut,
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

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            containerColor = PureWhite,
            title = {
                Text(
                    text = strings.accountSettingsTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.orderNotificationsToggle,
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = onToggleNotifications,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = MintTeal
                            )
                        )
                    }
                    Text(
                        text = "${strings.defaultDeliveryAddress} $defaultLocDisplay",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettingsDialog = false }) {
                    Text(strings.ok, color = ForestGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showHelpOrdersDialog) {
        AlertDialog(
            onDismissRequest = { showHelpOrdersDialog = false },
            containerColor = PureWhite,
            title = {
                Text(
                    text = strings.ordersHelpTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    when (ordersState) {
                        UiState.Loading -> LoadingStateCard()
                        is UiState.Error -> ErrorStateCard(
                            errorMessage = ordersState.message,
                            onRetry = onRetry
                        )
                        is UiState.Success -> {
                            if (orders.isEmpty()) {
                                Text(
                                    text = strings.noOrdersYet,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            } else {
                                orders.take(4).forEach { order ->
                                    val methodLabel = when (order.paymentMethod) {
                                        "visa" -> strings.visa
                                        "mastercard" -> strings.mastercard
                                        else -> strings.cashLabel
                                    }
                                    val locDisplay = PharmacyCatalog.localizeLocation(
                                        order.deliveryLocation,
                                        strings.isEnglish
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = NeutralBackground,
                                        border = BorderStroke(1.dp, BorderSubtle),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                                                    contentDescription = null,
                                                    tint = ForestGreen,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column {
                                                    Text(
                                                        text = "${order.itemCount} ${strings.productsWord} • $methodLabel",
                                                        style = MaterialTheme.typography.titleSmall,
                                                        color = TextPrimary
                                                    )
                                                    Text(
                                                        text = locDisplay,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = TextSecondary
                                                    )
                                                }
                                            }
                                            Text(
                                                text = strings.formatPrice(order.totalUsd),
                                                style = MaterialTheme.typography.titleSmall,
                                                color = ForestGreen,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpOrdersDialog = false }) {
                    Text(strings.close, color = ForestGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showAppLinkDialog) {
        AlertDialog(
            onDismissRequest = { showAppLinkDialog = false },
            containerColor = PureWhite,
            title = {
                Text(
                    text = strings.appLinkTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = strings.appLinkDesc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NeutralBackground,
                        border = BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = appShareUrl,
                            style = MaterialTheme.typography.bodySmall,
                            color = ForestGreen,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("App URL", appShareUrl))
                                Toast.makeText(context, strings.copiedToast, Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ForestGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = null,
                                tint = ForestGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.copy, color = ForestGreen)
                        }
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appShareUrl))
                                runCatching { context.startActivity(intent) }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.OpenInBrowser,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(strings.open, color = PureWhite)
                        }
                    }
                    OutlinedButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "${strings.appName}:\n$appShareUrl")
                            }
                            runCatching {
                                context.startActivity(Intent.createChooser(shareIntent, strings.shareChooserTitle))
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.share, color = ForestGreen)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAppLinkDialog = false }) {
                    Text(strings.close, color = ForestGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = PureWhite,
            title = {
                Text(
                    text = strings.aboutTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            },
            text = {
                Text(
                    text = strings.aboutBody,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Start
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text(strings.done, color = ForestGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showUpdatesDialog) {
        AlertDialog(
            onDismissRequest = { showUpdatesDialog = false },
            containerColor = PureWhite,
            title = {
                Text(
                    text = strings.appUpdatesDialogTitle,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
            },
            text = {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MintSoft,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.currentVersionLabel,
                                style = MaterialTheme.typography.labelLarge,
                                color = TextSecondary
                            )
                            Text(
                                text = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                                style = MaterialTheme.typography.labelLarge,
                                color = ForestGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = strings.githubRepoHelper,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = repoInput,
                        onValueChange = {
                            repoInput = it
                            repoValidationError = false
                        },
                        label = { Text(strings.githubRepoLabel) },
                        placeholder = { Text(strings.githubRepoPlaceholder) },
                        isError = repoValidationError,
                        supportingText = if (repoValidationError) {
                            { Text(strings.invalidRepoFormat) }
                        } else null,
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForestGreen,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("updates_repo_input")
                    )

                    when (updateState) {
                        AppUpdateState.Checking -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = ForestGreen,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = strings.checkingUpdates,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ForestGreen
                                )
                            }
                        }
                        is AppUpdateState.UpToDate -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = ForestGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "${strings.upToDateMessage} (${updateState.currentVersion})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ForestGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        is AppUpdateState.UpdateAvailable -> {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MintSoft,
                                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "${strings.updateFoundMessage} ${updateState.info.tagName}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = ForestGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Button(
                                        onClick = { onDownloadUpdate(updateState.info) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .minimumInteractiveComponentSize()
                                            .testTag("updates_dialog_download_btn")
                                    ) {
                                        Text(
                                            text = strings.updateNowAction,
                                            color = PureWhite,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                        is AppUpdateState.Error -> {
                            Text(
                                text = updateState.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        AppUpdateState.Idle -> Unit
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                if (UpdatePreferencesStore.isValidRepoSlug(repoInput)) {
                                    repoValidationError = false
                                    onSaveRepoAndCheck(repoInput)
                                } else {
                                    repoValidationError = true
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("updates_save_check_button")
                        ) {
                            Text(strings.saveAndCheck, color = PureWhite)
                        }
                        if (configuredRepo.isNotBlank()) {
                            OutlinedButton(
                                onClick = onCheckUpdates,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, ForestGreen),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("updates_check_now_button")
                            ) {
                                Text(strings.checkNow, color = ForestGreen)
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(
                                ClipData.newPlainText(
                                    "GitHub Release APK Workflow",
                                    GitHubReleaseChecker.GITHUB_WORKFLOW_TEMPLATE
                                )
                            )
                            Toast.makeText(context, strings.workflowCopiedToast, Toast.LENGTH_LONG).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("updates_copy_workflow_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = null,
                            tint = ForestGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(strings.copyWorkflowYaml, color = ForestGreen)
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NeutralBackground,
                        border = BorderStroke(1.dp, BorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = strings.signingNoteTitle,
                                style = MaterialTheme.typography.labelLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = strings.signingNoteBody,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showUpdatesDialog = false }) {
                    Text(strings.close, color = ForestGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun ProfileMenuRow(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = PureWhite,
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ForestGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
