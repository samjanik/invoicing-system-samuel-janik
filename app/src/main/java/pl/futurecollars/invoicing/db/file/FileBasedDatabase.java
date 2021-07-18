package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.file.IdService;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

@AllArgsConstructor
public class FileBasedDatabase implements Database {

    private final Path databasePath;
    private final IdService idService;
    private final FilesService filesService;
    private final JsonService jsonService;

    @Override
    public int save(Invoice invoice) {
        try {
            invoice.setId(idService.getNextIdAndIncrement());
            filesService.appendLineToFile(databasePath, jsonService.objectToString(invoice));
            return invoice.getId();
        } catch (IOException ex) {
            throw new RuntimeException("Database failed to save invoice", ex);
        }
    }

    @Override
    public Optional<Invoice> getById(int id) {
        try {
            return filesService.readAllLines(databasePath)
                .stream()
                .filter(line -> containsId(id, line))
                .map(inv -> jsonService.stringToObject(inv))
                .findFirst();
        } catch (IOException ex) {
            throw new RuntimeException("Database failed to get invoice with id: " + id, ex);
        }
    }

    @Override
    public List<Invoice> getAll() {
        try {
            return filesService.readAllLines(databasePath)
                .stream()
                .map(line -> jsonService.stringToObject(line))
                .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read invoices from the file", ex);
        }
    }

    @Override
    public Optional<Invoice> update(int id, Invoice updatedInvoice) {
        Optional<Invoice> invoiceWithID = getById(id);
        if (invoiceWithID.isEmpty()) {
            throw new RuntimeException("Id " + id + " does not exist");
        } else {
            delete(id);
            updatedInvoice.setId(id);
            try {
                filesService.writeLinesToFile(databasePath, List.of(jsonService.objectToString(updatedInvoice)));
            } catch (IOException e) {
                throw new RuntimeException("Failed to update invoice with id: " + id, e);
            }
            return Optional.of(updatedInvoice);
        }
    }

    @Override
    public Optional<Invoice> delete(int id) {

        Optional<Invoice> deletedInvoice = getById(id);
        try {
            List<String> updatedList = filesService.readAllLines(databasePath)
                .stream()
                .filter(line -> !containsId(id, line))
                .collect(Collectors.toList());
            filesService.writeLinesToFile(databasePath, updatedList);
            return deletedInvoice;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to delete invoice with id: " + id, ex);
        }
    }

    public boolean containsId(int id, String line) {
        return line.contains("\"id\":" + id + ",");
    }
}
