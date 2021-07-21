package pl.futurecollars.invoicing.utils

import pl.futurecollars.invoicing.helpers.TestHelpers
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

class FilesServiceTest extends Specification {

    private Path invoicesDbpath = File.createTempFile('lines', '.json').toPath()
    private FilesService filesService = new FilesService();

    def "lines are correctly read from file given an example invoice"() {
        given:
        int id = 12
        def jsonService = new JsonService()
        def invoice = TestHelpers.invoice(id)
        invoice.setId(id)

        when:
        def invoiceAsString = jsonService.objectToString(invoice)
        Files.write(invoicesDbpath, List.of(invoiceAsString), StandardOpenOption.TRUNCATE_EXISTING)

        then:
        List.of(invoiceAsString) == filesService.readAllLines(invoicesDbpath)

        cleanup:
        Files.delete(invoicesDbpath)
    }

    def "lines are correctly read from file given any text"() {
        setup:
        def lines = List.of("line 1", "line 2", "line 3")
        Files.write(invoicesDbpath, lines)

        expect:
        lines == filesService.readAllLines(invoicesDbpath)

        cleanup:
        Files.delete(invoicesDbpath)
    }

    def "list of lines is correctly written to file given arbitrary data"() {
        given:
        def digits = ['1', '2', '3']
        def letters = ['a', 'b', 'c']

        expect:
        [] == Files.readAllLines(invoicesDbpath)

        when:
        filesService.writeLinesToFile(invoicesDbpath, digits)

        then:
        digits == Files.readAllLines(invoicesDbpath)

        when:
        filesService.writeLinesToFile(invoicesDbpath, letters)

        then:
        letters == Files.readAllLines(invoicesDbpath)

        cleanup:
        Files.delete(invoicesDbpath)
    }

    def "empty file returns empty collection"() {
        expect:
        [] == filesService.readAllLines(invoicesDbpath)

        cleanup:
        Files.delete(invoicesDbpath)
    }


    def "Runtime error thrown when try to read from a missing database"() {
        given: 'Delete the invoice database'
        Files.delete(invoicesDbpath)

        when: 'Try to read file'
        filesService.readAllLines(invoicesDbpath)

        then: 'Runtime exception'
        thrown(IOException)
    }
}
