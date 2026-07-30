package hu.mostoha.mobile.kmp.locationiq_cmp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Color
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

private val LiqBackground = Color(0xFFFDF8F6)
private val LiqRed = Color(0xFFF1654A)
private val LiqCoral = Color(0xFFF9A79C)
private val ClearBackground = Color(0xFFE4E4E4)
private val ClearForeground = Color(0xFF5F6B70)

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
                .safeContentPadding()
                .pointerInput(Unit) {
                    detectTapGestures { focusManager.clearFocus() }
                }
                .padding(16.dp),
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
            if (uiState.result.isNotEmpty()) {
                Text(
                    text = uiState.result,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }
    }
}
