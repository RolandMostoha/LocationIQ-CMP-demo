package hu.mostoha.mobile.kmp.locationiq_cmp

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess

/**
 * LocationIQ backed [GeocodingRepository].
 *
 * Query parameters: https://api-reference.locationiq.com/#autocomplete_query-parameters
 *
 * @param limit number of returned results, `1` to `20`. The API defaults to 10.
 * @param countryCodes limit the search to a list of countries, e.g. `"hu,at,sk"`.
 * @param acceptLanguage preferred result language. Autocomplete takes a single two letter code:
 *   `en`, `cs`, `nl`, `fr`, `de`, `id`, `it`, `no`, `pl`, `es` or `sv`.
 * @param tag restrict to OSM `class:type` pairs, e.g. `"place:city"` or `"amenity:cafe"`.
 * @param viewBox the preferred area, as `min_lon,min_lat,max_lon,max_lat`. On autocomplete it
 *   only weights results towards the box, it does not restrict them to it.
 * @param bounded restrict results to the bounds given in [viewBox].
 * @param normalizeCity fill an empty `address.city` from the next available element, such as
 *   town, village or hamlet. The API defaults to off; on here so rows aren't missing a city.
 * @param importanceSort sort by importance. With `false` and a [viewBox], results are sorted
 *   by distance only.
 * @param dedupe return one match where several OSM objects describe the same real place. The
 *   API defaults to off. [limit] is applied before deduplication, so you may get fewer rows.
 */
class LocationIqGeocodingRepository(
    private val httpClient: HttpClient = NetworkClient.httpClient,
    private val limit: Int = 20,
    private val countryCodes: String? = null,
    private val acceptLanguage: String = "en",
    private val tag: String? = null,
    private val viewBox: String? = null,
    private val bounded: Boolean = false,
    private val normalizeCity: Boolean = true,
    private val importanceSort: Boolean = true,
    private val dedupe: Boolean = true,
) : GeocodingRepository {

    companion object {
        private const val AUTOCOMPLETE_URL = "https://api.locationiq.com/v1/autocomplete"
    }

    override suspend fun autocomplete(query: String): List<Place> {
        val response = httpClient.get(AUTOCOMPLETE_URL) {
            parameter("key", LOCATIONIQ_API_KEY)
            parameter("q", query)
            parameter("limit", limit)
            parameter("countrycodes", countryCodes)
            parameter("tag", tag)
            parameter("viewbox", viewBox)
            parameter("bounded", bounded.asFlag())
            parameter("normalizecity", normalizeCity.asFlag())
            parameter("accept-language", acceptLanguage)
            parameter("importancesort", importanceSort.asFlag())
            parameter("dedupe", dedupe.asFlag())
        }

        // https://docs.locationiq.com/reference/response-codes
        when (val status = response.status) {
            HttpStatusCode.NotFound -> return emptyList()
            HttpStatusCode.Unauthorized,
            HttpStatusCode.Forbidden,
                -> throw GeocodingException.InvalidApiKey("LocationIQ rejected the key: $status")
            HttpStatusCode.TooManyRequests ->
                throw GeocodingException.RateLimited("LocationIQ rate limit reached: $status")
            else -> if (!status.isSuccess()) {
                throw GeocodingException.Unavailable("LocationIQ returned $status")
            }
        }

        return response.body<List<PlaceDto>>().toPlaces()
    }
}

private fun Boolean.asFlag(): Int = if (this) 1 else 0
