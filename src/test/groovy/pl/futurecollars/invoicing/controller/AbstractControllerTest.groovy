package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.service.TaxCalculatorResults
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoicing.helpers.TestHelpers.invoice

@AutoConfigureMockMvc
@SpringBootTest
class AbstractControllerTest extends Specification {

    static final String INVOICE_ENDPOINT = "/invoices"
    static final String TAX_ENDPOINT = "/tax"

    @Autowired
    MockMvc mockMvc

    @Autowired
    JsonService jsonService

    LocalDate updatedDate = LocalDate.of(2020, 07, 29)

    def setup() {
        getAllInvoices().each { invoice -> deleteInvoice(invoice.id) }
    }

    int addInvoiceAndReturnId(Invoice invoice) {

        def invoiceId = mockMvc.perform(
                post(INVOICE_ENDPOINT)
                        .content(jsonService.objectToString(invoice))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString
        Integer.valueOf(invoiceId)
    }

    List<Invoice> addUniqueInvoices(int count) {
        (1..count).collect { id ->
            def invoice = invoice(id)
            invoice.id = addInvoiceAndReturnId(invoice)
            invoice
        }
    }

    List<Invoice> getAllInvoices() {
        def response = mockMvc.perform(get(INVOICE_ENDPOINT)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        return jsonService.stringToObject(response, Invoice[])
    }

    Invoice getInvoiceById(int id) {
        def invoiceAsString = mockMvc.perform(get("$INVOICE_ENDPOINT/$id")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        jsonService.stringToObject(invoiceAsString, Invoice)
    }

    ResultActions deleteInvoice(int id) {
        mockMvc.perform(delete("$INVOICE_ENDPOINT/$id"))
                .andExpect(status().isOk())
    }

    String invoiceAsJson(int id) {
        def testCaseInvoice = invoice(id)
        testCaseInvoice.id = id
        jsonService.objectToString(testCaseInvoice)
    }

    TaxCalculatorResults calculateTax(Company company) {

        def response = mockMvc.perform(
                post("$TAX_ENDPOINT")
                        .content(jsonService.objectToString(company))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        jsonService.stringToObject(response, TaxCalculatorResults)
    }
}