package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import com.example.R
import com.example.data.catalog.PharmacyCatalog
import com.example.data.model.PharmacyOrder
import com.example.data.model.UserProfile
import com.example.data.update.AppUpdateInfo
import com.example.data.update.AppUpdateState
import com.example.ui.auth.signOut
import com.example.ui.components.BackCircleButton
import com.example.ui.components.HeaderCircleIconButton
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.ForestGreenLight
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.MintTeal
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
    val defaultShareUrl = stringResource(id = R.string.app_share_url)
    val appShareUrl = remember(configuredRepo, defaultShareUrl) {
        if (configuredRepo.isNotBlank()) "https://github.com/$configuredRepo/releases" else defaultShareUrl
    }

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
        ProfileSettingsDialog(
            notificationsEnabled = notificationsEnabled,
            defaultLocDisplay = defaultLocDisplay,
            onToggleNotifications = onToggleNotifications,
            onDismiss = { showSettingsDialog = false }
        )
    }

    if (showHelpOrdersDialog) {
        ProfileOrdersHelpDialog(
            ordersState = ordersState,
            orders = orders,
            onRetry = onRetry,
            onDismiss = { showHelpOrdersDialog = false }
        )
    }

    if (showAppLinkDialog) {
        ProfileAppLinkDialog(
            appShareUrl = appShareUrl,
            onDismiss = { showAppLinkDialog = false }
        )
    }

    if (showAboutDialog) {
        ProfileAboutDialog(onDismiss = { showAboutDialog = false })
    }

    if (showUpdatesDialog) {
        AppUpdatesDialog(
            configuredRepo = configuredRepo,
            repoInput = repoInput,
            repoValidationError = repoValidationError,
            updateState = updateState,
            onRepoInputChange = { repoInput = it },
            onSetRepoValidationError = { repoValidationError = it },
            onSaveRepoAndCheck = onSaveRepoAndCheck,
            onCheckUpdates = onCheckUpdates,
            onDownloadUpdate = onDownloadUpdate,
            onDismiss = { showUpdatesDialog = false }
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
