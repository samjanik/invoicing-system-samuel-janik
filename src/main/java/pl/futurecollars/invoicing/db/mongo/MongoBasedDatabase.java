package pl.futurecollars.invoicing.db.mongo;

import com.mongodb.client.MongoCollection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.util.Streamable;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Invoice;

@RequiredArgsConstructor
public class MongoBasedDatabase implements Database {

    private final MongoCollection<Invoice> invoices;
    private final MongoIdProvider idProvider;

    @Override
    public long save(Invoice invoice) {
        invoice.setId(idProvider.getNextIdAndIncrement());
        invoices.insertOne(invoice);
        return invoice.getId();
    }

    @Override
    public Optional<Invoice> getById(long id) {
        return Optional.ofNullable(invoices.find(idFilter(id)).first());
    }

    @Override
    public List<Invoice> getAll() {
        return Streamable.of(invoices.find()).toList();
    }

    @Override
    public Optional<Invoice> update(long id, Invoice updatedInvoice) {
        updatedInvoice.setId(id);
        invoices.findOneAndReplace(idFilter(id), updatedInvoice);
        Invoice updatedDocument = invoices.find(idFilter(id)).first();
        return Optional.ofNullable(updatedDocument);
    }

    @Override
    public Optional<Invoice> delete(long id) {
        Invoice deletedDocument = invoices.findOneAndDelete(idFilter(id));
        return Optional.ofNullable(deletedDocument);
    }

    private Document idFilter(long id) {
        return new Document("_id", id);
    }
}
