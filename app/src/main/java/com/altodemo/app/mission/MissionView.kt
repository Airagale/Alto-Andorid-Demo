package com.altodemo.app.mission

import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.*
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.altodemo.R
import com.altodemo.app.ui.theme.*
import com.altodemo.app.util.LoadingIndicator
import com.altodemo.domain.model.Driver
import com.altodemo.domain.model.Trip
import com.altodemo.domain.model.Vehicle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MissionView(
    missionViewModel: MissionViewModel = hiltViewModel()
) {
    val uiState = missionViewModel.uiState
    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(listState)
    var isContentReady by remember {
        mutableStateOf(false)
    }
    val lastVisibleItem = remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
        }
    }
    val missionItems = listOf<@Composable (maxHeight: Dp) -> Unit>(
        // "Mission Items" are defined here, with and provided dynamic height by the lambda
        // This allows our TopBar item indicator and LazyColumn Items to remain dynamic based on this
        // list of items.
        // !! is safe here, as the composable will not be drawn until we've loaded the data for the UI.
        // See line 117 for details.
        { maxHeight ->
            YourTrip(
                trip = uiState.trip!!,
                availableContentHeight = maxHeight,
                appBarHeight = uiState.appBarHeight!!,
                formattedTime = uiState.formattedEta!!,
                isEditingNotes = uiState.isEditingNotes,
                processEditNotes = missionViewModel::processStartEditNotes,
                processSaveNotes = missionViewModel::processSaveDropOffNotes,
                processMutateNotes = missionViewModel::processMutateDropOffNotes
            )
        }, { maxHeight ->
            // TODO: Verify status bar height works here?
            YourDriver(
                driver = uiState.driver!!,
                availableContentHeight = maxHeight,
                statusBarHeight = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
            )
        }, { maxHeight ->
            YourVehicle(
                vehicle = uiState.vehicle!!, availableContentHeight = maxHeight, appBarHeight = uiState.appBarHeight!!
            )
        }, { maxHeight ->
            YourDropOff(
                formattedTime = uiState.formattedEta!!,
                selectedVibe = uiState.selectedVibe!!,
                dropOffName = uiState.trip!!.dropOffLocation.name,
                isVibeChangeAvailable = uiState.isVibeChangeAvailable,
                availableContentHeight = maxHeight,
                processSelectVibe = missionViewModel::processNavigateChangeVibe
            )
        })

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Scaffold(bottomBar = {
            BottomBar(
                isVibeChangeAvailable = uiState.isVibeChangeAvailable,
                destinationName = uiState.trip?.dropOffLocation?.name,
                etaStatusText = uiState.formattedEta ?: stringResource(id = R.string.eta_routing),
                processSelectVibe = missionViewModel::processNavigateChangeVibe
            )
        }) { contentPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = contentPadding.calculateBottomPadding())
            ) {
                when {
                    !uiState.isLoading() -> {
                        AnimatedVisibility(
                            visible = isContentReady, enter = fadeIn(tween(300))
                        ) {
                            LazyColumn(
                                state = listState, flingBehavior = flingBehavior
                            ) {
                                items(missionItems) { composable ->
                                    composable(maxHeight = maxHeight)
                                }
                            }
                        }
                        // Once we know our content is ready, we trigger the animation
                        isContentReady = true
                    }
                }
            }
            TopBar(
                activeIndex = lastVisibleItem.value,
                totalItems = missionItems.size,
                isDataAvailable = uiState.isDataAvailable(),
                setTopBarHeight = missionViewModel::processTrackAppBarHeight
            )
        }
        if (uiState.isLoading()) {
            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        }
    }
}

