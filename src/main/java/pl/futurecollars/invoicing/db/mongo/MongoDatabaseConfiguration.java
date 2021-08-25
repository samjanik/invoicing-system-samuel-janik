package pl.futurecollars.invoicing.db.mongo;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "invoicing-system.database", havingValue = "mongo")
public class MongoDatabaseConfiguration {

    @Bean
    public Database<Invoice> invoiceMongoBasedDatabase(
        @Value("${invoicing-system.database.invoice.collection}") String collectionName,
        MongoDatabase mongoDatabase,
        MongoIdProvider mongoIdProvider) {

        MongoCollection<Invoice> collection = mongoDatabase.getCollection(collectionName, Invoice.class);
        log.debug("Creating invoice mongo database");
        return new MongoBasedDatabase<>(collection, mongoIdProvider);
    }

    @Bean
    public Database<Company> companyMongoBasedDatabase(
        @Value("${invoicing-system.database.company.collection}") String collectionName,
        MongoDatabase mongoDatabase,
        MongoIdProvider mongoIdProvider) {

        MongoCollection<Company> collection = mongoDatabase.getCollection(collectionName, Company.class);
        log.debug("Creating company mongo database");
        return new MongoBasedDatabase<>(collection, mongoIdProvider);
    }

    @Bean
    public MongoIdProvider mongoIdProvider(
        @Value("${invoicing-system.database.counter.collection}") String collectionName,
        MongoDatabase mongoDatabase) {

        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        return new MongoIdProvider(collection);

    }

    @Bean
    public MongoDatabase mongoDatabase(
        @Value("${invoicing-system.database.name}") String databaseName) {

        CodecRegistry pojoCodecRegistry =
            CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClient client = MongoClients.create();
        return client.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);
    }
}
