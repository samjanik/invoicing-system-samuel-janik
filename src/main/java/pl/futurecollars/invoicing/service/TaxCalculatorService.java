package pl.futurecollars.invoicing.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Car;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.InvoiceEntry;

@Service
@AllArgsConstructor
public class TaxCalculatorService {

    private final Database database;

    public BigDecimal income(String taxIdentificationNumber) {
        return database.visit(sellerPredicate(taxIdentificationNumber), InvoiceEntry::getNetPrice);
    }

    public BigDecimal costs(String taxIdentificationNumber) {
        return database.visit(buyerPredicate(taxIdentificationNumber), this::getCostValueWithPrivateCarExpense);
    }

    public BigDecimal getEarnings(String taxIdentificationNumber) {
        return income(taxIdentificationNumber).subtract(costs(taxIdentificationNumber));
    }

    public BigDecimal collectedVat(String taxIdentificationNumber) {
        return database.visit(sellerPredicate(taxIdentificationNumber), InvoiceEntry::getVatValue);
    }

    public BigDecimal paidVat(String taxIdentificationNumber) {
        return database.visit(buyerPredicate(taxIdentificationNumber), this::getVatValueWithPrivateCarExpense);
    }

    public BigDecimal dueVat(String taxIdentificationNumber) {
        return collectedVat(taxIdentificationNumber).subtract(paidVat(taxIdentificationNumber));
    }

    private BigDecimal getVatValueWithPrivateCarExpense(InvoiceEntry invoiceEntry) {
        return Optional.ofNullable(invoiceEntry.getCarExpense())
            .map(Car::isPrivateExpense)
            .map(personalCarUsage -> personalCarUsage ? BigDecimal.valueOf(5, 1) : BigDecimal.ONE)
            .map(proportion -> invoiceEntry.getVatValue().multiply(proportion))
            .map(value -> value.setScale(2, RoundingMode.FLOOR))
            .orElse(invoiceEntry.getVatValue());
    }

    private BigDecimal getCostValueWithPrivateCarExpense(InvoiceEntry invoiceEntry) {
        return invoiceEntry.getNetPrice()
            .add(invoiceEntry.getVatValue())
            .subtract(getVatValueWithPrivateCarExpense(invoiceEntry));
    }

    public TaxCalculatorResults calculateTaxes(Company company) {
        String taxIdentificationNumber = company.getTaxIdentificationNumber();

        return TaxCalculatorResults.builder()
            .income(income(taxIdentificationNumber))
            .costs(costs(taxIdentificationNumber))
            .earnings(getEarnings(taxIdentificationNumber))
            .collectedVat(collectedVat(taxIdentificationNumber))
            .paidVat(paidVat(taxIdentificationNumber))
            .dueVat(dueVat(taxIdentificationNumber))
            .build();
    }

    private Predicate<Invoice> sellerPredicate(String taxIdentificationNumber) {
        return invoice -> taxIdentificationNumber.equals(invoice.getSeller().getTaxIdentificationNumber());
    }

    private Predicate<Invoice> buyerPredicate(String taxIdentificationNumber) {
        return invoice -> taxIdentificationNumber.equals(invoice.getBuyer().getTaxIdentificationNumber());
    }
}
