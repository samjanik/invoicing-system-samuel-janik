package pl.futurecollars.invoicing.db.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.WithId;

public class InMemoryDatabase<T extends WithId> implements Database<T> {

    public long nextId = 1;
    private final Map<Long, T> itemsInMemoryDatabase = new HashMap<>();

    @Override
    public long save(T item) {

        item.setId(nextId);
        itemsInMemoryDatabase.put(nextId, item);

        return nextId++;
    }

    @Override
    public Optional<T> getById(long id) {
        return Optional.ofNullable(itemsInMemoryDatabase.get(id));
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(itemsInMemoryDatabase.values());
    }

    @Override
    public Optional<T> update(long id, T updatedItem) {
        updatedItem.setId(id);
        return Optional.ofNullable(itemsInMemoryDatabase.put(id, updatedItem));
    }

    @Override
    public Optional<T> delete(long id) {
        return Optional.ofNullable(itemsInMemoryDatabase.remove(id));
    }
}
