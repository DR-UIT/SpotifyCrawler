package pl.druit.spotifycrawler.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class SpotifyClientConfig {
    @Bean
    fun restTemplate() = RestTemplate()

    @ConfigurationProperties("spotify")
    data class SpotifyConfig(
        val defaultUrl: String,
        val auth: AuthConfig,
    ) {
        data class AuthConfig(
            val url: String,
            val clientId: String,
            val clientSecret: String,
        )
    }
}
