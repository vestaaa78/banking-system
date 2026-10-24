package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceFindAccountsTest {

    private BankService bankService;
    private Repository<AccountNumber, BankAccount> repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>();
        bankService = new BankService(repository, null);
    }

    @Test
    void findAccountsByPositiveBalance() {
        Predicate<BankAccount> positiveBalance = account -> account.getBalance() > 0;

        bankService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        bankService.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 0));
        bankService.openAccount(new DebitAccount(new AccountNumber("3333333333"), "Charlie", 100));

        List<BankAccount> result = bankService.findAccounts(positiveBalance);

        assertEquals(2, result.size(), "Alice (5000) и Charlie (100) имеют положительный баланс");
        assertTrue(result.stream().allMatch(acc -> acc.getBalance() > 0));
    }

    @Test
    void findAccountsByType() {
        Predicate<BankAccount> debitOnly = DebitAccount.class::isInstance;

        bankService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        bankService.openAccount(new SavingsAccount(new AccountNumber("2222222222"), "Bob", 10000, 1000));
        bankService.openAccount(new DebitAccount(new AccountNumber("3333333333"), "Charlie", 2000));

        List<BankAccount> result = bankService.findAccounts(debitOnly);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(acc -> acc instanceof DebitAccount));
    }

    @Test
    void findAccountsReturnsNewList() {
        Predicate<BankAccount> any = account -> true;
        bankService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));

        List<BankAccount> result1 = bankService.findAccounts(any);
        List<BankAccount> result2 = bankService.findAccounts(any);

        assertNotSame(result1, result2, "Каждый вызов должен возвращать новый список");

        result1.clear();
        assertEquals(1, bankService.accountCount(), "Очистка результата не должна влиять на репозиторий");
    }

    @Test
    void findAccountsWithNullPredicateThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> bankService.findAccounts(null));
    }
}