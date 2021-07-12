package pl.futurecollars.invoicing.db.file;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.utils.FilesService;

@AllArgsConstructor
public class FileBasedDatabase implements Database {

    private final FilesService filesService;

    @Override
    public int save(Invoice invoice) {
        return filesService.appendLineToFile(invoice);
    }

    @Override
    public Optional<Invoice> getById(int id) {
        return filesService.readLineByID(id);
    }

    @Override
    public List<Invoice> getAll() {
        return filesService.readAllLines();
    }

    @Override
    public Optional<Invoice> update(int id, Invoice updatedInvoice) {
        return filesService.rewriteLinesToFile(id, updatedInvoice);
    }

    @Override
    public Optional<Invoice> delete(int id) {
        return filesService.removeLineByID(id);
    }
}
