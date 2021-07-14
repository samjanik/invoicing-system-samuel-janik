package pl.futurecollars.invoicing.model;

import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Invoice {

    private int id;
    private LocalDate issueDate;
    private Company buyer;
    private Company seller;
    private List<InvoiceEntry> entries;

    public Invoice(LocalDate issueDate, Company buyer, Company seller, List<InvoiceEntry> entries) {
        this.issueDate = issueDate;
        this.buyer = buyer;
        this.seller = seller;
        this.entries = entries;
    }
}
