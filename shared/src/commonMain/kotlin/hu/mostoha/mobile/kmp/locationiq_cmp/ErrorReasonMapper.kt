package hu.mostoha.mobile.kmp.locationiq_cmp

fun Throwable.toErrorReason(): ErrorReason = when (this) {
    is GeocodingException.InvalidApiKey -> ErrorReason.INVALID_API_KEY
    is GeocodingException.RateLimited -> ErrorReason.RATE_LIMITED
    else -> ErrorReason.UNAVAILABLE
}