@Composable
fun TopBar(
    activeIndex: Int?,
    isDataAvailable: Boolean,
    totalItems: Int,
    setTopBarHeight: (Dp) -> Unit,
) {
    // The UI State will loading in "chunks" to prevent layout jumping.
    // Managing dynamic app bar height requires a bit of gymnastics, even in compose.
    // 1. If the active index is present, use this.
    // 2. If data is available but the UI state is loading, set index one.
    val currentDensity = LocalDensity.current
    val indicatorTopPadding = topTitlePadding + topTitlePadding / 2
    val adjustedIndex = activeIndex ?: if (isDataAvailable) 0 else null
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = appContentMargin)
            .onSizeChanged {
                // Prevent the UI from tracking app bar height until we know there are multiple composable
                // present for the UI. This will appear to the user with a cleaning loading experience
                if (adjustedIndex != null && totalItems > 0) {
                    with(currentDensity) { setTopBarHeight(it.height.toDp()) }
                }
            },
        contentAlignment = Alignment.TopEnd,
    ) {
        // The asset ic_alto is 50x14 and not large enough to use as a title.
        // The Med Linotype typography appears to be the closest match.
        // Relative content padding required for indicator and title text

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = topTitlePadding)
                .statusBarsPadding(),
            text = stringResource(id = R.string.alto).uppercase(),
            style = MaterialTheme.typography.titleSmall,
            textAlign = TextAlign.Center
        )
        adjustedIndex?.let {
            Column(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = indicatorTopPadding)
                    .background(
                        color = AltoDemoColor.Black100.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
                    )
                    .padding(vertical = 3.dp), verticalArrangement = spacedBy(6.dp), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (i in 0 until totalItems) {
                    val color = if (i == adjustedIndex) {
                        AltoDemoColor.Brown80 // Selected
                    } else {
                        AltoDemoColor.Tan10 // Unselected
                    }
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(8.dp)
                            .background(
                                color = color, shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun YourTrip(
    trip: Trip,
    appBarHeight: Dp,
    formattedTime: String,
    isEditingNotes: Boolean,
    availableContentHeight: Dp,
    processEditNotes: () -> Unit,
    processSaveNotes: () -> Unit,
    processMutateNotes: (String) -> Unit
) {
    val context = LocalContext.current
    Column(modifier = Modifier.height(availableContentHeight)) {
        Spacer(modifier = Modifier.height(height = appBarHeight))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = appContentMargin, end = appContentMargin, bottom = bottomFeatureButtonMargin
                ), verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Your Trip Header
            Text(
                text = stringResource(id = R.string.your_trip), style = MaterialTheme.typography.titleMedium
            )
            // Trip Information
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    val timeFontSize = 68.sp
                    val letterSpacing = 0.sp
                    Text(
                        modifier = Modifier.alignByBaseline(),
                        text = formattedTime.substringBefore(" "),
                        color = AltoDemoColor.Black90,
                        fontSize = timeFontSize,
                        fontFamily = pxgroteskFontFamily,
                        letterSpacing = letterSpacing,
                        fontWeight = FontWeight.Normal,
                    )
                    Text(
                        modifier = Modifier
                            .alignByBaseline()
                            .padding(start = 8.dp),
                        text = formattedTime.substringAfter(" ").uppercase(),
                        color = AltoDemoColor.Black90,
                        fontSize = timeFontSize / 2,
                        fontFamily = pxgroteskFontFamily,
                        letterSpacing = letterSpacing,
                        fontWeight = FontWeight.Normal,
                    )
                }
                Text(
                    text = stringResource(id = R.string.your_trip_estimation, trip.dropOffLocation.name),
                    color = AltoDemoColor.Black90,
                    fontSize = 12.sp,
                    letterSpacing = 0.sp,
                    fontFamily = pxgroteskFontFamily,
                    fontWeight = FontWeight.Normal,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = spacedBy(24.dp)
                ) {
                    Column(modifier = Modifier
                        .weight(1f)
                        .clickable {
                            Toast
                                .makeText(context, "Feature out of scope", Toast.LENGTH_SHORT)
                                .show()
                        }) {
                        DefaultDivider()
                        Text(
                            letterSpacing = 0.sp,
                            color = AltoDemoColor.Brown60,
                            fontSize = 12.sp,
                            fontFamily = pxgroteskFontFamily,
                            fontWeight = FontWeight.Normal,
                            text = stringResource(id = R.string.your_trip_estimated_fare)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                letterSpacing = 0.sp,
                                color = AltoDemoColor.Brown60,
                                fontSize = 14.sp,
                                fontFamily = pxgroteskFontFamily,
                                fontWeight = FontWeight.Bold,
                                text = stringResource(
                                    id = R.string.your_trip_elements_format, trip.minFare, trip.maxFare
                                )
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Image(
                                modifier = Modifier.size(12.dp), painter = painterResource(id = R.drawable.ic_info), contentDescription = null
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        DefaultDivider()
                        Text(
                            letterSpacing = 0.sp,
                            color = AltoDemoColor.Brown60,
                            fontSize = 12.sp,
                            text = stringResource(id = R.string.your_trip_passengers)
                        )
                        Text(
                            letterSpacing = 0.sp,
                            fontFamily = pxgroteskFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = AltoDemoColor.Brown60,
                            fontSize = 14.sp,
                            text = stringResource(
                                id = R.string.your_trip_elements_format, trip.minPassengers, trip.maxPassengers
                            )
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        DefaultDivider()
                        Text(
                            letterSpacing = 0.sp,
                            color = AltoDemoColor.Brown60,
                            fontSize = 12.sp,
                            text = stringResource(id = R.string.your_trip_payment)
                        )
                        Text(
                            letterSpacing = 0.sp,
                            color = AltoDemoColor.Brown60,
                            fontFamily = pxgroteskFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            text = trip.payment,
                        )
                    }
                }
            }
            // Address information and notes
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp), verticalArrangement = Arrangement.Top
            ) {
                Text(
                    letterSpacing = 0.sp,
                    color = AltoDemoColor.Brown60,
                    fontFamily = pxgroteskFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    lineHeight = 18.sp,
                    text = trip.pickupAddress.formatted()
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp), horizontalArrangement = spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        CustomDivider()
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(1f))
                }
                Text(
                    letterSpacing = 0.sp,
                    color = AltoDemoColor.Brown60,
                    fontFamily = pxgroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 18.sp,
                    text = trip.dropOffLocation.formatted(),
                )
                Row(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp), verticalAlignment = Alignment.Bottom
                ) {
                    if (isEditingNotes) {
                        DropOffNoteTextField(
                            notes = trip.notes,
                            processSaveNotes = processSaveNotes,
                            processMutateNotes = processMutateNotes
                        )
                    } else {
                        DropOffNoteText(
                            notes = trip.notes,
                            processEditNotes = processEditNotes
                        )
                    }
                }
            }
            // Cancel trip button
            DisabledFeatureButton(text = stringResource(id = R.string.your_trip_cancel))
        }
    }
}


