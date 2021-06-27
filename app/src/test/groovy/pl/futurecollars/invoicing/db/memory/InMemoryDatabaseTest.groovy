package pl.futurecollars.invoicing.db.memory

import pl.futurecollars.invoicing.db.Database
import pl.futurecollars.invoicing.model.Invoice
import spock.lang.Specification

import static pl.futurecollars.invoicing.TestHelpers.invoice

class InMemoryDatabaseTest extends Specification {

    private Database database
    private List<Invoice> invoices

    def setup() {
        database = new InMemoryDatabase();
        invoices = (1..10).collect{invoice(it)}
    }

    def "should save invoices returning sequential id, invoice should have id set to correct value, getById returns saved invoice"() {
        when:
        def ids = invoices.collect({database.save(it)})

        then:
        ids == (1..invoices.size()).collect()
        ids.forEach({assert database.getById(it).isPresent()})
        ids.forEach({assert database.getById(it).get().getId() == it})
        ids.forEach({assert database.getById(it).get() == invoices.get(it - 1)})
    }

    def "getByID returns empty optional when there is no invoice with given id"() {
        expect:
        !database.getById(1).isPresent()
    }

    def "getAll returns empty collection in case of no invoices"() {
        expect:
        database.getAll().isEmpty()
    }

    def "getAll returns all invoices in the database, deleted invoice is not returned"() {
        given:
        invoices.forEach({database.save(it)})

        expect:
        database.getAll().size() == invoices.size()
        database.getAll().forEach({assert it == invoices.get(it.getId() - 1)})

        when:
        database.delete(1)

        then:
        database.getAll().size() == invoices.size() - 1
        database.getAll().forEach({assert it == invoices.get(it.getId() - 1)})
        database.getAll().forEach({assert it.getId() != 1})
    }

    def "can delete all invoices"() {
        given:
        invoices.forEach({database.save(it)})

        when:
        invoices.forEach({database.delete(it.getId())})

        then:
        database.getAll().isEmpty()
    }

    def "can update the invoice" () {
        given:
        int id = database.save(invoices.get(0))

        when:
        database.update(id,invoices.get(1))

        then:
        database.getById(id).get() == invoices.get(1)
    }
}
