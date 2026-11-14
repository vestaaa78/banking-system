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

    @Test
    void shouldSaveAccountsToJsonFile() throws IOException {

        List<AccountDto> accounts = List.of(
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
                )
        );

        Path file = tempDir.resolve("accounts.json");

        JsonStorage storage = new JsonStorage();

        storage.saveAccounts(file, accounts);

        assertTrue(Files.exists(file));

        assertTrue(Files.size(file) > 0);

        String content = Files.readString(file);

        assertTrue(content.contains("1111111111"));
        assertTrue(content.contains("Ivan Ivanov"));
        assertTrue(content.contains("2222222222"));
        assertTrue(content.contains("Petr Petrov"));
    }
    @Test
    void shouldLoadAccountsFromJsonFile() throws IOException {

        List<AccountDto> accounts = List.of(
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
                )
        );

        Path file = tempDir.resolve("accounts.json");

        JsonStorage storage = new JsonStorage();
        storage.saveAccounts(file, accounts);

        List<AccountDto> loadedAccounts = storage.loadAccounts(file);

        assertEquals(2, loadedAccounts.size());

        assertEquals("1111111111", loadedAccounts.get(0).number());
        assertEquals("Ivan Ivanov", loadedAccounts.get(0).owner());
        assertEquals(15000.0, loadedAccounts.get(0).balance());
        assertEquals("DebitAccount", loadedAccounts.get(0).type());

        assertEquals("2222222222", loadedAccounts.get(1).number());
        assertEquals("Petr Petrov", loadedAccounts.get(1).owner());
        assertEquals(25000.0, loadedAccounts.get(1).balance());
        assertEquals("CreditAccount", loadedAccounts.get(1).type());
    }
}

