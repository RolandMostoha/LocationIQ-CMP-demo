package hu.mostoha.mobile.kmp.locationiq_cmp

/**
 * A place as the app knows it, independent of the provider that returned it. Small enough that the
 * UI renders it directly; a bigger app would map this once more into a per screen UI model.
 */
data class Place(
    val id: String,
    val name: String,
    val address: String,
    // The OSM tag as a human label: `amenity` + `fast_food` becomes "Fast Food".
    val typeLabel: String,
    val latitude: Double,
    val longitude: Double,
)

fun List<PlaceDto>.toPlaces(): List<Place> = map { it.toPlace() }.distinctBy { it.id }

private fun PlaceDto.toPlace(): Place {
    val id = "$osmType/$osmId"
    return Place(
        id = id,
        name = displayPlace.ifBlank { displayName },
        address = displayAddress.ifBlank { displayName },
        typeLabel = osmClassValue.toTypeLabel(),
        latitude = lat,
        longitude = lon,
    )
}

/**
 * `residential` -> "Residential", `fast_food` -> "Fast Food".
 */
private fun String.toTypeLabel(): String = split('_')
    .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
