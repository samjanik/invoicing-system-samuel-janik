package pl.futurecollars.invoicing.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;

@RestController
@RequestMapping("invoices")
public class InvoiceController {

    private InvoiceService invoiceService;

    private InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping(produces = { "application/json;charset=UTF-8" })
    public List<Invoice> getAll() {
        return invoiceService.getAll();
    }

    @PostMapping(produces = { "application/json;charset=UTF-8" })
    public int save(@RequestBody Invoice invoice) {
        return invoiceService.save(invoice);
    }

    @GetMapping(value = "/{id}", produces = { "application/json;charset=UTF-8" })
    public ResponseEntity<Invoice> getById(@PathVariable int id) {
        return invoiceService.getById(id)
            .map(invoice -> ResponseEntity.ok().body(invoice))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{id}", produces = { "application/json;charset=UTF-8" })
    public ResponseEntity<Invoice> updateById(@PathVariable int id, @RequestBody Invoice invoice) {
        return invoiceService.update(id, invoice)
            .map(updatedInvoice -> ResponseEntity.ok().body(updatedInvoice))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Invoice> deleteById(@PathVariable int id) {
        return invoiceService.delete(id)
            .map(deletedInvoice -> ResponseEntity.ok().body(deletedInvoice))
            .orElse(ResponseEntity.notFound().build());
    }
}
