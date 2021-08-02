package pl.futurecollars.invoicing.controller

import pl.futurecollars.invoicing.model.Car
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry

import static pl.futurecollars.invoicing.helpers.TestHelpers.company

import spock.lang.Unroll

@Unroll
class TaxCalculatorControllerIntegrationTest extends AbstractControllerTest {

    def "zeros are returned when there are no invoices in the system"() {
        when:
        def taxCalculatorResults = calculateTax(company(0))

        then:
        taxCalculatorResults.income == 0
        taxCalculatorResults.costs == 0
        taxCalculatorResults.earnings == 0
        taxCalculatorResults.collectedVat == 0
        taxCalculatorResults.paidVat == 0
        taxCalculatorResults.dueVat == 0
    }

    def "zeros are returned when tax id is not matching"() {
        given:
        addUniqueInvoices(10)

        when:
        def taxCalculatorResult = calculateTax(company(-14))

        then:
        taxCalculatorResult.income == 0
        taxCalculatorResult.costs == 0
        taxCalculatorResult.earnings == 0
        taxCalculatorResult.collectedVat == 0
        taxCalculatorResult.paidVat == 0
        taxCalculatorResult.dueVat == 0
    }

    def "sum of all products is returned when tax id is matching"() {
        given:
        addUniqueInvoices(10)

        when:
        def taxCalculatorResult = calculateTax(company(5))

        then:
        taxCalculatorResult.income == 15000
        taxCalculatorResult.costs == 0
        taxCalculatorResult.earnings == 15000
        taxCalculatorResult.collectedVat == 1200.0
        taxCalculatorResult.paidVat == 0
        taxCalculatorResult.dueVat == 1200.0

        when:
        taxCalculatorResult = calculateTax(company(10))

        then:
        taxCalculatorResult.income == 55000
        taxCalculatorResult.costs == 0
        taxCalculatorResult.earnings == 55000
        taxCalculatorResult.collectedVat == 4400.0
        taxCalculatorResult.paidVat == 0
        taxCalculatorResult.dueVat == 4400.0

        when:
        taxCalculatorResult = calculateTax(company(15))

        then:
        taxCalculatorResult.income == 0
        taxCalculatorResult.costs == 15000
        taxCalculatorResult.earnings == -15000
        taxCalculatorResult.collectedVat == 0
        taxCalculatorResult.paidVat == 1200.0
        taxCalculatorResult.dueVat == -1200.0
    }

    def "correct values are returned when company was buyer and seller"() {
        given:
        addUniqueInvoices(15) // sellers: 1-15, buyers: 10-25, 10-15 overlapping

        when:
        def taxCalculatorResult = calculateTax(company(12))

        then:
        taxCalculatorResult.income == 78000
        taxCalculatorResult.costs == 3000
        taxCalculatorResult.earnings == 75000
        taxCalculatorResult.collectedVat == 6240.0
        taxCalculatorResult.paidVat == 240.0
        taxCalculatorResult.dueVat == 6000.0
    }

    def "tax is calculated correctly when car is used for personal purposes"() {
        given:
        def invoice = Invoice.builder()
                .seller(company(1))
                .buyer(company(2))
                .entries(List.of(
                        InvoiceEntry.builder()
                                .vatValue(BigDecimal.valueOf(23.45))
                                .netPrice(BigDecimal.valueOf(100))
                                .carExpense(
                                        Car.builder()
                                                .privateExpense(true)
                                                .build()
                                )
                                .build()
                ))
                .build()

        addInvoiceAndReturnId(invoice)

        when:
        def taxCalculatorResponse = calculateTax(invoice.getSeller())

        then: "no proportion - it does not apply to seller"
        taxCalculatorResponse.income == 100
        taxCalculatorResponse.costs == 0
        taxCalculatorResponse.earnings == 100
        taxCalculatorResponse.collectedVat == 23.45
        taxCalculatorResponse.paidVat == 0
        taxCalculatorResponse.dueVat == 23.45

        when:
        taxCalculatorResponse = calculateTax(invoice.getBuyer())

        then: "proportion applied - it applies to buyer"
        taxCalculatorResponse.income == 0
        taxCalculatorResponse.costs == 111.73
        taxCalculatorResponse.earnings == -111.73
        taxCalculatorResponse.collectedVat == 0
        taxCalculatorResponse.paidVat == 11.72
        taxCalculatorResponse.dueVat == -11.72
    }

}
