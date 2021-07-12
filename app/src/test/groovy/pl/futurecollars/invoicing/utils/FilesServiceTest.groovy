package pl.futurecollars.invoicing.utils

import pl.futurecollars.invoicing.helpers.TestHelpers

import java.nio.file.Path
import java.nio.file.Files
import spock.lang.Specification
import java.nio.file.StandardOpenOption

class FilesServiceTest extends Specification {

    private Path invoicesDbpath = File.createTempFile('lines', '.json').toPath()
    private Path nextIdDbPath = File.createTempFile('nextId', '.json').toPath()
    private FilesService fileService = new FilesService(invoicesDbpath, nextIdDbPath);

    def "lines are correctly read from file"() {
        given:
        int id = 12
        def jsonService = new JsonService()
        def invoice = TestHelpers.invoice(id)
        invoice.setId(id)

        when:
        def invoiceAsString = jsonService.objectToString(invoice)
        Files.write(invoicesDbpath, List.of(invoiceAsString), StandardOpenOption.TRUNCATE_EXISTING)

        then:
        List.of(invoice) == fileService.readAllLines()

        cleanup:
        Files.delete(nextIdDbPath)
        Files.delete(invoicesDbpath)
    }

    def "line is correctly rewritten to file"() {
        given:
        int id12 = 12
        def invoice12 = TestHelpers.invoice(id12)
        invoice12.setId(id12)
        int id13 = 13
        def invoice13 = TestHelpers.invoice(id13)
        invoice13.setId(id13)
        int id14 = 14
        def invoice14 = TestHelpers.invoice(id14)
        invoice14.setId(id14)
        int id13new = 13
        def invoice13new = TestHelpers.invoice(id13new)
        invoice13new.setId(id13new)
        int id15 = 15
        def invoice15 = TestHelpers.invoice(id15)
        invoice15.setId(id15)
        def jsonService = new JsonService()

        def invoiceAsString12 = jsonService.objectToString(invoice12)
        def invoiceAsString13 = jsonService.objectToString(invoice13)
        def invoiceAsString14 = jsonService.objectToString(invoice14)

        Files.write(invoicesDbpath, List.of(invoiceAsString12), StandardOpenOption.TRUNCATE_EXISTING)
        Files.write(invoicesDbpath, List.of(invoiceAsString13), StandardOpenOption.APPEND)
        Files.write(invoicesDbpath, List.of(invoiceAsString14), StandardOpenOption.APPEND)
        Files.writeString(nextIdDbPath, "14", StandardOpenOption.TRUNCATE_EXISTING)

        expect:
        Optional.of(invoice13) == fileService.rewriteLinesToFile(13, invoice13new)

        and:
        Optional.empty() == fileService.rewriteLinesToFile(15, invoice15)

        cleanup:
        Files.delete(nextIdDbPath)
        Files.delete(invoicesDbpath)
    }

    def "line is correctly appended to file"() {
        given:
        int id = 12
        def invoice = TestHelpers.invoice(id)
        invoice.setId(id)

        when:
        Files.writeString(nextIdDbPath, "11", StandardOpenOption.TRUNCATE_EXISTING)

        then:
        invoice.getId() == fileService.appendLineToFile(invoice)

        cleanup:
        Files.delete(nextIdDbPath)
        Files.delete(invoicesDbpath)
    }

    def "next id starts from 1 if file was empty"() {

        given:
        fileService.getLastTrackedID()

        expect:
        ['1'] == Files.readAllLines(nextIdDbPath)

        and:
        2 == fileService.getNextIdAndIncrement()
        ['2'] == Files.readAllLines(nextIdDbPath)

        and:
        3 == fileService.getNextIdAndIncrement()
        ['3'] == Files.readAllLines(nextIdDbPath)

        cleanup:
        Files.delete(nextIdDbPath)
        Files.delete(invoicesDbpath)
    }

    def "next id is incremented consecutively from last used id"() {
        given:
        Files.writeString(nextIdDbPath, "17", StandardOpenOption.TRUNCATE_EXISTING)
        fileService.getNextIdAndIncrement()

        expect:
        ['18'] == Files.readAllLines(nextIdDbPath)

        and:
        19 == fileService.getNextIdAndIncrement()
        ['19'] == Files.readAllLines(nextIdDbPath)

        and:
        20 == fileService.getNextIdAndIncrement()
        ['20'] == Files.readAllLines(nextIdDbPath)

        cleanup:
        Files.delete(nextIdDbPath)
        Files.delete(invoicesDbpath)
    }

    def "empty file returns empty collection"() {
        expect:
        [] == fileService.readAllLines()

        cleanup:
        Files.delete(nextIdDbPath)
        Files.delete(invoicesDbpath)
    }

    def "line is correctly removed from file"() {

        given:
        int id = 12
        def jsonService = new JsonService()
        def invoice = TestHelpers.invoice(id)
        invoice.setId(id)

        int id2 = 13
        def invoice2 = TestHelpers.invoice(id2)
        invoice2.setId(id2)

        def invoiceAsString = jsonService.objectToString(invoice)
        def invoiceAsString2 = jsonService.objectToString(invoice2)

        Files.write(invoicesDbpath, List.of(invoiceAsString), StandardOpenOption.TRUNCATE_EXISTING)
        Files.write(invoicesDbpath, List.of(invoiceAsString2), StandardOpenOption.APPEND)

        expect:
        Optional.of(invoice2) == fileService.removeLineByID(13)

        and:
        List.of(invoice) == fileService.readAllLines()

        and:
        Optional.empty() == fileService.removeLineByID(14)

        and:
        Optional.of(invoice) == fileService.removeLineByID(12)

        and:
        List.of() == fileService.readAllLines()

        cleanup:
        Files.delete(nextIdDbPath)
        Files.delete(invoicesDbpath)
    }

    def "Error not thrown when get last used ID"() {
        when:
        fileService.getLastTrackedID()

        then:
        notThrown(IOException)
        fileService.getNextId() == 0

        cleanup:
        Files.delete(nextIdDbPath)
        Files.delete(invoicesDbpath)
    }

    def "Error thrown when try to print ID to tracker but tracker is missing"() {
        given: 'Delete the id tracker'
        Files.delete(nextIdDbPath)

        when: 'Try to print id to file'
        fileService.printIDtoTracker()

        then: 'Returns Runtime Exception'
        thrown(RuntimeException)
        cleanup:
        Files.delete(invoicesDbpath)
    }

    def "Error thrown when id tracker is missing when retrieving last used id"() {
        given: 'Delete the id tracker'
        Files.delete(nextIdDbPath)

        when: 'Try to get the last id'
        fileService.getLastTrackedID()

        then: 'Returns Runtime Exception'
        thrown(RuntimeException)
        cleanup:
        Files.delete(invoicesDbpath)
    }

    def "Error thrown when try to save to a missing database"() {
        given: 'Delete the id tracker'
        Files.delete(invoicesDbpath)

        when: 'Try to print id to file'
        fileService.appendLineToFile(9)

        then: 'Returns Runtime Exception'
        thrown(RuntimeException)
        cleanup:
        Files.delete(nextIdDbPath)
    }

    def "Runtime error thrown when try to read from a missing database"() {
        given: 'Delete the invoice database'
        Files.delete(invoicesDbpath)

        when: 'Try to read file'
        fileService.readAllLines()

        then: 'Runtime exception'
        thrown(RuntimeException)
        cleanup:
        Files.delete(nextIdDbPath)
    }
}
