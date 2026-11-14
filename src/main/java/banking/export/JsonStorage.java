
package banking.export;

import banking.dto.AccountDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public class JsonStorage {

    private final ObjectMapper mapper = new ObjectMapper();

    public void saveAccounts(
            Path path,
            List<AccountDto> accounts
    ) throws IOException {

        Objects.requireNonNull(path, "Path cannot be null");
        Objects.requireNonNull(accounts, "Accounts cannot be null");

        Path parent = path.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(
                        path.toFile(),
                        accounts
                );
    }
}
