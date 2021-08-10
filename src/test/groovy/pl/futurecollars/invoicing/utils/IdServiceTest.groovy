package pl.futurecollars.invoicing.utils

import pl.futurecollars.invoicing.db.file.IdService
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class IdServiceTest extends Specification {

    private final Path nextIdDbPath = File.createTempFile('nextId', '.json').toPath()

    def "next id starts from 1 if file was empty"() {
        given:
        IdService idService = new IdService(nextIdDbPath, new FilesService())

        expect:
        Files.readAllLines(nextIdDbPath) == ['1']

        and:
        idService.getNextIdAndIncrement() == 1
        Files.readAllLines(nextIdDbPath) == ['2']

        and:
        idService.getNextIdAndIncrement() == 2
        Files.readAllLines(nextIdDbPath) == ['3']

        cleanup:
        Files.delete(nextIdDbPath)
    }

    def "next id is incremented consecutively from last used id"() {
        given:
        Files.writeString(nextIdDbPath, "17", StandardOpenOption.TRUNCATE_EXISTING)
        IdService idService = new IdService(nextIdDbPath, new FilesService())

        expect:
        Files.readAllLines(nextIdDbPath) == ['17']

        and:
        idService.getNextIdAndIncrement() == 17
        Files.readAllLines(nextIdDbPath) == ['18']

        and:
        idService.getNextIdAndIncrement() == 18
        Files.readAllLines(nextIdDbPath) == ['19']

        and:
        idService.getNextIdAndIncrement() == 19
        Files.readAllLines(nextIdDbPath) == ['20']

        cleanup:
        Files.delete(nextIdDbPath)
    }

    def "Error thrown when get last used ID and tracker is missing"() {
        given: 'Delete the id tracker'
        IdService idService = new IdService(nextIdDbPath, new FilesService())
        Files.delete(nextIdDbPath)

        when: 'Try to increment to next id'
        idService.getNextIdAndIncrement()

        then: 'Returns Runtime Exception'
        thrown(RuntimeException)

    }

    def "Error thrown when try to print ID to tracker and tracker is missing"() {
        given: 'Delete the id tracker'
        IdService idService = new IdService(nextIdDbPath, new FilesService())
        Files.delete(nextIdDbPath)

        when: 'Try to print id to file'
        idService.printIDtoTracker()

        then: 'Returns Runtime Exception'
        thrown(RuntimeException)
    }

    def "Error thrown when try to instantiate class"() {
        given: 'Delete the id tracker'
        Files.delete(nextIdDbPath)

        when: 'Instantiate class'
        IdService idService = new IdService(nextIdDbPath, new FilesService())

        then: 'Returns Runtime Exception'
        thrown(RuntimeException)
    }
}

