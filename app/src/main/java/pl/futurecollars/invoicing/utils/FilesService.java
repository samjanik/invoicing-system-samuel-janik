package pl.futurecollars.invoicing.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import pl.futurecollars.invoicing.model.Invoice;

@Data
@AllArgsConstructor
public class FilesService {

    private int nextId = 0;
    private final Path invoicesPath;
    private final Path idFilePath;
    private JsonService jsonService = new JsonService();

    public FilesService(Path invoicesPath, Path idFilePath) {
        this.invoicesPath = invoicesPath;
        this.idFilePath = idFilePath;
    }

    public boolean containsID(int id, String line) {
        return line.contains("\"id\":" + id + ",");
    }

    public List<Invoice> readAllLines() {
        try {
            return Files.readAllLines(invoicesPath)
                .stream()
                .map(line -> jsonService.stringToObject(line))
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read invoices from file", e);
        }
    }

    public Optional<Invoice> readLineByID(int id) {
        try {
            return Files.readAllLines(invoicesPath)
                .stream()
                .filter(line -> containsID(id, line))
                .map(inv -> jsonService.stringToObject(inv))
                .findFirst();
        } catch (IOException e) {
            System.out.println("Database failed to get invoice with id: " + id);
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Invoice> rewriteLinesToFile(int id, Invoice updatedInvoice) {

        Optional<Invoice> invoiceWithID = readLineByID(id);

        if (invoiceWithID.isPresent()) {
            removeLineByID(id);
            updatedInvoice.setId(id);
            try {
                Files.write(invoicesPath, List.of(jsonService.objectToString(updatedInvoice)), StandardOpenOption.APPEND);
                return Optional.of(updatedInvoice);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Failed to update invoice with id: " + id);
            }
        }
        return Optional.empty();
    }

    public int appendLineToFile(Invoice invoice) {
        try {
            invoice.setId(getNextIdAndIncrement());
            Files.write(invoicesPath, List.of(jsonService.objectToString(invoice)), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Database failed to save invoice", e);
        }
        return invoice.getId();
    }

    public Optional<Invoice> removeLineByID(int id) {

        try {
            Optional<Invoice> deletedInvoice = readLineByID(id);
            List<String> updatedInvoiceList = Files.readAllLines(invoicesPath)
                .stream()
                .filter(line -> !containsID(id, line))
                .collect(Collectors.toList());
            Files.write(invoicesPath, updatedInvoiceList, StandardOpenOption.TRUNCATE_EXISTING);
            return deletedInvoice;
        } catch (IOException e) {
            System.out.println("Failed to delete invoice with id: " + id);
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public int getNextIdAndIncrement() {
        getLastTrackedID();
        nextId++;
        printIDtoTracker();
        return nextId;
    }

    public int getLastTrackedID() {
        try {
            List<String> lastUsedID = Files.readAllLines(idFilePath);
            if (lastUsedID.isEmpty()) {
                String line = "1";
                try {
                    Files.write(idFilePath, line.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                    return nextId;
                } catch (IOException e) {
                    throw new RuntimeException("Failed to initialize id database", e);
                }
            }
            return nextId = Integer.parseInt(lastUsedID.get(0));
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize id database", e);
        }
    }

    public void printIDtoTracker() {
        try {
            Files.write(idFilePath, String.valueOf(nextId).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to id file", e);
        }
    }
}