@Composable
fun YourDriver(driver: Driver, availableContentHeight: Dp, statusBarHeight: Dp) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(availableContentHeight)
    ) {
        Image(
            modifier = Modifier
                .weight(1f)
                .height(availableContentHeight / 2 + statusBarHeight)
                .fillMaxWidth(),
            painter = painterResource(id = R.drawable.driver_photo),
            contentDescription = "Driver photo",
            contentScale = ContentScale.FillHeight,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .height(availableContentHeight / 2 - statusBarHeight)
                .padding(
                    top = appContentMargin,
                    start = appContentMargin,
                    end = appContentMargin,
                    bottom = bottomFeatureButtonMargin
                ), verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Your Driver
            Text(
                text = stringResource(id = R.string.your_driver).uppercase(),
                fontFamily = pxgroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                color = AltoDemoColor.LeatherTan50
            )
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Top
            ) {
                // Driver Name
                Text(
                    text = driver.name,
                    color = AltoDemoColor.Black90,
                    fontSize = 68.sp,
                    fontFamily = pxgroteskFontFamily,
                    letterSpacing = 0.5.sp,
                    fontWeight = FontWeight.Normal,
                )
                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        DefaultDivider()
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
                // Driver Bio
                Text(
                    text = driver.bio,
                    letterSpacing = 0.sp,
                    color = AltoDemoColor.Brown60,
                    fontFamily = pxgroteskFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 16.sp,
                )
            }
            // Contact Driver
            DisabledFeatureButton(text = stringResource(id = R.string.your_driver_contact))
        }
    }
}

