package pl.futurecollars.invoicing.db.file;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import lombok.Data;
import pl.futurecollars.invoicing.utils.FilesService;

@Data
public class IdService {

    private final Path idFilePath;
    private final FilesService filesService;

    private int nextId;

    public IdService(Path idFilePath, FilesService filesService) {
        this.idFilePath = idFilePath;
        this.filesService = filesService;

        try {
            List<String> lastUsedID = filesService.readAllLines(idFilePath);
            if (lastUsedID.isEmpty()) {
                filesService.writeToFile(idFilePath, "1");
                nextId = 1;
            } else {
                nextId = Integer.parseInt(lastUsedID.get(0));
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize id database", e);
        }
    }

    public void printIDtoTracker() {
        try {
            filesService.writeToFile(idFilePath, String.valueOf(nextId + 1));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to id file", e);
        }
    }

    public int getNextIdAndIncrement() {
        printIDtoTracker();
        return nextId++;
    }
}
