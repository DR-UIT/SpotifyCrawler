package pl.druit.spotifycrawler.clients

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.POST
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import pl.druit.spotifycrawler.config.SpotifyClientConfig.SpotifyConfig

@Component
class SpotifyAuthClient(
    private val restTemplate: RestTemplate,
    private val config: SpotifyConfig,
) {
    fun retrieveToken(): SpotifyAccessTokenResponse {
        val headers = buildAuthHeader()
        val requestBody = buildAuthBody(config.auth)

        val response = restTemplate.exchange(
            config.auth.url,
            POST,
            HttpEntity(requestBody, headers),
            SpotifyAccessTokenResponse::class.java
        )
        // TODO: error/null handling
        return response.body!!
    }

    private fun buildAuthHeader() = HttpHeaders().apply {
        contentType = APPLICATION_FORM_URLENCODED
    }

    private fun buildAuthBody(config: SpotifyConfig.AuthConfig) =
        "grant_type=client_credentials&client_id=${config.clientId}&client_secret=${config.clientSecret}"
}

data class SpotifyAccessTokenResponse(
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("token_type")
    val tokenType: String,
    @JsonProperty("expires_in")
    val expiresIn: Long,
)
