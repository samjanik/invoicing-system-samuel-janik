package pl.futurecollars.invoicing.db.file

import pl.futurecollars.invoicing.db.AbstractDatabaseTest
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.helpers.TestHelpers

import pl.futurecollars.invoicing.utils.FilesService
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Shared

import java.nio.file.Files

class FileBasedDatabaseIntegrationTest extends AbstractDatabaseTest {

    @Shared
    def dbPath
    @Shared
    def idPath

    @Override
    Database getDatabaseInstance() {

        def filesService = new FilesService()
        def jsonService = new JsonService()

        idPath = File.createTempFile('ids', '.json').toPath()
        def idService = new IdService(idPath, filesService)

        dbPath = File.createTempFile('invoices', '.json').toPath()
        return new FileBasedDatabase(dbPath, idService, filesService, jsonService)
    }

    def "file based database writes invoices to correct file"() {
        given:
        def db = getDatabaseInstance()

        when:
        db.save(TestHelpers.invoice(4))

        then:
        1 == Files.readAllLines(dbPath).size()

        when:
        db.save(TestHelpers.invoice(5))

        then:
        2 == Files.readAllLines(dbPath).size()
    }

    def "file based database returns Runtime Exception when missing database during save operation"() {
        given:
        def db = getDatabaseInstance()

        when:
        Files.delete(dbPath)
        db.save(TestHelpers.invoice(4))

        then:
        thrown(RuntimeException)
    }

    def "file based database returns Runtime Exception when missing database during getAll operation"() {
        given:
        def db = getDatabaseInstance()

        when:
        Files.delete(dbPath)
        db.getAll()

        then:
        thrown(RuntimeException)
    }

    def "file based database returns Runtime Exception when missing database during getById operation"() {
        given:
        def db = getDatabaseInstance()

        when:
        Files.delete(dbPath)
        db.getById(4)

        then:
        thrown(RuntimeException)
    }

    def cleanupSpec() {
        Files.delete(idPath)
    }
}