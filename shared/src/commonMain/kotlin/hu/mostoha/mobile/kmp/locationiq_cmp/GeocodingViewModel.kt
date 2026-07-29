package hu.mostoha.mobile.kmp.locationiq_cmp

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GeocodingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GeocodingUiState())
    val uiState: StateFlow<GeocodingUiState> = _uiState.asStateFlow()

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun onQueryCleared() {
        _uiState.update { it.copy(query = "") }
    }
}

data class GeocodingUiState(
    val query: String = "",
)
