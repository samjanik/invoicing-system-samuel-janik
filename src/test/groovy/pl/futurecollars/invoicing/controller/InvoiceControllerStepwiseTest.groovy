package pl.futurecollars.invoicing.controller

import com.mongodb.client.MongoDatabase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Requires
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.time.LocalDate
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoicing.helpers.TestHelpers.resetIds

@AutoConfigureMockMvc
@SpringBootTest
@Stepwise
class InvoiceControllerStepwiseTest extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JsonService jsonService

    private final Invoice originalInvoice = TestHelpers.invoice(1)

    private final LocalDate updatedDate = LocalDate.of(2021, 07, 29)

    private static final String ENDPOINT = "/invoices"

    @Shared
    private int invoiceId

    @Autowired
    private ApplicationContext context

    @Requires({ System.getProperty('spring.profiles.active', 'memory').contains("mongo") })
    def "mongo database is dropped to ensure clean state"() {
        expect:
        MongoDatabase mongoDatabase = context.getBean(MongoDatabase)
        mongoDatabase.drop()
    }

    def "empty array is returned when no invoices were added"() {

        when:
        def response = mockMvc.perform(get(ENDPOINT))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        then:
        response == "[]"

    }

    def "add single invoice"() {

        given:
        def invoiceAsJson = jsonService.objectToString(originalInvoice)

        when:
        invoiceId = Integer.valueOf(mockMvc.perform(post(ENDPOINT)
                .content(invoiceAsJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString)

        then:
        invoiceId >= 1
    }

    def "one invoice is returned when getting all invoices"() {

        def expectedInvoice = originalInvoice
        expectedInvoice.id = invoiceId

        when:
        def response = mockMvc.perform(get(ENDPOINT)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .getContentAsString()

        def invoices = jsonService.stringToObject(response, Invoice[])

        then:
        invoices.size() == 1
        resetIds(invoices[0]) == resetIds(expectedInvoice)
    }

    def "invoice is returned correctly when getting by id"() {

        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = invoiceId

        when:
        def response = mockMvc.perform(get("$ENDPOINT/$expectedInvoice.id"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoice = jsonService.stringToObject(response, Invoice)

        then:
        resetIds(invoice) == resetIds(expectedInvoice)
    }

    def "invoice date can be modified"() {

        given:
        def modifiedInvoice = originalInvoice
        modifiedInvoice.id = invoiceId
        modifiedInvoice.date = updatedDate

        def invoiceAsJson = jsonService.objectToString(modifiedInvoice)

        expect:
        mockMvc.perform(put("$ENDPOINT/$modifiedInvoice.id")
                .content(invoiceAsJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
    }

    def "updated invoice is returned correctly when getting by id"() {

        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = invoiceId
        expectedInvoice.date = updatedDate

        when:
        def response = mockMvc.perform(get("$ENDPOINT/$expectedInvoice.id"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoice = jsonService.stringToObject(response, Invoice)

        then:
        resetIds(invoice) == resetIds(expectedInvoice)
    }

    def "invoice can be deleted"() {
        expect:
        mockMvc.perform(delete("$ENDPOINT/$invoiceId"))
                .andExpect(status().isOk())

        and:
        mockMvc.perform(delete("$ENDPOINT/$invoiceId"))
                .andExpect(status().isNotFound())

        and:
        mockMvc.perform(get("$ENDPOINT/$invoiceId"))
                .andExpect(status().isNotFound())
    }

}
