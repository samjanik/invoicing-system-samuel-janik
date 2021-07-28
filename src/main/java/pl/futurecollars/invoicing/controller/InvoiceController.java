package pl.futurecollars.invoicing.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

@RestController
@RequestMapping("invoices")
public class InvoiceController implements InvoiceApi {

    private final InvoiceService invoiceService;

    private InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Override
    public List<Invoice> getAll() {
        return invoiceService.getAll();
    }

    @Override
    public int save(@RequestBody Invoice invoice) {
        return invoiceService.save(invoice);
    }

    @Override
    public ResponseEntity<Invoice> getById(@PathVariable int id) {
        return invoiceService.getById(id)
            .map(invoice -> ResponseEntity.ok().body(invoice))
            .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Invoice> updateById(@PathVariable int id, @RequestBody Invoice invoice) {
        return invoiceService.update(id, invoice)
            .map(updatedInvoice -> ResponseEntity.ok().body(updatedInvoice))
            .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<Invoice> deleteById(@PathVariable int id) {
        return invoiceService.delete(id)
            .map(deletedInvoice -> ResponseEntity.ok().body(deletedInvoice))
            .orElse(ResponseEntity.notFound().build());
    }
}
