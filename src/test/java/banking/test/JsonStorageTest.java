
package banking.test;

import banking.dto.AccountDto;
import banking.export.JsonStorage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonStorageTest {

    @TempDir
    Path tempDir;

    private final JsonStorage storage = new JsonStorage();

    @Test
    void shouldRestoreAccountsAfterRoundTrip() throws IOException {

        List<AccountDto> source = List.of(
                new AccountDto(
                        "1111111111",
                        "Ivan Ivanov",
                        15000.0,
                        "DebitAccount"
                ),
                new AccountDto(
                        "2222222222",
                        "Petr Petrov",
                        25000.0,
                        "CreditAccount"
                ),
                new AccountDto(
                        "3333333333",
                        "Anna Smirnova",
                        5000.0,
                        "DebitAccount"
                )
        );

        Path path = tempDir.resolve("accounts.json");

        storage.saveAccounts(path, source);

        List<AccountDto> restored = storage.loadAccounts(path);

        assertEquals(source, restored);
    }

    @Test
    void shouldSaveAndLoadEmptyList() throws IOException {

        List<AccountDto> source = List.of();

        Path path = tempDir.resolve("empty-accounts.json");

        storage.saveAccounts(path, source);

        List<AccountDto> restored = storage.loadAccounts(path);

        assertEquals(source, restored);
        assertTrue(restored.isEmpty());
    }

    @Test
    void shouldThrowExceptionForInvalidJson() throws IOException {

        Path path = tempDir.resolve("invalid.json");

        Files.writeString(path, "{ invalid json content ");

        assertThrows(
                IOException.class,
                () -> storage.loadAccounts(path)
        );
    }

    @Test
    void shouldThrowExceptionWhenFileDoesNotExist() {

        Path path = tempDir.resolve("missing.json");

        assertFalse(Files.exists(path));

        assertThrows(
                IOException.class,
                () -> storage.loadAccounts(path)
        );
    }
}
