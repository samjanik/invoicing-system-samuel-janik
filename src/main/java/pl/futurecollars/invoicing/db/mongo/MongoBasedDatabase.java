package pl.futurecollars.invoicing.db.mongo;

import com.mongodb.client.MongoCollection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.util.Streamable;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.db.WithId;

@RequiredArgsConstructor
public class MongoBasedDatabase<T extends WithId> implements Database<T> {

    private final MongoCollection<T> items;
    private final MongoIdProvider idProvider;

    @Override
    public long save(T item) {
        item.setId(idProvider.getNextIdAndIncrement());
        items.insertOne(item);
        return item.getId();
    }

    @Override
    public Optional<T> getById(long id) {
        return Optional.ofNullable(items.find(idFilter(id)).first());
    }

    @Override
    public List<T> getAll() {
        return Streamable.of(items.find()).toList();
    }

    @Override
    public Optional<T> update(long id, T updatedItem) {
        updatedItem.setId(id);
        items.findOneAndReplace(idFilter(id), updatedItem);
        T updatedDocument = items.find(idFilter(id)).first();
        return Optional.ofNullable(updatedDocument);
    }

    @Override
    public Optional<T> delete(long id) {
        T deletedDocument = items.findOneAndDelete(idFilter(id));
        return Optional.ofNullable(deletedDocument);
    }

    private Document idFilter(long id) {
        return new Document("_id", id);
    }
}
