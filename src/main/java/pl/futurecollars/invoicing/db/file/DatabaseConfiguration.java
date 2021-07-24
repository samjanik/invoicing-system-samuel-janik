package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

@Configuration
public class DatabaseConfiguration {

    private static Path idFilePath;
    private static Path databaseFilePath;
    private static final String DATABASE_LOCATION = "db";
    private static final String ID_FILE_NAME = "id.json";
    private static final String INVOICES_FILE_NAME = "invoices.json";

    @Bean
    public IdService idService(FilesService filesService) {
        idFilePath = null;
        try {
            idFilePath = Files.createTempFile(DATABASE_LOCATION, ID_FILE_NAME);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initiate id database", e);
        }
        return new IdService(idFilePath, filesService);
    }

    @Bean
    public Database fileBasedDatabase(IdService idService, FilesService filesService, JsonService jsonService) {
        databaseFilePath = null;
        try {
            databaseFilePath = Files.createTempFile(DATABASE_LOCATION, INVOICES_FILE_NAME);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initiate invoices database", e);
        }
        return new FileBasedDatabase(databaseFilePath, idService, filesService, jsonService);
    }
}
