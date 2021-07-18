package pl.futurecollars.invoicing.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.file.FileBasedDatabase;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.service.InvoiceService;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.IdService;
import pl.futurecollars.invoicing.utils.JsonService;

@RestController
@RequestMapping("invoices")
public class InvoiceController {

    private FilesService filesService = new FilesService();
    private JsonService jsonService = new JsonService();

    private InvoiceService invoiceService = new InvoiceService(fileBasedDatabase(idService(filesService), filesService, jsonService));

    private IdService idService(FilesService filesService) {
        Path idFilePath = null;
        try {
            idFilePath = File.createTempFile("Ids", ".json").toPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new IdService(idFilePath, filesService);
    }

    private Database fileBasedDatabase(IdService idProvider, FilesService filesService, JsonService jsonService) {
        Path databaseFilePath = null;
        try {
            databaseFilePath = File.createTempFile("invoices", ".json").toPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new FileBasedDatabase(databaseFilePath, idProvider, filesService, jsonService);
    }

    @GetMapping
    public List<Invoice> getAll() {
        return invoiceService.getAll();
    }

    @PostMapping
    public int save(@RequestBody Invoice invoice) {
        return invoiceService.save(invoice);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getById(@PathVariable int id) {
        return invoiceService.getById(id)
            .map(invoice -> ResponseEntity.ok().body(invoice))
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Invoice> deleteById(@PathVariable int id) {
        return invoiceService.delete(id)
            .map(invoice -> ResponseEntity.ok().body(invoice))
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> updateById(@PathVariable int id, @RequestBody Invoice invoice) {
        return invoiceService.update(id, invoice)
            .map(deletedInvoice -> ResponseEntity.ok().body(deletedInvoice))
            .orElse(ResponseEntity.notFound().build());
    }

}
