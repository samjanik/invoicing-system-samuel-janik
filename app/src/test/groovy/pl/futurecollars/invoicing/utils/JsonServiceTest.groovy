package pl.futurecollars.invoicing.utils

import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.utils.JsonService
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
}
