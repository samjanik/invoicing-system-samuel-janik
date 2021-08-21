package pl.futurecollars.invoicing.db;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoicing.db.file.FileBasedDatabase;
import pl.futurecollars.invoicing.db.file.IdService;
import pl.futurecollars.invoicing.db.jpa.InvoiceRepository;
import pl.futurecollars.invoicing.db.jpa.JpaDatabase;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.db.mongo.MongoBasedDatabase;
import pl.futurecollars.invoicing.db.mongo.MongoIdProvider;
import pl.futurecollars.invoicing.db.sql.SqlDatabase;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

@Slf4j
@Configuration
public class DatabaseConfiguration {

    private static Path idFilePath;
    private static Path databaseFilePath;

    @Bean
    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "file")
    public IdService idService(FilesService filesService,
                               @Value("${invoicing-system.database.prefix}") String filePrefix,
                               @Value("${invoicing-system.database.id.file}") String idFile) {
        try {
            idFilePath = Files.createTempFile(filePrefix, idFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initiate id database", e);
        }
        return new IdService(idFilePath, filesService);
    }

    @Bean
    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "file")
    public Database fileBasedDatabase(IdService idService,
                                      FilesService filesService,
                                      JsonService jsonService,
                                      @Value("${invoicing-system.database.prefix}") String filePrefix,
                                      @Value("${invoicing-system.database.invoices.file}") String invoicesFile
    ) {
        try {
            databaseFilePath = Files.createTempFile(filePrefix, invoicesFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initiate invoices database", e);
        }
        log.debug("Creating file-based database: " + databaseFilePath.toString());
        return new FileBasedDatabase(databaseFilePath, idService, filesService, jsonService);
    }

    @Bean
    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "memory")
    public Database inMemoryDatabase() {
        log.debug("Creating in-memory database");
        return new InMemoryDatabase();
    }

    @Bean
    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "sql")
    public Database sqlDatabase(JdbcTemplate jdbcTemplate) {
        log.debug("Creating sql database");
        return new SqlDatabase(jdbcTemplate);
    }

    @Bean
    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "jpa")
    public Database jpaDatabase(InvoiceRepository invoiceRepository) {
        log.debug("Creating jpa database");
        return new JpaDatabase(invoiceRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "mongo")
    public Database mongoBasedDatabase(

        @Value("${invoicing-system.database.collection.name}") String collectionName,
        MongoDatabase mongoDatabase,
        MongoIdProvider mongoIdProvider) {

        MongoCollection<Invoice> collection = mongoDatabase.getCollection(collectionName, Invoice.class);

        return new MongoBasedDatabase(collection, mongoIdProvider);
    }

    @Bean
    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "mongo")
    public MongoIdProvider mongoIdProvider(
        @Value("${invoicing-system.database.counter.collection}") String collectionName,
                           MongoDatabase mongoDatabase) {

        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);

        return new MongoIdProvider(collection);

    }

    @Bean
    @ConditionalOnProperty(name = "invoicing-system.database", havingValue = "mongo")
    public MongoDatabase mongoDatabase(
        @Value("${invoicing-system.database.name}") String databaseName) {

        CodecRegistry pojoCodecRegistry =
            CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClient client = MongoClients.create();
        return client.getDatabase(databaseName).withCodecRegistry(pojoCodecRegistry);

    }
}
