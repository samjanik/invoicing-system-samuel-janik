package pl.futurecollars.invoicing.db.file

import pl.futurecollars.invoicing.db.AbstractDatabaseTest
import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.helpers.TestHelpers

import pl.futurecollars.invoicing.utils.FilesService
import pl.futurecollars.invoicing.utils.IdService
import pl.futurecollars.invoicing.utils.JsonService

import java.nio.file.Files

class FileBasedDatabaseIntegrationTest extends AbstractDatabaseTest {

    def dbPath

    @Override
    Database getDatabaseInstance() {

        def filesService = new FilesService()
        def jsonService = new JsonService()

        def idPath = File.createTempFile('ids', '.json').toPath()
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
}