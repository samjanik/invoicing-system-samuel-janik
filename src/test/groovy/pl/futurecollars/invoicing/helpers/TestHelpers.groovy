package pl.futurecollars.invoicing.helpers

import pl.futurecollars.invoicing.model.Company
import pl.futurecollars.invoicing.model.Invoice
import pl.futurecollars.invoicing.model.InvoiceEntry
import pl.futurecollars.invoicing.model.Vat

import java.time.LocalDate

class TestHelpers {

    static company(int id) {
        Company.builder()
                .taxIdentificationNumber(("$id").repeat(10))
                .address("ul. Bukowińska 24d/$id 02-703 Warszawa, Polska")
                .name("iCode Trust $id Sp. z o.o")
                .build()
    }

    static product(int id) {
        InvoiceEntry.builder()
                .description("Programming course $id")
                .price(BigDecimal.valueOf(id * 1000))
                .vatValue(BigDecimal.valueOf(id * 1000 * 0.08))
                .vatRate(Vat.VAT_8)
                .build()
    }

    static invoice(int id) {
        Invoice.builder()
                .issueDate(LocalDate.now())
                .buyer(company(id))
                .seller(company(id + 1))
                .entries(List.of(product(id)))
                .build()
    }
}