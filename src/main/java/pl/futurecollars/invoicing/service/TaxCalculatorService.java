package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

@Service
@AllArgsConstructor
public class TaxCalculatorService {

    private final Database database;

    public BigDecimal income(String taxIdentificationNumber) {
        return database.visit(sellerPredicate(taxIdentificationNumber), InvoiceEntry::getPrice);
    }

    public BigDecimal costs(String taxIdentificationNumber) {
        return database.visit(buyerPredicate(taxIdentificationNumber), InvoiceEntry::getPrice);
    }

    public BigDecimal incomingVat(String taxIdentificationNumber) {
        return database.visit(sellerPredicate(taxIdentificationNumber), InvoiceEntry::getVatValue);
    }

    public BigDecimal outgoingVat(String taxIdentificationNumber) {
        return database.visit(buyerPredicate(taxIdentificationNumber), InvoiceEntry::getVatValue);
    }

    public BigDecimal getEarnings(String taxIdentificationNumber) {
        return income(taxIdentificationNumber).subtract(costs(taxIdentificationNumber));
    }

    public BigDecimal getVatToReturn(String taxIdentificationNumber) {
        return incomingVat(taxIdentificationNumber).subtract(outgoingVat(taxIdentificationNumber));
    }

    public TaxCalculatorResults calculateTaxes(String taxIdentificationNumber) {
        return TaxCalculatorResults.builder()
            .income(income(taxIdentificationNumber))
            .costs(costs(taxIdentificationNumber))
            .earnings(getEarnings(taxIdentificationNumber))
            .collectedVat(incomingVat(taxIdentificationNumber))
            .paidVat(outgoingVat(taxIdentificationNumber))
            .dueVat(getVatToReturn(taxIdentificationNumber))
            .build();
    }

    private Predicate<Invoice> sellerPredicate(String taxIdentificationNumber) {
        return invoice -> taxIdentificationNumber.equals(invoice.getSeller().getTaxIdentificationNumber());
    }

    private Predicate<Invoice> buyerPredicate(String taxIdentificationNumber) {
        return invoice -> taxIdentificationNumber.equals(invoice.getBuyer().getTaxIdentificationNumber());
    }
}
