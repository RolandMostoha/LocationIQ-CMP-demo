package hu.mostoha.mobile.kmp.locationiq_cmp

interface GeocodingRepository {
    suspend fun autocomplete(query: String): List<Place>
}
