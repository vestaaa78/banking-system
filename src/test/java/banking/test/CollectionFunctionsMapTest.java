package banking.test;
import banking.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class CollectionFunctionsMapTest {

    @Test
    void mapBankAccountsToSummaries() {
        Function<BankAccount, AccountSummary> toSummary = account -> new AccountSummary(
                account.getId(),
                account.getClass().getSimpleName(),
                account.getBalance()
        );

        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        accounts.add(new SavingsAccount(new AccountNumber("2222222222"), "Bob", 10000, 1000));
        accounts.add(new CreditAccount(new AccountNumber("3333333333"), "Charlie", 3000, 5000));

        List<AccountSummary> summaries = CollectionFunctions.map(accounts, toSummary);

        assertEquals(3, summaries.size());

        AccountSummary first = summaries.get(0);
        assertEquals(new AccountNumber("1111111111"), first.number());
        assertEquals("DebitAccount", first.type());
        assertEquals(5000.0, first.balance());

        AccountSummary second = summaries.get(1);
        assertEquals(new AccountNumber("2222222222"), second.number());
        assertEquals("SavingsAccount", second.type());
        assertEquals(10000.0, second.balance());

        AccountSummary third = summaries.get(2);
        assertEquals(new AccountNumber("3333333333"), third.number());
        assertEquals("CreditAccount", third.type());
        assertEquals(3000.0, third.balance());
    }

    @Test
    void mapBankAccountsToStringLabels() {
        Function<BankAccount, String> label = account ->
                account.getId().value() + " : " + account.getBalance();

        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        accounts.add(new DebitAccount(new AccountNumber("2222222222"), "Bob", 10000));

        List<String> labels = CollectionFunctions.map(accounts, label);

        assertEquals(2, labels.size());
        assertEquals("1111111111 : 5000.0", labels.get(0));
        assertEquals("2222222222 : 10000.0", labels.get(1));
    }

    @Test
    void mapReturnsNewListNotOriginalReference() {
        Function<BankAccount, String> toString = account -> account.getOwner();

        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        List<String> result = CollectionFunctions.map(accounts, toString);

        assertNotSame(accounts, result, "map должен возвращать новый список");

        result.clear();
        assertEquals(1, accounts.size(), "Очистка результата не должна влиять на исходный список");
    }

    @Test
    void mapEmptyListReturnsEmptyList() {
        Function<BankAccount, AccountSummary> toSummary = account -> new AccountSummary(
                account.getId(),
                account.getClass().getSimpleName(),
                account.getBalance()
        );

        List<BankAccount> accounts = new ArrayList<>();

        List<AccountSummary> result = CollectionFunctions.map(accounts, toSummary);

        assertTrue(result.isEmpty());
    }

    @Test
    void mapNullSourceThrowsException() {
        Function<BankAccount, String> mapper = account -> account.getOwner();

        assertThrows(IllegalArgumentException.class,
                () -> CollectionFunctions.map(null, mapper));
    }

    @Test
    void mapNullMapperThrowsException() {
        List<BankAccount> accounts = new ArrayList<>();

        assertThrows(IllegalArgumentException.class,
                () -> CollectionFunctions.map(accounts, null));
    }

    @Test
    void mapPreservesOrderOfElements() {
        Function<BankAccount, Double> getBalance = BankAccount::getBalance;

        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        accounts.add(new DebitAccount(new AccountNumber("2222222222"), "Bob", 10000));
        accounts.add(new DebitAccount(new AccountNumber("3333333333"), "Charlie", 15000));

        List<Double> balances = CollectionFunctions.map(accounts, getBalance);

        assertEquals(5000.0, balances.get(0));
        assertEquals(10000.0, balances.get(1));
        assertEquals(15000.0, balances.get(2));
    }

    @Test
    void mapCanChangeTypeFromBankAccountToInteger() {
        Function<BankAccount, Integer> getBalanceAsInteger = account -> (int) account.getBalance();

        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        accounts.add(new DebitAccount(new AccountNumber("2222222222"), "Bob", 10000));

        List<Integer> result = CollectionFunctions.map(accounts, getBalanceAsInteger);

        assertEquals(2, result.size());
        assertEquals(5000, result.get(0));
        assertEquals(10000, result.get(1));
    }
}