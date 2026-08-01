package hu.mostoha.mobile.kmp.locationiq_cmp

sealed class GeocodingException(message: String) : Exception(message) {
    class InvalidApiKey(message: String) : GeocodingException(message)

    class RateLimited(message: String) : GeocodingException(message)

    class Unavailable(message: String) : GeocodingException(message)
}
