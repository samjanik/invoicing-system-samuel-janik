package pl.futurecollars.invoicing.controller

import pl.futurecollars.invoicing.model.Car
import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat

import java.time.LocalDate

import static pl.futurecollars.invoicing.helpers.TestHelpers.company

import spock.lang.Unroll

@Unroll
class TaxCalculatorControllerIntegrationTest extends AbstractControllerTest {

    def "zeros are returned when there are no invoices in the system"() {
        when:
        def taxCalculatorResults = calculateTax(company(0))

        then:
        with(taxCalculatorResults) {
            income == 0
            costs == 0
            earnings == 0
            collectedVat == 0
            paidVat == 0
            dueVat == 0
        }
    }

    def "zeros are returned when tax id is not matching"() {
        given:
        addUniqueInvoices(10)

        when:
        def taxCalculatorResult = calculateTax(company(-14))

        then:
        with(taxCalculatorResult) {
            income == 0
            costs == 0
            earnings == 0
            collectedVat == 0
            paidVat == 0
            dueVat == 0
        }
    }

    def "sum of all products is returned when tax id is matching"() {
        given:
        addUniqueInvoices(10)

        when:
        def taxCalculatorResult = calculateTax(company(5))

        then:
        with(taxCalculatorResult) {
            income == 15000
            costs == 0
            earnings == 15000
            collectedVat == 1200.0
            paidVat == 0
            dueVat == 1200.0
        }

        when:
        taxCalculatorResult = calculateTax(company(10))

        then:
        with(taxCalculatorResult) {
            income == 55000
            costs == 0
            earnings == 55000
            collectedVat == 4400.0
            paidVat == 0
            dueVat == 4400.0
        }

        when:
        taxCalculatorResult = calculateTax(company(15))

        then:
        with(taxCalculatorResult) {
            income == 0
            costs == 15000
            earnings == -15000
            collectedVat == 0
            paidVat == 1200.0
            dueVat == -1200.0
        }
    }

    def "correct values are returned when company was buyer and seller"() {
        given:
        addUniqueInvoices(15) // sellers: 1-15, buyers: 10-25, 10-15 overlapping

        when:
        def taxCalculatorResult = calculateTax(company(12))

        then:
        with(taxCalculatorResult) {
            income == 78000
            costs == 3000
            earnings == 75000
            collectedVat == 6240.0
            paidVat == 240.0
            dueVat == 6000.0
        }
    }

    def "tax is calculated correctly when car is used for personal purposes"() {

        addInvoiceAndReturnId(invoice)
        given:
        def invoice = Invoice.builder()
                .date(LocalDate.now())
                .number("FAV/18/R/063128/08/21/FCJ")
                .seller(company(1))
                .buyer(company(2))
                .entries(List.of(
                        InvoiceEntry.builder()
                                .vatValue(BigDecimal.valueOf(23.45))
                                .vatRate(Vat.VAT_23)
                                .quantity(1.0)
                                .netPrice(BigDecimal.valueOf(100))
                                .carExpense(
                                        Car.builder()
                                                .privateExpense(true)
                                                .registrationNumber("SL 468345")
                                                .build()
                                )
                                .build()
                ))
                .build()

        when:
        def taxCalculatorResponse = calculateTax(invoice.getSeller())

        then: "no proportion - it does not apply to seller"
        with(taxCalculatorResponse) {
            income == 100
            costs == 0
            earnings == 100
            collectedVat == 23.45
            paidVat == 0
            dueVat == 23.45
        }

        when:
        taxCalculatorResponse = calculateTax(invoice.getBuyer())

        then: "proportion applied - it applies to buyer"
        with(taxCalculatorResponse) {
            income == 0
            costs == 111.73
            earnings == -111.73
            collectedVat == 0
            paidVat == 11.72
            dueVat == -11.72
        }
    }

    def "All calculations are executed correctly"() {
        given:
        def ourCompany = Company.builder()
                .name("Mygol")
                .address("67 Main St")
                .taxIdentificationNumber("1234")
                .pensionInsurance(514.57)
                .healthInsurance(319.94)
                .build()

        def invoiceWithIncome = Invoice.builder()
                .number("FAV/18/R/063128/08/21/FCJ")
                .date(LocalDate.now())
                .seller(ourCompany)
                .buyer(company(2))
                .entries(List.of(
                        InvoiceEntry.builder()
                                .quantity(1.0)
                                .netPrice(76011.62)
                                .vatValue(0.0)
                                .vatRate(Vat.VAT_0)
                                .build()
                ))
                .build()

        def invoiceWithCosts = Invoice.builder()
                .number("FAV/18/R/063128/08/21/FCJ")
                .date(LocalDate.now())
                .seller(company(4))
                .buyer(ourCompany)
                .entries(List.of(
                        InvoiceEntry.builder()
                                .quantity(1.0)
                                .netPrice(11329.47)
                                .vatValue(0.0)
                                .vatRate(Vat.VAT_0)
                                .build()
                ))
                .build()

        addInvoiceAndReturnId(invoiceWithIncome)
        addInvoiceAndReturnId(invoiceWithCosts)

        when:
        def taxCalculatorResponse = calculateTax(ourCompany)

        then:
        with(taxCalculatorResponse) {
            income == 76011.62
            costs == 11329.47
            earnings == 64682.15
            pensionInsurance == 514.57
            earningsLessPensionInsurance == 64167.58
            earningsLessPensionInsuranceRoundedTaxCalculationBase == 64168
            incomeTax == 12191.92
            healthInsuranceIncurredCost == 319.94
            healthInsuranceDeductible == 275.50
            incomeTaxLessHealthInsurance == 11916.42
            finalIncomeTax == 11916

            collectedVat == 0
            paidVat == 0
            dueVat == 0
        }
    }
}
