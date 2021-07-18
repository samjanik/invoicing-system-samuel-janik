package pl.futurecollars.invoicing.db.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.futurecollars.invoicing.db.Database;
import pl.futurecollars.invoicing.utils.FilesService;
import pl.futurecollars.invoicing.utils.JsonService;

@Configuration
public class DatabaseConfiguration {

    @Bean
    public IdService idService(FilesService filesService) {
        Path idFilePath = null;
        try {
            idFilePath = File.createTempFile("Ids", ".json").toPath();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initiate id database", e);
        }
        return new IdService(idFilePath, filesService);
    }

    @Bean
    public Database fileBasedDatabase(IdService idService, FilesService filesService, JsonService jsonService) {
        Path databaseFilePath = null;
        try {
            databaseFilePath = File.createTempFile("invoices", ".json").toPath();
        } catch (IOException e) {
            throw new RuntimeException("Failed to initiate invoices database", e);
        }
        return new FileBasedDatabase(databaseFilePath, idService, filesService, jsonService);
    }
}
