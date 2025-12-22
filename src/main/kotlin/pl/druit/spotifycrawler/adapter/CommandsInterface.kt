package pl.druit.spotifycrawler.adapter

import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption
import pl.druit.spotifycrawler.service.SpotifyService

@ShellComponent
class CommandsInterface(
    private val spotifyService: SpotifyService,
) {
    @ShellMethod(
        key = ["shows"],
        value = "Wylistuj wszystkie podcasty, które Spotify znalazł na podstawie podanej nazwy"
    )
    fun listShowsByName(
        @ShellOption(help = "Nazwa podcastu, którą chcesz wyszukać") showName: String,
    ) {
        spotifyService.searchShows(showName)
    }

    @ShellMethod(
        key = ["export-episodes"],
        value = "Wyeksportuj wszystkie odcinki danego podcastu, wydane po 2019 roku"
    )
    fun exportShowEpisodes(
        @ShellOption(help = "Id podcastu, którego odcinki chcesz wyeksportować") showId: String,
        @ShellOption(help = "Czy dla testów", defaultValue = "false") isTest: Boolean,
    ) {
        spotifyService.exportEpisodes(showId, isTest)
    }

    @ShellMethod(
        key = ["export-prepared"],
        value = "Wyeksportuj wszystkie odcinku z przygotowanej listy, wydane po 2019 roku"
    )
    fun exportPrepared(
        @ShellOption(help = "Czy dla testów", defaultValue = "false") isTest: Boolean,
    ) {
        val preparedShowsList = listOf(
            "kryminatorium" to "4wEuac2C7cpuvy8HBjfvW7",
            "piate_nie_zabijaj" to "6JxVYXIYqPvucDdKJli1rN",
            "olga_herring_true_crime" to "6uBaxDWFmaPQukcf1Rv6qG",
            "sonder_nieznane_historie" to "2jJGRVnZycJ6VwseuK2sgW",
            "zbrodnie_bez_cenzury" to "4oO1oKcIew6RmHHT1dNgxg",
            "polskie_zbrodnie" to "0EYySaCH8wEvQYRKLrsHXh",
            "zagadki_kryminalne" to "2ebH6a36WoKI4I6J8HkIzu",
            "kryminalne_historie" to "4MP3dZZu5efb5qvPSXu89k",
            "z_morderstwem_im_do_twarzy" to "7y2lRgXQbgYe7tgQlhC1OJ",
            "pieklo_jest_tu" to "1gkP4jFmgLWp1RSaTpm0MZ",
        )
        preparedShowsList.forEach { (customFilename, showId) ->
            spotifyService.exportEpisodes(customFilename, showId, isTest)
        }
    }
}
