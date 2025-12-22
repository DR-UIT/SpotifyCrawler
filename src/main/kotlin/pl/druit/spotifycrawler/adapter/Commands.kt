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
        key = ["shows"],
        value = "List Spotify shows, found via API by name"
    )
    fun listShowsByName(
        @ShellOption(help = "Nazwa podcastu, którą chcesz wyszukać") showName: String,
        @ShellOption(help = "Limit liczby znalezionych podcastów (domyślnie - 1)", defaultValue = "1") limit: Int,
    ) {
        spotifyService.searchShows(showName, limit)
    }
}
