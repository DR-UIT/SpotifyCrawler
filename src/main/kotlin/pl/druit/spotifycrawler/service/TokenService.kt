package pl.druit.spotifycrawler.service

import org.springframework.stereotype.Service
import pl.druit.spotifycrawler.clients.SpotifyAuthClient
import java.time.Instant
import java.time.Instant.now

@Service
class TokenService(
    private val spotifyAuthClient: SpotifyAuthClient,
) {
   private var token: SpotifyToken? = null

    fun getToken(): String {
        if (shouldRetrieveNewToken()) {
            val retrievedToken = spotifyAuthClient.retrieveToken()
            val expiresIn = now().plusSeconds(retrievedToken.expiresIn)
            token = SpotifyToken(
                accessToken = retrievedToken.accessToken,
                expiresIn = expiresIn,
            )
        }

        return token!!.accessToken
    }

    private fun shouldRetrieveNewToken() =
        token == null || token!!.expiresIn.isBefore(now())
}

data class SpotifyToken(
    val accessToken: String,
    val expiresIn: Instant,
)
