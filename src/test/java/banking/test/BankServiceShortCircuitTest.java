package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceShortCircuitTest {

    private BankService bankService;
    private Repository<AccountNumber, BankAccount> repository;
    private BlockedAccountRegistry blockedAccounts;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>();
        blockedAccounts = new BlockedAccountRegistry();
        bankService = new BankService(repository, null, null);
    }


    @Test
    void hasBlockedAccountsReturnsTrueWhenAtLeastOneBlocked() {
        bankService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        bankService.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 3000));
        blockedAccounts.block(new AccountNumber("2222222222"));

        assertTrue(bankService.hasBlockedAccounts(blockedAccounts));
    }

    @Test
    void hasBlockedAccountsReturnsFalseWhenNoneBlocked() {
        bankService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        bankService.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 3000));

        assertFalse(bankService.hasBlockedAccounts(blockedAccounts));
    }

    @Test
    void hasBlockedAccountsReturnsFalseForEmptyRepository() {
        assertFalse(bankService.hasBlockedAccounts(blockedAccounts));
    }

    @Test
    void allAccountsPositiveReturnsTrueWhenAllPositive() {
        bankService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        bankService.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 3000));

        assertTrue(bankService.allAccountsPositive());
    }

    @Test
    void allAccountsPositiveReturnsFalseWhenOneZero() {
        bankService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        bankService.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 0));

        assertFalse(bankService.allAccountsPositive());
    }

    @Test
    void allAccountsPositiveReturnsFalseWhenOneNegative() {
        bankService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        CreditAccount credit = new CreditAccount(new AccountNumber("2222222222"), "Bob", 1000, 5000);
        credit.withdraw(3000);
        bankService.openAccount(credit);

        assertFalse(bankService.allAccountsPositive());
    }

    @Test
    void allAccountsPositiveReturnsTrueForEmptyRepository() {
        assertTrue(bankService.allAccountsPositive());
    }


    @Test
    void noCreditAccountsReturnsTrueWhenNoCreditAccounts() {
        bankService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        bankService.openAccount(new SavingsAccount(new AccountNumber("2222222222"), "Bob", 3000, 1000));

        assertTrue(bankService.noCreditAccounts());
    }

    @Test
    void noCreditAccountsReturnsFalseWhenAtLeastOneCredit() {
        bankService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        bankService.openAccount(new CreditAccount(new AccountNumber("2222222222"), "Bob", 3000, 10000));

        assertFalse(bankService.noCreditAccounts());
    }

    @Test
    void noCreditAccountsReturnsTrueForEmptyRepository() {
        assertTrue(bankService.noCreditAccounts());
    }
}