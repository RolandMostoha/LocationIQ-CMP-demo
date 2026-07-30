package hu.mostoha.mobile.kmp.locationiq_cmp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * One prediction from the `/autocomplete` endpoint, with every field the API documents.
 *
 * Response fields: https://api-reference.locationiq.com/#autocomplete_response
 */
@Serializable
data class PlaceDto(
    // Internal identifier in the LocationIQ database. It varies across their servers and changes so avoid caching it.
    @SerialName("place_id")
    val placeId: String,
    // The corresponding OSM ID. Only unique together with osmType.
    @SerialName("osm_id")
    val osmId: String,
    @SerialName("osm_type")
    val osmType: OsmType,
    val lat: Double,
    val lon: Double,
    // [min_lat, max_lat, min_lon, max_lon]. Must-have for proper zooming.
    val boundingbox: List<Double>,
    // The display name with the complete address. Everything OSM has, so too long for a list row:
    // "Empire State Building, 350, 5th Avenue, Korea Town, Midtown South, Manhattan, Manhattan
    // Community Board 5, New York County, New York City, New York, 10001, United States of America"
    @SerialName("display_name")
    val displayName: String,
    // Only the name part: if the type is `city`, just the city's name; if `highway`, just the
    // road's name. "Empire State Building". Title line.
    @SerialName("display_place")
    val displayPlace: String,
    // The complete address without the text already present in displayPlace:
    // "350, 5th Avenue, Midtown South, New York City, New York, 10001, USA". Subtitle line.
    @SerialName("display_address")
    val displayAddress: String,
    val address: AddressDto,
    // The category of this element: `place`, `highway`, `amenity`, `boundary`, `shop`...
    @SerialName("class")
    val osmClass: String,
    // The type within that class: `city`, `restaurant`, `residential`, `peak`... The two together
    // are the OSM tag, so `place` + `city` means the object is tagged `place=city`.
    @SerialName("type")
    val osmClassValue: String,
    // URL of an icon representing this element, when there is one.
    val icon: String? = null,
    // The licence and attribution requirements. OSM data is ODbL, so you are required to show it.
    val licence: String,
)

@Serializable
enum class OsmType {
    @SerialName("node")
    NODE,

    @SerialName("way")
    WAY,

    @SerialName("relation")
    RELATION,
}

@Serializable
data class AddressDto(
    val name: String? = null,
    @SerialName("house_number")
    val houseNumber: String? = null,
    val road: String? = null,
    val neighbourhood: String? = null,
    val suburb: String? = null,
    val city: String? = null,
    val county: String? = null,
    val state: String? = null,
    @SerialName("state_code")
    val stateCode: String? = null,
    val postcode: String? = null,
    val country: String? = null,
    @SerialName("country_code")
    val countryCode: String? = null,
)
