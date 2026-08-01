package hu.mostoha.mobile.kmp.locationiq_cmp

sealed interface PlacesResult {
    data object Idle : PlacesResult

    data object Loading : PlacesResult

    data class Places(val places: List<Place>) : PlacesResult

    data class Empty(val query: String) : PlacesResult

    data class Error(val reason: ErrorReason) : PlacesResult
}
