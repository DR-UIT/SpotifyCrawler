package pl.druit.spotifycrawler.service

import org.springframework.stereotype.Service
import pl.druit.spotifycrawler.clients.SpotifyClient

@Service
class SpotifyService(
    private val spotifyClient: SpotifyClient,
) {
    fun searchShows(showName: String, limit: Int) {
        val shows = spotifyClient.findShowByName(showName, limit)
        shows.filter { it.name == showName }
        // TODO: for shows matching searched one save episodes info to csv
    }
}
