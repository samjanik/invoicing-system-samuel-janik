package pl.futurecollars.invoicing.utils

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class IdServiceTest extends Specification {

    private Path nextIdDbPath = File.createTempFile('nextId', '.json').toPath()

    def "next id starts from 1 if file was empty"() {
        given:
        IdService idService = new IdService(nextIdDbPath, new FilesService())

        expect:
        ['1'] == Files.readAllLines(nextIdDbPath)

        and:
        1 == idService.getNextIdAndIncrement()
        ['2'] == Files.readAllLines(nextIdDbPath)

        and:
        2 == idService.getNextIdAndIncrement()
        ['3'] == Files.readAllLines(nextIdDbPath)

        cleanup:
        Files.delete(nextIdDbPath)
    }

    def "next id is incremented consecutively from last used id"() {
        given:
        Files.writeString(nextIdDbPath, "17", StandardOpenOption.TRUNCATE_EXISTING)
        IdService idService = new IdService(nextIdDbPath, new FilesService())

        expect:
        ['17'] == Files.readAllLines(nextIdDbPath)

        and:
        17 == idService.getNextIdAndIncrement()
        ['18'] == Files.readAllLines(nextIdDbPath)

        and:
        18 == idService.getNextIdAndIncrement()
        ['19'] == Files.readAllLines(nextIdDbPath)

        and:
        19 == idService.getNextIdAndIncrement()
        ['20'] == Files.readAllLines(nextIdDbPath)

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
}

