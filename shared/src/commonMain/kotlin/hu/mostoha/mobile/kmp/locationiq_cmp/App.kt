package hu.mostoha.mobile.kmp.locationiq_cmp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import locationiqcmp.shared.generated.resources.Res
import locationiqcmp.shared.generated.resources.ic_location_iq_logo
import org.jetbrains.compose.resources.painterResource

@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel = viewModel { GeocodingViewModel() }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val focusManager = LocalFocusManager.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LiqBackground)
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top))
                .pointerInput(Unit) {
                    detectTapGestures { focusManager.clearFocus() }
                }
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_location_iq_logo),
                contentDescription = null,
                modifier = Modifier
                    .height(56.dp)
                    .padding(bottom = 24.dp),
            )
            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChanged,
                label = { Text("Search for a place") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search,
                    capitalization = KeyboardCapitalization.Words,
                ),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = LiqRed,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(
                            onClick = viewModel::onQueryCleared,
                            modifier = Modifier.padding(end = 8.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = ClearForeground,
                                modifier = Modifier
                                    .size(26.dp)
                                    .background(ClearBackground, CircleShape)
                                    .padding(4.dp),
                            )
                        }
                    }
                },
                shape = CircleShape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LiqRed,
                    unfocusedBorderColor = LiqCoral,
                    focusedLabelColor = LiqRed,
                    unfocusedLabelColor = LiqCoral,
                    cursorColor = LiqRed,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentType = ContentType.PostalAddress },
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 12.dp),
                contentAlignment = Alignment.TopCenter,
            ) {
                val stateModifier = Modifier.padding(top = 48.dp)
                when (val result = uiState.result) {
                    PlacesResult.Idle -> Unit
                    PlacesResult.Loading -> LoadingView(stateModifier)
                    is PlacesResult.Places -> PlaceList(result.places)
                    is PlacesResult.Empty -> InfoView(
                        icon = Icons.Outlined.SearchOff,
                        title = "No places found",
                        message = "We couldn't find a match for \"${result.query}\". " +
                            "Try a different spelling or a nearby landmark.",
                        modifier = stateModifier,
                    )
                    is PlacesResult.Error -> ErrorView(result.reason, stateModifier)
                }
            }
        }
    }
}

@Composable
private fun ErrorView(reason: ErrorReason, modifier: Modifier = Modifier) {
    when (reason) {
        ErrorReason.INVALID_API_KEY -> InfoView(
            icon = Icons.Outlined.Key,
            title = "API key rejected",
            message = "LocationIQ didn't accept the API key. Check the key in " +
                "LocationIqConfig.kt and that it is allowed to call the autocomplete endpoint.",
            modifier = modifier,
        )
        ErrorReason.RATE_LIMITED -> InfoView(
            icon = Icons.Outlined.HourglassEmpty,
            title = "Too many searches",
            message = "You have hit the LocationIQ rate limit. Wait a moment, then type again.",
            modifier = modifier,
        )
        ErrorReason.UNAVAILABLE -> InfoView(
            icon = Icons.Outlined.WarningAmber,
            title = "Search unavailable",
            message = "We couldn't reach LocationIQ. Check your connection and try again.",
            modifier = modifier,
        )
    }
}

@Composable
private fun PlaceList(places: List<Place>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding()
        ),
    ) {
        itemsIndexed(
            items = places,
            key = { _, place -> place.id },
        ) { index, place ->
            PlaceRow(place)
            if (index != places.lastIndex) {
                HorizontalDivider(color = LiqDivider)
            }
        }
    }
}
