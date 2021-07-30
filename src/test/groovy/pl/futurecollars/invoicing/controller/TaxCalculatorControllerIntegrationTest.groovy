package pl.futurecollars.invoicing.controller

import spock.lang.Unroll

@Unroll
class TaxCalculatorControllerIntegrationTest extends AbstractControllerTest {

    def "zeros are returned when there are no invoices in the system"() {
        when:
        def taxCalculatorResults = calculateTax("0")

        then:
        taxCalculatorResults.income == 0
        taxCalculatorResults.costs == 0
        taxCalculatorResults.earnings == 0
        taxCalculatorResults.incomingVat == 0
        taxCalculatorResults.outgoingVat == 0
        taxCalculatorResults.vatToReturn == 0
    }

    def "zeros are returned when tax id is not matching"() {
        given:
        addUniqueInvoices(10)

        when:
        def taxCalculatorResult = calculateTax("no_match")

        then:
        taxCalculatorResult.income == 0
        taxCalculatorResult.costs == 0
        taxCalculatorResult.earnings == 0
        taxCalculatorResult.incomingVat == 0
        taxCalculatorResult.outgoingVat == 0
        taxCalculatorResult.vatToReturn == 0
    }

    def "sum of all products is returned when tax id is matching"() {
        given:
        addUniqueInvoices(10)

        when:
        def taxCalculatorResult = calculateTax("5")

        then:
        taxCalculatorResult.income == 15000
        taxCalculatorResult.costs == 0
        taxCalculatorResult.earnings == 15000
        taxCalculatorResult.incomingVat == 1200.0
        taxCalculatorResult.outgoingVat == 0
        taxCalculatorResult.vatToReturn == 1200.0

        when:
        taxCalculatorResult = calculateTax("10")

        then:
        taxCalculatorResult.income == 55000
        taxCalculatorResult.costs == 0
        taxCalculatorResult.earnings == 55000
        taxCalculatorResult.incomingVat == 4400.0
        taxCalculatorResult.outgoingVat == 0
        taxCalculatorResult.vatToReturn == 4400.0

        when:
        taxCalculatorResult = calculateTax("15")

        then:
        taxCalculatorResult.income == 0
        taxCalculatorResult.costs == 15000
        taxCalculatorResult.earnings == -15000
        taxCalculatorResult.incomingVat == 0
        taxCalculatorResult.outgoingVat == 1200.0
        taxCalculatorResult.vatToReturn == -1200.0
    }

    def "correct values are returned when company was buyer and seller"() {
        given:
        addUniqueInvoices(15) // sellers: 1-15, buyers: 10-25, 10-15 overlapping

        when:
        def taxCalculatorResult = calculateTax("12")

        then:
        taxCalculatorResult.income == 78000
        taxCalculatorResult.costs == 3000
        taxCalculatorResult.earnings == 75000
        taxCalculatorResult.incomingVat == 6240.0
        taxCalculatorResult.outgoingVat == 240.0
        taxCalculatorResult.vatToReturn == 6000.0
    }

}
