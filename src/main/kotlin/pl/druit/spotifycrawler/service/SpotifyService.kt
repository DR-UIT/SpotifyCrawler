package pl.druit.spotifycrawler.service

import org.springframework.stereotype.Service
import pl.druit.spotifycrawler.adapter.CsvWriter
import pl.druit.spotifycrawler.clients.EpisodeDto
import pl.druit.spotifycrawler.clients.ShowDto
import pl.druit.spotifycrawler.clients.SpotifyClient

@Service
class SpotifyService(
    private val spotifyClient: SpotifyClient,
    private val csvWriter: CsvWriter,
) {
    fun searchShows(showName: String) {
        val foundShows = spotifyClient.findShowsByName(showName)
        val loggingString = foundShows.mapIndexed { idx, item -> (idx + 1).toString() + "\n" + item.toString() }
            .joinToString(separator = "\n===\n")
        println(loggingString)
    }

    fun exportAllEpisodes(showId: String, isTest: Boolean) {
        exportAllEpisodes(showId = showId, isTest = isTest)
    }

    fun exportAllAndRandom(
        customShowName: String,
        showId: String,
        isTest: Boolean,
        drawnNumber: Int,
        thresholdYear: Int,
    ) {
        val (show, allEpisodes) = collectEpisodes(showId, isTest, thresholdYear)
        exportEpisodes(customShowName, allEpisodes, show, thresholdYear)

        val drawnEpisodes = drawXEpisodes(allEpisodes, drawnNumber)
        exportEpisodes(customShowName, drawnEpisodes, show, thresholdYear)
    }

    fun exportAllEpisodes(
        customShowName: String? = null,
        showId: String,
        isTest: Boolean,
        thresholdYear: Int = 2020,
    ) {
        val (show, allEpisodes) = collectEpisodes(showId, isTest, thresholdYear)
        exportEpisodes(customShowName, allEpisodes, show, thresholdYear)
    }

    fun exportRandomEpisodes(
        customShowName: String,
        showId: String,
        isTest: Boolean,
        drawnNumber: Int,
        thresholdYear: Int,
    ) {
        val (show, allEpisodes) = collectEpisodes(showId, isTest, thresholdYear)
        val drawnEpisodes = drawXEpisodes(allEpisodes, drawnNumber)
        exportEpisodes(customShowName, drawnEpisodes, show, thresholdYear)
    }

    private fun collectEpisodes(
        showId: String,
        isTest: Boolean,
        thresholdYear: Int,
    ): Pair<ShowDto, List<EpisodeDto>> {
        val show = spotifyClient.findShowById(showId)
        val allEpisodes = if (isTest) {
            collectOnly10Episodes(show.id)
        } else {
            collectAllEpisodes(show.id, show.totalEpisodes).filter { it.releaseDate.year >= thresholdYear }
        }

        return show to allEpisodes
    }

    private fun collectAllEpisodes(showId: String, totalEpisodes: Int): List<EpisodeDto> {
        val episodes = mutableListOf<EpisodeDto>()
        val limit = 50

        for (offset in customInitialOffset(showId)..totalEpisodes step limit) {
            val response = spotifyClient.findEpisodesByShowId(showId, offset, limit)
            episodes.addAll(response.items.filterNotNull())
        }
        return episodes
    }

    private fun collectOnly10Episodes(showId: String) =
        spotifyClient.findEpisodesByShowId(showId, customInitialOffset(showId), 10).items.filterNotNull()

    private fun customInitialOffset(showId: String) = NULLABLE_ITEMS_SHOWS[showId] ?: 0

    private fun drawXEpisodes(
        allEpisodes: List<EpisodeDto>,
        drawnNumber: Int,
    ) = allEpisodes.shuffled().take(drawnNumber)

    private fun exportEpisodes(
        customShowName: String?,
        episodes: List<EpisodeDto>,
        show: ShowDto,
        thresholdYear: Int,
    ) {
        val filename = createAllEpisodesFilename(customShowName, episodes.size, show.totalEpisodes, thresholdYear)
        val savedFilename = csvWriter.writeToCsv(episodes, show, filename)
        println("Odcinki podcastu ${show.name} zapisano w pliku $savedFilename - ${episodes.size}/${show.totalEpisodes}")
    }

    private fun createAllEpisodesFilename(
        customShowName: String?,
        exportedEpisodes: Int,
        totalEpisodes: Int,
        thresholdYear: Int,
    ) = listOf(customShowName ?: "episodes", exportedEpisodes, totalEpisodes, thresholdYear).joinToString("_")

    companion object {
        // some of the shows have null as first item; it's easier to start from another element than fixing it dynamically
        private val NULLABLE_ITEMS_SHOWS = mapOf(
            "4wEuac2C7cpuvy8HBjfvW7" to 1, // Kryminatorium
            "0EYySaCH8wEvQYRKLrsHXh" to 1, // Polskie zbrodnie
            "2ebH6a36WoKI4I6J8HkIzu" to 5, // Zagadki kryminalne
            "1gkP4jFmgLWp1RSaTpm0MZ" to 1, // Piekło jest tu
        )
    }
}
