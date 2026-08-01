package hu.mostoha.mobile.kmp.locationiq_cmp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.milliseconds

private val DEBOUNCE_TIMEOUT = 1100.milliseconds
private const val MIN_QUERY_LENGTH = 3

@OptIn(ExperimentalCoroutinesApi::class)
class GeocodingViewModel(
    private val repository: GeocodingRepository = LocationIqGeocodingRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(GeocodingUiState())
    val uiState: StateFlow<GeocodingUiState> = _uiState.asStateFlow()

    init {
        _uiState
            .map { it.query.trim() }
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.length < MIN_QUERY_LENGTH) {
                    flowOf(PlacesResult.Idle)
                } else {
                    flow<PlacesResult> {
                        emit(PlacesResult.Loading)
                        delay(DEBOUNCE_TIMEOUT)
                        emitAll(search(query))
                    }
                }
            }
            .onEach { result -> setResult(result) }
            .launchIn(viewModelScope)
    }

    private fun search(query: String) = flow {
        val places = repository.autocomplete(query)
        emit(
            if (places.isEmpty()) {
                PlacesResult.Empty(query)
            } else {
                PlacesResult.Places(places)
            }
        )
    }.catch { throwable ->
        emit(PlacesResult.Error(throwable.toErrorReason()))
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
    }

    fun onQueryCleared() {
        _uiState.update { it.copy(query = "") }
    }

    private fun setResult(result: PlacesResult) {
        _uiState.update { it.copy(result = result) }
    }
}

data class GeocodingUiState(
    val query: String = "",
    val result: PlacesResult = PlacesResult.Idle,
)
