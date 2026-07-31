package hu.mostoha.mobile.kmp.locationiq_cmp

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class LocationIqRepository(
    private val httpClient: HttpClient = NetworkClient.httpClient,
) {

    companion object {
        private const val AUTOCOMPLETE_URL = "https://api.locationiq.com/v1/autocomplete"
    }

    /**
     * Query parameters: https://api-reference.locationiq.com/#autocomplete_query-parameters
     *
     * @param query the search text. Commas are optional and separate address components.
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
    suspend fun autocomplete(
        query: String,
        limit: Int = 20,
        countryCodes: String? = null,
        acceptLanguage: String = "en",
        tag: String? = null,
        viewBox: String? = null,
        bounded: Boolean = false,
        normalizeCity: Boolean = true,
        importanceSort: Boolean = true,
        dedupe: Boolean = true,
    ): List<PlaceDto> {
        return httpClient
            .get(AUTOCOMPLETE_URL) {
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
            .body()
    }
}

private fun Boolean.asFlag(): Int = if (this) 1 else 0
