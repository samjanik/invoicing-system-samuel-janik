package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "invoicing-system.database", havingValue = "file")
public class FileDatabaseConfiguration {

    private static Path idFilePath;
    private static Path databaseFilePath;

    @Bean
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
    public Database<Invoice> invoiceFileBasedDatabase(
        IdService idService,
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
        log.debug("Creating file-based invoices database: " + databaseFilePath.toString());
        return new FileBasedDatabase<>(databaseFilePath, idService, filesService, jsonService, Invoice.class);
    }

    @Bean
    public Database<Company> companyFileBasedDatabase(
        IdService idService,
        FilesService filesService,
        JsonService jsonService,
        @Value("${invoicing-system.database.prefix}") String filePrefix,
        @Value("${invoicing-system.database.companies.file}") String companiesFile
    ) {
        try {
            databaseFilePath = Files.createTempFile(filePrefix, companiesFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initiate companies database", e);
        }
        log.debug("Creating file-based companies database: " + databaseFilePath.toString());
        return new FileBasedDatabase<>(databaseFilePath, idService, filesService, jsonService, Company.class);
    }
}
