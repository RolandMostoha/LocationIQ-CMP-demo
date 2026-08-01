package hu.mostoha.mobile.kmp.locationiq_cmp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            color = LiqRed,
            trackColor = LiqCoralLight,
            modifier = Modifier.size(40.dp),
        )
        Text(
            text = "Loading...",
            style = MaterialTheme.typography.bodyMedium,
            color = ClearForeground,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}
