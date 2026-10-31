package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceStreamFilterTest {

    private BankService bankService;
    private Repository<AccountNumber, BankAccount> repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>();
        bankService = new BankService(repository, null, null);

        bankService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 150000));
        bankService.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 50000));
        bankService.openAccount(new SavingsAccount(new AccountNumber("3333333333"), "Alice", 200000, 10000));
        bankService.openAccount(new CreditAccount(new AccountNumber("4444444444"), "Charlie", 5000, 50000));
        bankService.openAccount(new DebitAccount(new AccountNumber("5555555555"), "Eve", 300000));
    }

    @Test
    void findRichAccountsReturnsAccountsWithBalanceAboveThreshold() {
        List<BankAccount> result = bankService.findRichAccounts(100_000);

        assertEquals(3, result.size());
        for (BankAccount acc : result) {
            assertTrue(acc.getBalance() >= 100_000,
                    "Все счета должны иметь баланс >= 100000, но у " + acc.getOwner() + " баланс " + acc.getBalance());
        }
    }

    @Test
    void findRichAccountsPreservesOrder() {
        List<BankAccount> result = bankService.findRichAccounts(100_000);

        assertEquals("1111111111", result.get(0).getId().value(), "Первый — Alice Debit");
        assertEquals("3333333333", result.get(1).getId().value(), "Второй — Alice Savings");
        assertEquals("5555555555", result.get(2).getId().value(), "Третий — Eve");
    }

    @Test
    void findRichAccountsReturnsEmptyListForHighThreshold() {
        List<BankAccount> result = bankService.findRichAccounts(1_000_000);

        assertTrue(result.isEmpty());
    }

    @Test
    void findRichAccountsReturnsAllForZeroThreshold() {
        List<BankAccount> result = bankService.findRichAccounts(0);

        assertEquals(5, result.size());
    }

    @Test
    void findDebitAccountsReturnsOnlyDebitAccounts() {
        List<BankAccount> result = bankService.findDebitAccounts();

        assertEquals(3, result.size());
        for (BankAccount acc : result) {
            assertTrue(acc instanceof DebitAccount,
                    "Все счета должны быть DebitAccount, но найден " + acc.getClass().getSimpleName());
        }
    }

    @Test
    void findDebitAccountsPreservesOrder() {
        List<BankAccount> result = bankService.findDebitAccounts();

        assertEquals("1111111111", result.get(0).getId().value());
        assertEquals("2222222222", result.get(1).getId().value());
        assertEquals("5555555555", result.get(2).getId().value());
    }

    @Test
    void findDebitAccountsReturnsEmptyListWhenNoDebitAccounts() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        BankService service = new BankService(repo, null, null);
        service.openAccount(new SavingsAccount(new AccountNumber("1111111111"), "Alice", 5000, 1000));
        service.openAccount(new CreditAccount(new AccountNumber("2222222222"), "Bob", 3000, 10000));

        List<BankAccount> result = service.findDebitAccounts();

        assertTrue(result.isEmpty());
    }


    @Test
    void findAccountsByOwnerReturnsAccountsOfSpecificOwner() {
        List<BankAccount> result = bankService.findAccountsByOwner("Alice");

        assertEquals(2, result.size());
        for (BankAccount acc : result) {
            assertEquals("Alice", acc.getOwner(), "Все счета должны принадлежать Alice");
        }
    }

    @Test
    void findAccountsByOwnerReturnsSingleAccount() {
        List<BankAccount> result = bankService.findAccountsByOwner("Bob");

        assertEquals(1, result.size());
        assertEquals("Bob", result.get(0).getOwner());
        assertEquals("2222222222", result.get(0).getId().value());
    }

    @Test
    void findAccountsByOwnerReturnsEmptyListForUnknownOwner() {
        List<BankAccount> result = bankService.findAccountsByOwner("NonExistent");

        assertTrue(result.isEmpty());
    }

    @Test
    void findAccountsByOwnerIsCaseSensitive() {
        List<BankAccount> result = bankService.findAccountsByOwner("alice");

        assertTrue(result.isEmpty(), "Поиск должен быть чувствителен к регистру");
    }

    @Test
    void originalRepositoryNotModifiedAfterFiltering() {
        int originalSize = bankService.accountCount();

        bankService.findRichAccounts(100_000);
        bankService.findDebitAccounts();
        bankService.findAccountsByOwner("Alice");

        assertEquals(originalSize, bankService.accountCount(),
                "Фильтрация не должна изменять исходный репозиторий");
    }

    @Test
    void emptyRepositoryReturnsEmptyList() {
        Repository<AccountNumber, BankAccount> emptyRepo = new InMemoryRepository<>();
        BankService emptyService = new BankService(emptyRepo, null, null);

        assertTrue(emptyService.findRichAccounts(100_000).isEmpty());
        assertTrue(emptyService.findDebitAccounts().isEmpty());
        assertTrue(emptyService.findAccountsByOwner("Alice").isEmpty());
    }

    @Test
    void filterMethodsReturnNewListInstances() {
        List<BankAccount> result1 = bankService.findDebitAccounts();
        List<BankAccount> result2 = bankService.findDebitAccounts();

        assertNotSame(result1, result2, "Каждый вызов должен возвращать новый список");

        assertEquals(result1.size(), result2.size(), "Размеры должны совпадать");
        assertEquals(result1, result2, "Содержимое должно быть одинаковым");
    }
}