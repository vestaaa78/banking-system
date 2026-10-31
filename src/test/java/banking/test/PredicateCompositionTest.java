package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class PredicateCompositionTest {

    private BankService bankService;
    private BlockedAccountRegistry blockedAccounts;

    @BeforeEach
    void setUp() {
        Repository<AccountNumber, BankAccount> repository = new InMemoryRepository<>();
        blockedAccounts = new BlockedAccountRegistry();
        TransferService transferService = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService(),
                blockedAccounts,
                new TransactionJournal()
        );
        bankService = new BankService(repository, transferService, null);

        bankService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 150000));
        bankService.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 50000));
        bankService.openAccount(new SavingsAccount(new AccountNumber("3333333333"), "Charlie", 200000, 10000));
        bankService.openAccount(new CreditAccount(new AccountNumber("4444444444"), "Dave", 5000, 50000));
        bankService.openAccount(new DebitAccount(new AccountNumber("5555555555"), "Eve", 300000));

        blockedAccounts.block(new AccountNumber("2222222222"));
    }

    @Test
    void accountsWithBalanceNotLessThanThreshold() {
        Predicate<BankAccount> rich = account -> account.getBalance() >= 100_000;

        List<BankAccount> result = bankService.findAccounts(rich);

        assertEquals(3, result.size());

        for (BankAccount acc : result) {
            assertTrue(acc.getBalance() >= 100_000, "Все счета должны иметь баланс >= 100000");
        }

        boolean hasAlice = false;
        boolean hasCharlie = false;
        boolean hasEve = false;
        for (BankAccount acc : result) {
            if (acc.getOwner().equals("Alice")) hasAlice = true;
            if (acc.getOwner().equals("Charlie")) hasCharlie = true;
            if (acc.getOwner().equals("Eve")) hasEve = true;
        }
        assertTrue(hasAlice, "Должен быть Alice");
        assertTrue(hasCharlie, "Должен быть Charlie");
        assertTrue(hasEve, "Должен быть Eve");
    }

    @Test
    void unblockedAccounts() {
        Predicate<BankAccount> notBlocked = account -> !blockedAccounts.isBlocked(account.getId());

        List<BankAccount> result = bankService.findAccounts(notBlocked);

        assertEquals(4, result.size());

        for (BankAccount acc : result) {
            assertFalse(acc.getOwner().equals("Bob"), "Bob не должен быть в списке");
        }
    }

    @Test
    void debitAccountsOnly() {
        Predicate<BankAccount> debit = DebitAccount.class::isInstance;

        List<BankAccount> result = bankService.findAccounts(debit);

        assertEquals(3, result.size());

        for (BankAccount acc : result) {
            assertTrue(acc instanceof DebitAccount, "Все счета должны быть DebitAccount");
        }
    }

    @Test
    void richAndUnblockedAccounts() {
        Predicate<BankAccount> rich = account -> account.getBalance() >= 100_000;
        Predicate<BankAccount> notBlocked = account -> !blockedAccounts.isBlocked(account.getId());
        Predicate<BankAccount> richAndActive = rich.and(notBlocked);

        List<BankAccount> result = bankService.findAccounts(richAndActive);

        assertEquals(3, result.size());

        for (BankAccount acc : result) {
            assertTrue(acc.getBalance() >= 100_000, "Баланс должен быть >= 100000");
            assertFalse(blockedAccounts.isBlocked(acc.getId()), "Счёт не должен быть заблокирован");
        }
    }

    @Test
    void nonDebitAccounts() {
        Predicate<BankAccount> debit = DebitAccount.class::isInstance;
        Predicate<BankAccount> notDebit = debit.negate();

        List<BankAccount> result = bankService.findAccounts(notDebit);

        assertEquals(2, result.size());

        boolean hasSavings = false;
        boolean hasCredit = false;
        for (BankAccount acc : result) {
            assertFalse(acc instanceof DebitAccount, "Не должно быть DebitAccount");
            if (acc instanceof SavingsAccount) hasSavings = true;
            if (acc instanceof CreditAccount) hasCredit = true;
        }
        assertTrue(hasSavings, "Должен быть SavingsAccount");
        assertTrue(hasCredit, "Должен быть CreditAccount");
    }

    @Test
    void richOrCreditAccounts() {
        Predicate<BankAccount> rich = account -> account.getBalance() >= 100_000;
        Predicate<BankAccount> credit = CreditAccount.class::isInstance;
        Predicate<BankAccount> richOrCredit = rich.or(credit);

        List<BankAccount> result = bankService.findAccounts(richOrCredit);

        assertEquals(4, result.size());

        for (BankAccount acc : result) {
            boolean isRich = acc.getBalance() >= 100_000;
            boolean isCredit = acc instanceof CreditAccount;
            assertTrue(isRich || isCredit, "Счёт должен быть богатым ИЛИ кредитным");
        }
    }

}