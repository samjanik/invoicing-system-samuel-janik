package pl.futurecollars.invoicing.controller.invoice;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.invoice.InvoiceService;

@RestController
@AllArgsConstructor
public class InvoiceController implements InvoiceApi {

    private final InvoiceService invoiceService;

    @Override
    public List<Invoice> getAll() {
        return invoiceService.getAll();
    }

    @Override
    public long save(@RequestBody Invoice invoice) {
        return invoiceService.save(invoice);
    }

    @Override
    public ResponseEntity<Invoice> getById(@PathVariable long id) {
        return invoiceService.getById(id)
            .map(invoice -> ResponseEntity.ok().body(invoice))
            .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Invoice> updateById(@PathVariable long id, @RequestBody Invoice invoice) {
        return invoiceService.update(id, invoice)
            .map(updatedInvoice -> ResponseEntity.ok().body(updatedInvoice))
            .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Invoice> deleteById(@PathVariable long id) {
        return invoiceService.delete(id)
            .map(deletedInvoice -> ResponseEntity.ok().body(deletedInvoice))
            .orElse(ResponseEntity.notFound().build());
    }
}
