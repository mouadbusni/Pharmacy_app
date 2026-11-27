package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.BuildConfig
import com.example.data.update.AppUpdateInfo
import com.example.data.update.AppUpdateState
import com.example.data.update.GitHubReleaseChecker
import com.example.data.update.UpdatePreferencesStore
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.MintSoft
import com.example.ui.theme.NeutralBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
internal fun AppUpdatesDialog(
    configuredRepo: String,
    repoInput: String,
    repoValidationError: Boolean,
    updateState: AppUpdateState,
    onRepoInputChange: (String) -> Unit,
    onSetRepoValidationError: (Boolean) -> Unit,
    onSaveRepoAndCheck: (String) -> Unit,
    onCheckUpdates: () -> Unit,
    onDownloadUpdate: (AppUpdateInfo) -> Unit,
    onDismiss: () -> Unit
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
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
                        onRepoInputChange(it)
                        onSetRepoValidationError(false)
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

                UpdateStatusSection(
                    updateState = updateState,
                    onDownloadUpdate = onDownloadUpdate
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (UpdatePreferencesStore.isValidRepoSlug(repoInput)) {
                                onSetRepoValidationError(false)
                                onSaveRepoAndCheck(repoInput)
                            } else {
                                onSetRepoValidationError(true)
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
            TextButton(onClick = onDismiss) {
                Text(strings.close, color = ForestGreen, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun UpdateStatusSection(
    updateState: AppUpdateState,
    onDownloadUpdate: (AppUpdateInfo) -> Unit
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current

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
        is AppUpdateState.NoReleasesYet -> {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MintSoft,
                border = BorderStroke(1.dp, ForestGreen.copy(alpha = 0.35f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("updates_no_releases_card")
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = strings.noReleasesYetTitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = ForestGreen,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = strings.noReleasesYetBody,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(
                                ClipData.newPlainText(
                                    "GitHub Release APK Workflow",
                                    GitHubReleaseChecker.GITHUB_WORKFLOW_TEMPLATE
                                )
                            )
                            Toast.makeText(context, strings.workflowCopiedToast, Toast.LENGTH_LONG).show()
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(GitHubReleaseChecker.newWorkflowUrl(updateState.repoSlug))
                            )
                            runCatching { context.startActivity(intent) }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("updates_create_workflow_button")
                    ) {
                        Text(
                            text = strings.copyAndOpenWorkflow,
                            color = PureWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(GitHubReleaseChecker.pullRequestsUrl(updateState.repoSlug))
                            )
                            runCatching { context.startActivity(intent) }
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, ForestGreen),
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("updates_open_prs_button")
                    ) {
                        Text(
                            text = strings.openPullRequests,
                            color = ForestGreen
                        )
                    }
                }
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
}
