package pl.druit.spotifycrawler.clients

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.shell.component.view.event.KeyEvent.Key.t
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import pl.druit.spotifycrawler.config.SpotifyClientConfig.SpotifyConfig
import pl.druit.spotifycrawler.service.TokenService

@Component
class SpotifyClient(
    private val tokenService: TokenService,
    private val restTemplate: RestTemplate,
    private val config: SpotifyConfig,
) {
    fun findShowByName(name: String, limit: Int): List<ShowDto> {
        val response = restTemplate.exchange(
            buildSearchUrl(query = name, limit = limit),
            GET,
            HttpEntity<Void>(buildDefaultHeaders()),
            SearchResponseDto::class.java // TODO: response class
        )
        // TODO: error/null handling

        val shows = response.body!!.shows
        println(shows.stringRepresentation())
        return shows.items
    }

    private fun buildSearchUrl(query: String, limit: Int): String =
        "${config.defaultUrl}/search?q=$query&type=show&market=PL&limit=$limit"

    private fun buildDefaultHeaders() = HttpHeaders().apply {
        contentType = APPLICATION_JSON
        accept = listOf(APPLICATION_JSON)
        setBearerAuth(tokenService.getToken())
    }
}

data class SearchResponseDto(
    val shows: ShowResponseDto,
)

data class ShowResponseDto(
    val total: Int,
    val offset: Int,
    val limit: Int,
    val items: List<ShowDto>,
) {
    fun stringRepresentation(): String {
        val header = "Odnaleziono następujące podcasty ($limit/$total):"
        val shows = items
            .mapIndexed { idx, item -> (idx + 1).toString() + "\n" + item.stringRepresentation() }
            .joinToString(separator = "\n===\n")
        return header + shows
    }
}

data class ShowDto(
    val name: String,
    val description: String,
    val id: String,
    val uri: String,
    @JsonProperty("total_episodes")
    val totalEpisodes: Int,
) {
    fun stringRepresentation(): String =
        """
            Nazwa: $name
            Opis: $description
            Id: $id
            Liczba odcinków: $totalEpisodes
        """.trimIndent()
}
