package pl.druit.spotifycrawler.adapter

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import pl.druit.spotifycrawler.service.SpotifyService

@ShellComponent
class Commands(
    private val spotifyService: SpotifyService,
) {
    @ShellMethod(
        key = ["list-shows-by-name"],
        value = "List Spotify shows, found via API by name"
    )
    fun listShowsByName(
        @ShellOption(help = "Nazwa podcastu, którą chcesz wyszukać") showName: String,
    ) {
        spotifyService.searchShows(showName)
    }
}
