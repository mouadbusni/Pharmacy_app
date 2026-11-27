package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.ForestGreen
import com.example.ui.theme.LocalAppStrings
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextSecondary

@Composable
internal fun EmptyFilterStateCard(
    message: String,
    resetTestTag: String,
    onResetFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalAppStrings.current
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = PureWhite,
        border = BorderStroke(1.dp, BorderSubtle),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
            OutlinedButton(
                onClick = onResetFilters,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ForestGreen),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag(resetTestTag)
            ) {
                Text(
                    text = strings.resetFilters,
                    color = ForestGreen,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
