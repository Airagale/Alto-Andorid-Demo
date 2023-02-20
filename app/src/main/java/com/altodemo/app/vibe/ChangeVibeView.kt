package com.altodemo.app.vibe

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.altodemo.R
import com.altodemo.app.ui.theme.*
import com.altodemo.app.util.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeVibeView(
    changeVibeViewModel: ChangeVibeViewModel = hiltViewModel()
) {
    val uiState = changeVibeViewModel.uiState
    Scaffold(topBar = {
        Box(
            modifier = Modifier
                .padding(
                    start = appContentMargin,
                    top = 12.dp
                )
                .statusBarsPadding(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        // button space + extra room
                        end = 48.dp + 4.dp
                    ),
                text = stringResource(id = R.string.change_vibe_title).uppercase(),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Start
            )
            IconButton(
                modifier = Modifier.padding(end = 16.dp),
                onClick = changeVibeViewModel::processExit,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_close_24),
                    contentDescription = stringResource(id = R.string.cd_navigate_back),
                    colorFilter = ColorFilter.tint(color = AltoDemoColor.Brown80)
                )
            }
        }
    }) { padding ->
        Box(
            contentAlignment = Alignment.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding()),
            ) {
                items(
                    items = uiState.availableVibes
                ) { vibe ->
                    Vibe(
                        vibe = vibe, isSelected = vibe.equals(
                            uiState.selectedVibe, ignoreCase = true
                        )
                    ) { incomingVibe ->
                        changeVibeViewModel.processMutateVibe(incomingVibe)
                    }
                }
            }
            if (uiState.isLoading) {
                LoadingIndicator()
            }
        }
    }
}

@Composable
fun Vibe(
    vibe: String, isSelected: Boolean, onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .defaultMinSize(minHeight = 48.dp)
            .clickable { onSelect(vibe) }, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(
                    vertical = 8.dp, horizontal = appContentMargin
                ),
            text = vibe,
            fontSize = 16.sp,
            color = if (isSelected) {
                AltoDemoColor.Brown80
            } else {
                AltoDemoColor.Brown60
            },
            fontWeight = if (isSelected) {
                FontWeight.Bold
            } else {
                FontWeight.Light
            },
            fontFamily = pxgroteskFontFamily,
            letterSpacing = if (isSelected) {
                0.1.sp
            } else {
                0.sp
            },
            overflow = TextOverflow.Ellipsis,
        )

        if (isSelected) {
            Image(
                modifier = Modifier.padding(end = appContentMargin),
                painter = painterResource(id = R.drawable.baseline_check_circle_outline_24),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = AltoDemoColor.Brown80)
            )
        }
    }

}

@Preview
@Composable
fun PreviewVibeItem() {
    AltoDemoTheme {
        Column {
            Vibe(vibe = "Chill Hop Beats", isSelected = false) { }
            Vibe(vibe = "Uptight Hop Beats", isSelected = true) { }
        }
    }
}
