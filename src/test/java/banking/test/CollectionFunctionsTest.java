package banking.test;

import banking.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class CollectionFunctionsTest {

    @Test
    void filterPositiveBalance() {
        Predicate<BankAccount> positiveBalance = account -> account.getBalance() > 0;

        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        accounts.add(new DebitAccount(new AccountNumber("2222222222"), "Bob", 0));
        accounts.add(new CreditAccount(new AccountNumber("3333333333"), "Charlie", 1000, 5000));
        accounts.add(new DebitAccount(new AccountNumber("4444444444"), "Dave", 100));

        List<BankAccount> result = CollectionFunctions.filter(accounts, positiveBalance);

        assertEquals(3, result.size(), "Должны быть отфильтрованы счета с балансом > 0");
        assertTrue(result.stream().allMatch(acc -> acc.getBalance() > 0));
    }

    @Test
    void filterRichAccounts() {
        Predicate<BankAccount> rich = account -> account.getBalance() >= 100_000;

        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        accounts.add(new DebitAccount(new AccountNumber("2222222222"), "Bob", 150000));
        accounts.add(new DebitAccount(new AccountNumber("3333333333"), "Charlie", 100000));
        accounts.add(new DebitAccount(new AccountNumber("4444444444"), "Dave", 99999));

        List<BankAccount> result = CollectionFunctions.filter(accounts, rich);

        assertEquals(2, result.size());
        assertEquals("Bob", result.get(0).getOwner());
        assertEquals("Charlie", result.get(1).getOwner());
    }

    @Test
    void filterDebitAccountsOnly() {
        Predicate<BankAccount> debitOnly = DebitAccount.class::isInstance;

        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        accounts.add(new SavingsAccount(new AccountNumber("2222222222"), "Bob", 10000, 1000));
        accounts.add(new DebitAccount(new AccountNumber("3333333333"), "Charlie", 2000));
        accounts.add(new CreditAccount(new AccountNumber("4444444444"), "Dave", 3000, 5000));

        List<BankAccount> result = CollectionFunctions.filter(accounts, debitOnly);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(acc -> acc instanceof DebitAccount));
    }

    @Test
    void filterReturnsEmptyListForNoMatches() {
        Predicate<BankAccount> rich = account -> account.getBalance() >= 100_000;

        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));

        List<BankAccount> result = CollectionFunctions.filter(accounts, rich);

        assertTrue(result.isEmpty());
    }

    @Test
    void filterReturnsNewListNotOriginalReference() {
        Predicate<BankAccount> positiveBalance = account -> account.getBalance() > 0;

        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));

        List<BankAccount> result = CollectionFunctions.filter(accounts, positiveBalance);

        assertNotSame(accounts, result, "filter должен возвращать новый список");

        result.clear();
        assertEquals(1, accounts.size(), "Очистка результата не должна влиять на исходный список");
    }

    @Test
    void filterNullSourceThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> CollectionFunctions.filter(null, account -> true));
    }

    @Test
    void filterNullPredicateThrowsException() {
        List<BankAccount> accounts = new ArrayList<>();
        assertThrows(IllegalArgumentException.class,
                () -> CollectionFunctions.filter(accounts, null));
    }
}