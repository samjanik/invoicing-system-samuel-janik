package pl.futurecollars.invoicing.config;

import java.nio.file.Path;

public class Config {

    public static final Path DATABASE_LOCATION = Path.of("db/invoices.json");
    public static final Path ID_FILE_LOCATION = Path.of("db/id.json");

}
