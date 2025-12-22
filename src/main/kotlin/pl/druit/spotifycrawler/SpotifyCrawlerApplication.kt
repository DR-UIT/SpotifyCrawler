package pl.druit.spotifycrawler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpotifyCrawlerApplication

fun main(args: Array<String>) {
    runApplication<SpotifyCrawlerApplication>(*args)
}
