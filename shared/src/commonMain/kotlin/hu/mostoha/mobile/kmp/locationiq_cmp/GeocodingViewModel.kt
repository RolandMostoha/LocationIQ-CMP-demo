package hu.mostoha.mobile.kmp.locationiq_cmp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.milliseconds

private val DEBOUNCE_TIMEOUT = 1100.milliseconds
private const val MIN_QUERY_LENGTH = 3

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class GeocodingViewModel(
    private val repository: LocationIqRepository = LocationIqRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(GeocodingUiState())
    val uiState: StateFlow<GeocodingUiState> = _uiState.asStateFlow()

    init {
        _uiState
            .map { it.query.trim() }
            .filter { it.length >= MIN_QUERY_LENGTH }
            .debounce(DEBOUNCE_TIMEOUT)
            .distinctUntilChanged()
            .flatMapLatest { query -> search(query) }
            .onEach { result -> _uiState.update { it.copy(result = result) } }
            .launchIn(viewModelScope)
    }

    private fun search(query: String) = flow {
        emit(NetworkClient.prettyJson.encodeToString(repository.autocomplete(query)))
    }.catch {
        emit("Search failed: ${it.message}")
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun onQueryCleared() {
        _uiState.update { it.copy(query = "", result = "") }
    }
}

data class GeocodingUiState(
    val query: String = "",
    val result: String = "",
)
