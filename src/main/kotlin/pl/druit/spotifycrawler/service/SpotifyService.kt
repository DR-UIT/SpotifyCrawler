package pl.druit.spotifycrawler.service

import org.springframework.stereotype.Service
import pl.druit.spotifycrawler.clients.SpotifyClient

@Service
class SpotifyService(
    private val spotifyClient: SpotifyClient,
) {
    fun searchShows(showName: String) {
        val shows = spotifyClient.findShowByName(showName)

    }
}