@Composable
fun YourVehicle(vehicle: Vehicle, availableContentHeight: Dp, appBarHeight: Dp) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = appContentMargin)
            .height(availableContentHeight),
    ) {
        Image(
            modifier = Modifier
                .padding(top = appBarHeight)
                .fillMaxWidth(),
            painter = painterResource(id = R.drawable.vehicle_photo),
            contentDescription = "Driver Vehicle",
            contentScale = ContentScale.FillWidth,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(
                    top = appContentMargin, bottom = bottomFeatureButtonMargin
                )
        ) {
            // Your Vehicle Details
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center
            ) {
                // Your Vehicle header
                Text(
                    text = stringResource(id = R.string.your_vehicle).uppercase(),
                    fontFamily = pxgroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = AltoDemoColor.LeatherTan50
                )
                // Vehicle license
                Text(
                    text = vehicle.license,
                    color = AltoDemoColor.Black90,
                    fontSize = 68.sp,
                    fontFamily = pxgroteskFontFamily,
                    letterSpacing = 0.5.sp,
                    fontWeight = FontWeight.Normal,
                )
                // Divider
                Row(
                    modifier = Modifier.fillMaxWidth(), horizontalArrangement = spacedBy(12.dp)
                ) {
                    // Make/Model
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        DefaultDivider()
                        Text(
                            letterSpacing = 0.sp,
                            color = AltoDemoColor.Brown60,
                            fontSize = 12.sp,
                            text = stringResource(id = R.string.your_vehicle_make_model)
                        )
                        Text(
                            letterSpacing = 0.sp,
                            fontFamily = pxgroteskFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = AltoDemoColor.Brown60,
                            fontSize = 14.sp,
                            text = vehicle.make
                        )
                    }
                    // Color
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        DefaultDivider()
                        Text(
                            letterSpacing = 0.sp,
                            color = AltoDemoColor.Brown60,
                            fontSize = 12.sp,
                            text = stringResource(id = R.string.your_vehicle_color)
                        )
                        Text(
                            letterSpacing = 0.sp,
                            fontFamily = pxgroteskFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = AltoDemoColor.Brown60,
                            fontSize = 14.sp,
                            text = vehicle.color
                        )
                    }
                }
            }
            // Identify Vehicle
            DisabledFeatureButton(text = stringResource(id = R.string.your_vehicle_identified))
        }
    }
}

