package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceSortedAccountsTest {

    private BankService bankService;
    private Repository<AccountNumber, BankAccount> repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>();
        bankService = new BankService(repository, null);

        bankService.openAccount(new DebitAccount(new AccountNumber("3333333333"), "Charlie", 5000));
        bankService.openAccount(new SavingsAccount(new AccountNumber("1111111111"), "Alice", 150000, 10000));
        bankService.openAccount(new CreditAccount(new AccountNumber("5555555555"), "Eve", 2000, 50000));
        bankService.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 100000));
        bankService.openAccount(new SavingsAccount(new AccountNumber("4444444444"), "Dave", 50000, 5000));
    }

    @Test
    void sortByBalanceAscending() {
        Comparator<BankAccount> byBalance = Comparator.comparingDouble(BankAccount::getBalance);

        List<BankAccount> result = bankService.sortedAccounts(byBalance);

        assertEquals(5, result.size());
        assertEquals(2000.0, result.get(0).getBalance(), "Первый — Eve с минимальным балансом");
        assertEquals(5000.0, result.get(1).getBalance());
        assertEquals(50000.0, result.get(2).getBalance());
        assertEquals(100000.0, result.get(3).getBalance());
        assertEquals(150000.0, result.get(4).getBalance(), "Последний — Alice с максимальным балансом");
    }

    @Test
    void sortByBalanceDescending() {
        Comparator<BankAccount> byBalance = Comparator.comparingDouble(BankAccount::getBalance);
        Comparator<BankAccount> byBalanceDesc = byBalance.reversed();

        List<BankAccount> result = bankService.sortedAccounts(byBalanceDesc);

        assertEquals(5, result.size());
        assertEquals(150000.0, result.get(0).getBalance(), "Первый — Alice с максимальным балансом");
        assertEquals(100000.0, result.get(1).getBalance());
        assertEquals(50000.0, result.get(2).getBalance());
        assertEquals(5000.0, result.get(3).getBalance());
        assertEquals(2000.0, result.get(4).getBalance(), "Последний — Eve с минимальным балансом");
    }

    @Test
    void sortByAccountNumber() {
        Comparator<BankAccount> byNumber = Comparator.comparing(a -> a.getId().value());

        List<BankAccount> result = bankService.sortedAccounts(byNumber);

        assertEquals(5, result.size());
        assertEquals("1111111111", result.get(0).getId().value());
        assertEquals("2222222222", result.get(1).getId().value());
        assertEquals("3333333333", result.get(2).getId().value());
        assertEquals("4444444444", result.get(3).getId().value());
        assertEquals("5555555555", result.get(4).getId().value());
    }

    @Test
    void sortByTypeThenBalance() {
        Comparator<BankAccount> byTypeThenBalance = Comparator
                .comparing((BankAccount a) -> a.getClass().getSimpleName())
                .thenComparingDouble(BankAccount::getBalance);

        List<BankAccount> result = bankService.sortedAccounts(byTypeThenBalance);

        assertEquals(5, result.size());

        assertEquals("CreditAccount", result.get(0).getClass().getSimpleName());
        assertEquals("DebitAccount", result.get(1).getClass().getSimpleName());
        assertEquals("DebitAccount", result.get(2).getClass().getSimpleName());
        assertEquals("SavingsAccount", result.get(3).getClass().getSimpleName());
        assertEquals("SavingsAccount", result.get(4).getClass().getSimpleName());

        assertEquals(2000.0, result.get(0).getBalance());
        assertEquals(5000.0, result.get(1).getBalance());
        assertEquals(100000.0, result.get(2).getBalance());
        assertEquals(50000.0, result.get(3).getBalance());
        assertEquals(150000.0, result.get(4).getBalance());
    }

    @Test
    void originalRepositoryNotModifiedAfterSorting() {
        Comparator<BankAccount> byBalance = Comparator.comparingDouble(BankAccount::getBalance);
        int originalSize = bankService.accountCount();

        List<BankAccount> sorted1 = bankService.sortedAccounts(byBalance);
        List<BankAccount> sorted2 = bankService.sortedAccounts(byBalance.reversed());

        assertEquals(originalSize, bankService.accountCount(), "Размер репозитория не должен измениться");

        assertNotSame(sorted1, sorted2, "Каждый вызов должен возвращать новый список");

        sorted1.clear();
        assertEquals(originalSize, bankService.accountCount(), "Очистка результата не должна влиять на репозиторий");
    }

    @Test
    void nullComparatorThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> bankService.sortedAccounts(null));
    }

    @Test
    void sortByBalanceForEmptyRepository() {
        Repository<AccountNumber, BankAccount> emptyRepo = new InMemoryRepository<>();
        BankService emptyBank = new BankService(emptyRepo, null);
        Comparator<BankAccount> byBalance = Comparator.comparingDouble(BankAccount::getBalance);

        List<BankAccount> result = emptyBank.sortedAccounts(byBalance);

        assertTrue(result.isEmpty());
    }
}
