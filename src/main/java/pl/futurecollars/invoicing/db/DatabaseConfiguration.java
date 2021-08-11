package pl.futurecollars.invoicing.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.futurecollars.invoicing.db.file.FileBasedDatabase;
import pl.futurecollars.invoicing.db.file.IdService;
import pl.futurecollars.invoicing.db.memory.InMemoryDatabase;
import pl.futurecollars.invoicing.db.sql.SqlDatabase;
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
}
