package pl.futurecollars.invoicing.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.service.tax.TaxCalculatorResults
import pl.futurecollars.invoicing.utils.JsonService
import spock.lang.Specification

import java.time.LocalDate

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import static pl.futurecollars.invoicing.helpers.TestHelpers.invoice
import static pl.futurecollars.invoicing.helpers.TestHelpers.company

@AutoConfigureMockMvc
@SpringBootTest
class AbstractControllerTest extends Specification {

    static final String INVOICE_ENDPOINT = "/invoices"
    static final String COMPANY_ENDPOINT = "/companies"
    static final String TAX_ENDPOINT = "/tax"

    @Autowired
    MockMvc mockMvc

    @Autowired
    JsonService jsonService

    LocalDate updatedDate = LocalDate.of(2020, 07, 29)

    def setup() {
        getAllInvoices().each { invoice -> deleteInvoice(invoice.id) }
        getAllCompanies().each { company -> deleteCompany(company.id) }
    }

    int addInvoiceAndReturnId(Invoice invoice) {
        addAndReturnId(invoice, INVOICE_ENDPOINT)
    }

    int addCompanyAndReturnId(Company company) {
        addAndReturnId(company, COMPANY_ENDPOINT)
    }

    List<Invoice> addUniqueInvoices(int count) {
        (1..count).collect { id ->
            def invoice = invoice(id)
            invoice.id = addInvoiceAndReturnId(invoice)
            invoice
        }
    }

    List<Company> addUniqueCompanies(int count) {
        (1..count).collect { id ->
            def company = company(id)
            company.id = addCompanyAndReturnId(company)
            company
        }
    }

    List<Invoice> getAllInvoices() {
        getAll(Invoice[], INVOICE_ENDPOINT)
    }

    List<Company> getAllCompanies() {
        getAll(Company[], COMPANY_ENDPOINT)
    }

    Invoice getInvoiceById(long id) {
        getById(id, Invoice, INVOICE_ENDPOINT)
    }

    Company getCompanyById(long id) {
        getById(id, Company, COMPANY_ENDPOINT)
    }

    ResultActions deleteInvoice(long id) {
        mockMvc.perform(delete("$INVOICE_ENDPOINT/$id"))
                .andExpect(status().isOk())
    }

    ResultActions deleteCompany(long id) {
        mockMvc.perform(delete("$COMPANY_ENDPOINT/$id"))
                .andExpect(status().isOk())
    }

    String invoiceAsJson(long id) {
        def testCaseInvoice = invoice(id)
        testCaseInvoice.id = id
        jsonService.objectToString(testCaseInvoice)
    }

    String companyAsJson(long id) {
        def testCaseCompany = company(id)
        testCaseCompany.id = id
        jsonService.objectToString(testCaseCompany)
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

    private < T > int addAndReturnId(T item, String endpoint) {
        def itemId = mockMvc.perform(
                post(endpoint)
                        .content(jsonService.objectToString(item))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString
        Integer.valueOf(itemId)
    }

    private < T > T getAll(Class<T> clazz, String endpoint) {
        def response = mockMvc.perform(get(endpoint)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString
        jsonService.stringToObject(response, clazz)
    }

    private < T > T getById(long id, Class<T> clazz, String endpoint) {
        def invoiceAsString = mockMvc.perform(get("$endpoint/$id")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .response
                .contentAsString

        jsonService.stringToObject(invoiceAsString, clazz)
    }
}
