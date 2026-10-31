package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceOptionalTest {

    private BankService bankService;
    private Repository<AccountNumber, BankAccount> repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>();
        bankService = new BankService(repository, null, null);

        bankService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        bankService.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 150000));
        bankService.openAccount(new SavingsAccount(new AccountNumber("3333333333"), "Charlie", 200000, 10000));
        bankService.openAccount(new CreditAccount(new AccountNumber("4444444444"), "Dave", 3000, 50000));
    }


    @Test
    void findFirstRichAccountReturnsFirstMatch() {
        Optional<BankAccount> result = bankService.findFirstRichAccount(100_000);

        assertTrue(result.isPresent(), "Должен найти хотя бы один богатый счёт");
        assertEquals("2222222222", result.get().getId().value());
        assertEquals("Bob", result.get().getOwner());
        assertEquals(150000.0, result.get().getBalance());
    }

    @Test
    void findFirstRichAccountReturnsEmptyWhenNoMatch() {
        Optional<BankAccount> result = bankService.findFirstRichAccount(1_000_000);

        assertFalse(result.isPresent(), "Не должно быть счетов с балансом >= 1000000");
        assertTrue(result.isEmpty());
    }

    @Test
    void findFirstRichAccountReturnsEmptyForEmptyRepository() {
        Repository<AccountNumber, BankAccount> emptyRepo = new InMemoryRepository<>();
        BankService emptyService = new BankService(emptyRepo, null, null);

        Optional<BankAccount> result = emptyService.findFirstRichAccount(100_000);

        assertTrue(result.isEmpty());
    }

    @Test
    void findFirstRichAccountStopsAtFirstMatch() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        BankService service = new BankService(repo, null, null);
        service.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Rich", 200000)); // ← первый!
        service.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Poor", 1000));

        Optional<BankAccount> result = service.findFirstRichAccount(100_000);

        assertTrue(result.isPresent());
        assertEquals("1111111111", result.get().getId().value());
        assertEquals("Rich", result.get().getOwner());
    }

    @Test
    void orElseThrowReturnsAccountWhenPresent() {
        BankAccount account = bankService.findFirstRichAccount(100_000)
                .orElseThrow(() -> new IllegalStateException("No rich account"));

        assertNotNull(account);
        assertEquals("Bob", account.getOwner());
    }

    @Test
    void orElseThrowThrowsExceptionWhenEmpty() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bankService.findFirstRichAccount(1_000_000)
                        .orElseThrow(() -> new IllegalStateException("No rich account"))
        );

        assertEquals("No rich account", exception.getMessage());
    }

    @Test
    void mapOrElseReturnsTransformedValueWhenPresent() {
        String label = bankService.findFirstRichAccount(100_000)
                .map(a -> a.getId().value())
                .orElse("not found");

        assertEquals("2222222222", label);
    }

    @Test
    void mapOrElseReturnsDefaultWhenEmpty() {
        String label = bankService.findFirstRichAccount(1_000_000)
                .map(a -> a.getId().value())
                .orElse("not found");

        assertEquals("not found", label);
    }

    @Test
    void mapChainWithMultipleTransformations() {
        String description = bankService.findFirstRichAccount(100_000)
                .map(a -> a.getOwner() + " (" + a.getClass().getSimpleName() + ")")
                .map(String::toUpperCase)
                .orElse("NO ACCOUNT");

        assertEquals("BOB (DEBITACCOUNT)", description);
    }

    @Test
    void mapChainReturnsDefaultWhenAnyStepReturnsEmpty() {
        String description = bankService.findFirstRichAccount(1_000_000)
                .map(a -> a.getOwner())
                .map(String::toUpperCase)
                .orElse("NO ACCOUNT");

        assertEquals("NO ACCOUNT", description);
    }


    @Test
    void ifPresentExecutesActionWhenValuePresent() {
        StringBuilder log = new StringBuilder();

        bankService.findFirstRichAccount(100_000)
                .ifPresent(account -> log.append("Found: ").append(account.getOwner()));

        assertEquals("Found: Bob", log.toString());
    }

    @Test
    void ifPresentDoesNothingWhenEmpty() {
        StringBuilder log = new StringBuilder();

        bankService.findFirstRichAccount(1_000_000)
                .ifPresent(account -> log.append("Found: ").append(account.getOwner()));

        assertTrue(log.toString().isEmpty(), "Действие не должно выполниться для пустого Optional");
    }


    @Test
    void orElseAlwaysCreatesDefaultValue() {
        int[] counter = {0};

        BankAccount account = bankService.findFirstRichAccount(100_000)
                .orElse(createDefaultAccount(counter));

        assertNotNull(account);
        assertEquals(1, counter[0], "orElse вычислил дефолтное значение, хотя Optional не пуст");
    }

    @Test
    void orElseGetCreatesDefaultValueOnlyWhenEmpty() {
        int[] counter = {0};

        BankAccount account = bankService.findFirstRichAccount(100_000)
                .orElseGet(() -> createDefaultAccount(counter));

        assertNotNull(account);
        assertEquals(0, counter[0], "orElseGet НЕ вычислил дефолтное значение, так как Optional не пуст");
    }

    private BankAccount createDefaultAccount(int[] counter) {
        counter[0]++;
        return new DebitAccount(new AccountNumber("0000000000"), "Default", 0);
    }
}