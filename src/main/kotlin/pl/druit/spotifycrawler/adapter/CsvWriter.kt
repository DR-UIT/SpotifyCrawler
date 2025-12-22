package pl.druit.spotifycrawler.adapter

import org.springframework.stereotype.Component
import pl.druit.spotifycrawler.clients.EpisodeDto
import pl.druit.spotifycrawler.clients.ShowDto
import java.io.File
import java.io.PrintWriter
import java.time.Instant.now
import java.time.format.DateTimeFormatter


@Component
class CsvWriter {
    fun writeToCsv(episodes: List<EpisodeDto>, show: ShowDto, customFilename: String? = null): String {
        val filename = createCsvFilename(customFilename)
        val csvOutputFile = File(filename)
        PrintWriter(csvOutputFile).use { pw ->
            pw.println(createHeader())

            episodes
                .mapIndexed { idx, episode -> (idx + 1).toString() + ";" + episode.toCsvLine(show) }
                .forEach(pw::println)
        }
        return filename
    }

    private fun createCsvFilename(customFilename: String?): String {
        val filename = customFilename ?: "episodes${now().toEpochMilli()}"
        return "output\\$filename.csv"
    }

    private fun createHeader(): String =
        "Lp.;Nazwa podcastu;Nazwa odcinka;Data wydania"

    private fun EpisodeDto.toCsvLine(showDto: ShowDto): String {
        val showName = showDto.name.replace(";", ",").trim()
        val episodeName = name.replace(";", ",").trim()
        val releaseDate = releaseDate.format(DATE_FORMATTER)
        return "$showName;$episodeName;$releaseDate"
    }

    companion object {
        private val DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }
}
