package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import pl.futurecollars.invoicing.helpers.TestHelpers
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification
import spock.lang.Stepwise

import java.time.LocalDate
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@AutoConfigureMockMvc
@SpringBootTest
@Stepwise
class InvoiceControllerStepwiseTest extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private JsonService jsonService

    private Invoice originalInvoice = TestHelpers.invoice(1)

    private LocalDate updatedDate = LocalDate.of(2021, 07, 29)

    private static final String ENDPOINT = "/invoices"

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
        def invoiceId = mockMvc.perform(post(ENDPOINT)
                .content(invoiceAsJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        then:
        Integer.valueOf(invoiceId) == 1

    }

    def "one invoice is returned when getting all invoices"() {

        def expectedInvoice = originalInvoice
        expectedInvoice.id = 1

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
        invoices[0] == expectedInvoice
    }

    def "invoice is returned correctly when getting by id"() {

        given:
        def expectedInvoice = originalInvoice
        expectedInvoice.id = 1

        when:
        def response = mockMvc.perform(get("$ENDPOINT/$expectedInvoice.id"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoice = jsonService.stringToObject(response, Invoice)

        then:
        invoice == expectedInvoice
    }

    def "invoice date can be modified"() {

        given:
        def modifiedInvoice = originalInvoice
        modifiedInvoice.id = 1
        modifiedInvoice.issueDate = updatedDate

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
        expectedInvoice.id = 1
        expectedInvoice.issueDate = updatedDate

        when:
        def response = mockMvc.perform(get("$ENDPOINT/$expectedInvoice.id"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        def invoice = jsonService.stringToObject(response, Invoice)

        then:
        invoice == expectedInvoice
    }

    def "invoice can be deleted"() {
        expect:
        mockMvc.perform(delete("$ENDPOINT/1"))
                .andExpect(status().isOk())

        and:
        mockMvc.perform(delete("$ENDPOINT/1"))
                .andExpect(status().isNotFound())

        and:
        mockMvc.perform(get("$ENDPOINT/1"))
                .andExpect(status().isNotFound())
    }

}
