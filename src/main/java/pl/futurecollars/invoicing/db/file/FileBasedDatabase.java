package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.WithId;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

@AllArgsConstructor
public class FileBasedDatabase<T extends WithId> implements Database<T> {

    private final Path databasePath;
    private final IdService idService;
    private final FilesService filesService;
    private final JsonService jsonService;
    private final Class<T> clazz;

    @Override
    public long save(T item) {
        try {
            item.setId(idService.getNextIdAndIncrement());
            filesService.appendLineToFile(databasePath, jsonService.objectToString(item));
            return item.getId();
        } catch (IOException ex) {
            throw new RuntimeException("Database failed to save item", ex);
        }
    }

    @Override
    public Optional<T> getById(long id) {
        try {
            return filesService.readAllLines(databasePath)
                .stream()
                .filter(line -> containsId(id, line))
                .map(inv -> jsonService.stringToObject(inv, clazz))
                .findFirst();
        } catch (IOException ex) {
            throw new RuntimeException("Database failed to get item with id: " + id, ex);
        }
    }

    @Override
    public List<T> getAll() {
        try {
            return filesService.readAllLines(databasePath)
                .stream()
                .map(line -> jsonService.stringToObject(line, clazz))
                .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read items from the file", ex);
        }
    }

    @Override
    public Optional<T> update(long id, T updatedItem) {
        try {
            Optional<T> itemWithID = getById(id);
            if (itemWithID.isPresent()) {
                delete(id);
                updatedItem.setId(id);
                filesService.appendLineToFile(databasePath, jsonService.objectToString(updatedItem));
            }
            return itemWithID.isEmpty() ? Optional.empty() : Optional.of(updatedItem);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to update item with id: " + id, ex);
        }
    }

    @Override
    public Optional<T> delete(long id) {

        try {
            Optional<T> deletedItem = getById(id);
            List<String> updatedList = filesService.readAllLines(databasePath)
                .stream()
                .filter(line -> !containsId(id, line))
                .collect(Collectors.toList());
            filesService.writeLinesToFile(databasePath, updatedList);
            return deletedItem;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to delete item with id: " + id, ex);
        }
    }

    public boolean containsId(long id, String line) {
        return line.contains("\"id\":" + id + ",");
    }
}