@Composable
fun YourDropOff(
    formattedTime: String,
    dropOffName: String?,
    selectedVibe: String,
    availableContentHeight: Dp,
    isVibeChangeAvailable: Boolean,
    processSelectVibe: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(availableContentHeight),
    ) {
        //  Map + over layer content (Gradients, map icon button)
        Box(
            modifier = Modifier
                .height(availableContentHeight / 2)
                .fillMaxWidth(), contentAlignment = Alignment.BottomEnd
        ) {
            // Map
            Image(
                modifier = Modifier
                    .height(availableContentHeight / 2)
                    .fillMaxWidth(),
                painter = painterResource(id = R.drawable.map_overview),
                contentDescription = null,
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(availableContentHeight / 2), verticalArrangement = Arrangement.SpaceBetween
            ) {
                //Gradient
                Box(
                    modifier = Modifier
                        .height(52.dp)
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    AltoDemoColor.Tan10,
                                    Color.Transparent,
                                )
                            )
                        )
                )
                //Gradient
                Box(
                    modifier = Modifier
                        .height(52.dp)
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    AltoDemoColor.Tan10,
                                )
                            )
                        )
                )
            }

            // Map icon button
            Row {
                Image(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            Toast
                                .makeText(context, "Feature out of scope", Toast.LENGTH_SHORT)
                                .show()
                        },
                    painter = painterResource(id = R.drawable.ic_map),
                    contentDescription = "Identify location"
                )
                Spacer(modifier = Modifier.width(appContentMargin))
            }
        }
        // Drop off content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(
                    top = appContentMargin,
                    start = appContentMargin,
                    end = appContentMargin,
                    bottom = bottomFeatureButtonMargin
                ), verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Your Driver
            Text(
                text = stringResource(id = R.string.your_trip).uppercase(),
                fontFamily = pxgroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                color = AltoDemoColor.LeatherTan50
            )
            Column(
                modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Top
            ) {
                Row {
                    val timeFontSize = 68.sp
                    val letterSpacing = 0.5.sp
                    Text(
                        modifier = Modifier.alignByBaseline(),
                        text = formattedTime.substringBefore(" "),
                        color = AltoDemoColor.Black90,
                        fontSize = timeFontSize,
                        fontFamily = pxgroteskFontFamily,
                        letterSpacing = letterSpacing,
                        fontWeight = FontWeight.Normal,
                    )
                    Text(
                        modifier = Modifier
                            .alignByBaseline()
                            .padding(start = 8.dp),
                        text = formattedTime.substringAfter(" ").uppercase(),
                        color = AltoDemoColor.Black90,
                        fontSize = timeFontSize / 2,
                        fontFamily = pxgroteskFontFamily,
                        letterSpacing = letterSpacing,
                        fontWeight = FontWeight.Normal,
                    )
                }
                dropOffName?.let {
                    Text(
                        text = stringResource(id = R.string.your_trip_estimation, dropOffName),
                        color = AltoDemoColor.Black90,
                        fontSize = 12.sp,
                        letterSpacing = 0.sp,
                        fontFamily = pxgroteskFontFamily,
                        fontWeight = FontWeight.Normal,
                    )
                }
                DefaultDivider()
                // Vibe
                Text(
                    letterSpacing = 0.sp,
                    color = AltoDemoColor.Brown60,
                    fontSize = 12.sp,
                    text = stringResource(id = R.string.your_drop_off_selected_vibe)
                )
                Text(
                    letterSpacing = 0.sp,
                    fontFamily = pxgroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = AltoDemoColor.Brown60,
                    fontSize = 14.sp,
                    text = selectedVibe
                )

            }
            // Change Vibe
            // To follow comps, we require an out lined button for if the state is disabled
            if (isVibeChangeAvailable) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RectangleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AltoDemoColor.LeatherTan50,
                        contentColor = AltoDemoColor.Tan10,
                        disabledContainerColor = AltoDemoColor.Tan10,
                        disabledContentColor = AltoDemoColor.Tan30,
                    ),
                    onClick = processSelectVibe,
                ) {
                    Text(
                        text = stringResource(id = R.string.your_drop_off_change_vibe).uppercase(),
                        fontFamily = pxgroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        letterSpacing = 1.2.sp,
                    )
                }
            } else {
                DisabledFeatureButton(text = stringResource(id = R.string.your_drop_off_change_vibe))
            }
        }
    }
}

@Preview
@Composable
fun BottomBar(
    destinationName: String? = "testName",
    etaStatusText: String? = "testStatus",
    isVibeChangeAvailable: Boolean = true,
    processSelectVibe: () -> Unit = { },
) {
    val context = LocalContext.current
    Column {
        CustomDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = {
                Toast.makeText(
                    context, "Feature out of scope", Toast.LENGTH_SHORT
                ).show()
            }) {
                Image(
                    modifier = Modifier.size(24.dp),
                    contentDescription = "View Profile",
                    painter = painterResource(id = R.drawable.ic_profile),
                )
            }
            Column(
                modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally
            ) {
                destinationName?.let { name ->
                    Text(
                        text = name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        letterSpacing = 0.sp,
                        color = AltoDemoColor.Brown60,
                        fontFamily = pxgroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 17.sp
                    )
                }
                Text(
                    text = etaStatusText ?: stringResource(id = R.string.eta_uncalculated),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = 0.sp,
                    color = AltoDemoColor.Brown60,
                    fontFamily = pxgroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    lineHeight = 17.sp
                )
            }
            IconButton(
                enabled = isVibeChangeAvailable,
                onClick = processSelectVibe
            ) {
                Image(
                    modifier = Modifier.size(24.dp),
                    contentDescription = "Change Vehicle Vibe",
                    painter = painterResource(id = R.drawable.ic_vibes),
                    colorFilter =
                    if (isVibeChangeAvailable) {
                        null
                    } else {
                        ColorFilter.tint(color = AltoDemoColor.Tan30)
                    }
                )
            }
        }
    }
}

