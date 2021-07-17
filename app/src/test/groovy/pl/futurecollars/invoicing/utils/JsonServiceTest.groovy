package pl.futurecollars.invoicing.utils

import pl.futurecollars.invoicing.helpers.TestHelpers
import spock.lang.Specification

class JsonServiceTest extends Specification {

    def "can convert object to json and read it back"() {
        given:
        int id = 12
        def jsonService = new JsonService()
        def invoice = TestHelpers.invoice(id)
        invoice.setId(id)

        when:
        def invoiceAsString = jsonService.objectToString(invoice)

        and:
        def invoiceFromJson = jsonService.stringToObject(invoiceAsString)

        then:
        invoice == invoiceFromJson
    }

    def "serializing string to object throws runtime error when given string as bytes"() {
        given:
        int id = 12
        def jsonService = new JsonService()
        def invoice = TestHelpers.invoice(id)
        invoice.setId(id)

        when:
        def invoiceAsString = jsonService.objectToString(invoice)

        and:
        jsonService.stringToObject(invoiceAsString.getBytes())

        then:
        thrown(RuntimeException)
    }
}
