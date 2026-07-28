package hu.mostoha.mobile.kmp.locationiq_cmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform