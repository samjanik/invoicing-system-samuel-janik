package pl.futurecollars.invoicing.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = {"invoice-controller"})
public class InvoiceController {

    private final InvoiceService invoiceService;

    private InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping(produces = { "application/json;charset=UTF-8" })
    @ApiOperation(value = "Get list of all invoices")
    public List<Invoice> getAll() {
        return invoiceService.getAll();
    }

    @PostMapping(produces = { "application/json;charset=UTF-8" })
    @ApiOperation(value = "Add new invoice to the system")
    public int save(@RequestBody Invoice invoice) {
        return invoiceService.save(invoice);
    }

    @GetMapping(value = "/{id}", produces = { "application/json;charset=UTF-8" })
    @ApiOperation(value = "Get invoice by id")
    public ResponseEntity<Invoice> getById(@PathVariable int id) {
        return invoiceService.getById(id)
            .map(invoice -> ResponseEntity.ok().body(invoice))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/{id}", produces = { "application/json;charset=UTF-8" })
    @ApiOperation(value = "Update invoice with given id")
    public ResponseEntity<Invoice> updateById(@PathVariable int id, @RequestBody Invoice invoice) {
        return invoiceService.update(id, invoice)
            .map(updatedInvoice -> ResponseEntity.ok().body(updatedInvoice))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete invoice with given id")
    public ResponseEntity<Invoice> deleteById(@PathVariable int id) {
        return invoiceService.delete(id)
            .map(deletedInvoice -> ResponseEntity.ok().body(deletedInvoice))
            .orElse(ResponseEntity.notFound().build());
    }
}
