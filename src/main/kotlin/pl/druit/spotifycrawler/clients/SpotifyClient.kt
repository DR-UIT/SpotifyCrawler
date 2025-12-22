package pl.druit.spotifycrawler.clients

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.GET
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import pl.druit.spotifycrawler.config.SpotifyClientConfig.SpotifyConfig
import pl.druit.spotifycrawler.service.TokenService
import java.time.LocalDate

@Component
class SpotifyClient(
    private val tokenService: TokenService,
    private val restTemplate: RestTemplate,
    private val config: SpotifyConfig,
) {
    fun findShowsByName(name: String): List<ShowDto> {
        val response = restTemplate.exchange(
            buildSearchUrl(query = name),
            GET,
            HttpEntity<Void>(buildDefaultHeaders()),
            SearchResponseDto::class.java
        )
        // TODO: error/null handling
        return response.body!!.shows.items
    }

    fun findShowById(showId: String): ShowDto {
        val response = restTemplate.exchange(
            buildShowUrl(showId),
            GET,
            HttpEntity<Void>(buildDefaultHeaders()),
            ShowDto::class.java
        )
        // TODO: error/null handling
        return response.body!!
    }

    fun findEpisodesByShowId(showId: String, offset: Int, limit: Int): SpotifyResponseDto<EpisodeDto?> {
        val response = restTemplate.exchange(
            buildEpisodesUrl(showId, offset, limit),
            GET,
            HttpEntity<Void>(buildDefaultHeaders()),
            object : ParameterizedTypeReference<SpotifyResponseDto<EpisodeDto?>>() {}
        )
        // TODO: error/null handling
        return response.body!!
    }

    private fun buildSearchUrl(query: String) =
        "${config.defaultUrl}/search?q=$query&type=show&market=PL&limit=5"

    private fun buildShowUrl(showId: String) =
        "${config.defaultUrl}/shows/$showId?market=PL"

    private fun buildEpisodesUrl(showId: String, offset: Int, limit: Int) =
        "${config.defaultUrl}/shows/$showId/episodes?market=PL&limit=$limit&offset=$offset"

    private fun buildDefaultHeaders() = HttpHeaders().apply {
        contentType = APPLICATION_JSON
        accept = listOf(APPLICATION_JSON)
        setBearerAuth(tokenService.getToken())
    }
}

data class SearchResponseDto(
    val shows: SpotifyResponseDto<ShowDto>,
)

data class SpotifyResponseDto<T>(
    val total: Int,
    val offset: Int,
    val limit: Int,
    val items: List<T>,
)

data class ShowDto(
    val name: String,
    val description: String,
    val id: String,
    val uri: String,
    @JsonProperty("total_episodes")
    val totalEpisodes: Int,
) {
    override fun toString(): String =
        """
            Nazwa: $name
            Opis: $description
            Id: $id
            Liczba odcinków: $totalEpisodes
        """.trimIndent()
}

data class EpisodeDto(
    val name: String,
    val description: String,
    val id: String,
    val uri: String,
    @JsonProperty("release_date")
    val releaseDate: LocalDate,
) {
    override fun toString(): String =
        """
            Nazwa: $name
            Opis: $description
            Id: $id
            Data wydania: $releaseDate
        """.trimIndent()
}
