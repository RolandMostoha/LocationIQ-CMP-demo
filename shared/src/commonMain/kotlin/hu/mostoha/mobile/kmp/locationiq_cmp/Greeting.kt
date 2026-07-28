package hu.mostoha.mobile.kmp.locationiq_cmp

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return sayHello(platform.name)
    }
}