@Composable
fun DefaultDivider() {
    CustomDivider(modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
}

@Composable
fun CustomDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(1.dp)
            .fillMaxWidth()
            .background(color = AltoDemoColor.Divider)
    )
}

@Composable
fun DisabledFeatureButton(modifier: Modifier = Modifier, text: String) {
    OutlinedButton(
        modifier = modifier.fillMaxWidth(),
        enabled = false,
        shape = RectangleShape,
        border = BorderStroke(
            width = 1.dp, color = AltoDemoColor.Tan30
        ),
        onClick = { },
    ) {
        Text(
            text = text.uppercase(),
            fontFamily = pxgroteskFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            letterSpacing = 1.2.sp,
            color = AltoDemoColor.Tan30
        )
    }
}

@Composable
fun DropOffNoteText(
    notes: String,
    processEditNotes: () -> Unit
) {
    // Edit icon added as inline content - solution pulled from https://stackoverflow.com/questions/67605986/add-icon-at-last-word-of-text-in-jetpack-compose
    val icEdit = "icEdit"
    val isNoteEmpty = notes.isBlank()
    val displayText = notes.ifBlank {
        stringResource(id = R.string.your_trip_add_notes)
    }
    val text = buildAnnotatedString {
        append(displayText)
        appendInlineContent(
            id = icEdit,
            alternateText = stringResource(id = R.string.cd_edit_notes)
        )
    }
    val inlineContent = mapOf(
        Pair(icEdit, InlineTextContent(
            placeholder = Placeholder(
                width = 16.sp,
                height = 16.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Bottom
            )
        ) {
            Icon(
                modifier = Modifier.padding(
                    start = 4.dp, bottom = 4.dp
                ),
                painter = painterResource(id = R.drawable.baseline_edit_24),
                tint = AltoDemoColor.Grey60, contentDescription = null
            )
        })
    )
    Text(
        modifier = Modifier
            .clickable { processEditNotes() },
        text = text,
        letterSpacing = 0.sp,
        color = AltoDemoColor.Brown60,
        fontFamily = pxgroteskFontFamily,
        fontWeight = if (isNoteEmpty) {
            FontWeight.Light
        } else {
            FontWeight.Normal
        },
        fontStyle = if (isNoteEmpty) {
            FontStyle.Italic
        } else {
            null
        },
        fontSize = 16.sp,
        lineHeight = 18.sp,
        inlineContent = inlineContent
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DropOffNoteTextField(
    notes: String,
    processSaveNotes: () -> Unit,
    processMutateNotes: (String) -> Unit
) {
    val view = LocalView.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var flagSaveNote by remember { mutableStateOf(false) }
    var editNoteState by remember { mutableStateOf(TextFieldValue(notes)) }
    val customTextSelectionColors = TextSelectionColors(
        handleColor = AltoDemoColor.Brown80,
        backgroundColor = AltoDemoColor.Tan30.copy(alpha = 0.4f)
    )
    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        BasicTextField(
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        editNoteState = editNoteState.copy(
                            selection = TextRange(editNoteState.text.length)
                        )
                    }
                },
            value = editNoteState,
            textStyle = TextStyle(
                letterSpacing = 0.sp,
                color = AltoDemoColor.Brown60,
                fontFamily = pxgroteskFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 18.sp,
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    flagSaveNote = true
                    keyboardController?.hide()
                },
            ),
            onValueChange = { incomingNote ->
                // Arbitrary "70" character count
                if (incomingNote.text.length <= 70) {
                    editNoteState = incomingNote
                }
            },
        )
    }
    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val isKeyboardOpen = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
            if (!isKeyboardOpen) {
                if (flagSaveNote) {
                    processMutateNotes(editNoteState.text)
                }
                processSaveNotes()
                focusManager.clearFocus()
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }
}