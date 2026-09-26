package banking.test;

import banking.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BankAccountEqualityTest {
    private static final AccountNumber VALID_NUMBER = new AccountNumber("1234567890");
    private static final AccountNumber ANOTHER_NUMBER = new AccountNumber("0987654321");
    @Test
    void accountsWithSameNumberAreEqual() {
        AccountNumber accNum = new AccountNumber("1234567890");

        BankAccount debit = new DebitAccount(accNum, "Ivan", 10000.0);
        BankAccount savings = new SavingsAccount(accNum, "Petr", 5000.0, 1000.0);
        BankAccount credit = new CreditAccount(accNum, "Alex", 0.0, 5000.0);

        assertEquals(debit, savings);
        assertEquals(savings, credit);
        assertEquals(debit, credit);
    }

    @Test
    void accountsWithDifferentNumbersAreNotEqual() {
        BankAccount account1 = new DebitAccount(new AccountNumber("1234567890"), "Ivan", 10000.0);
        BankAccount account2 = new DebitAccount(new AccountNumber("0987654321"), "Ivan", 10000.0);

        assertNotEquals(account1, account2);
    }

    @Test
    void accountEqualsItself() {
        AccountNumber accNum = new AccountNumber("1234567890");
        BankAccount account = new DebitAccount(accNum, "Ivan", 10000.0);
        assertEquals(account, account);
    }

    @Test
    void accountDoesNotEqualNull() {
        AccountNumber accNum = new AccountNumber("1234567890");
        BankAccount account = new DebitAccount(accNum, "Ivan", 10000.0);
        assertNotEquals(account, null);
    }

    @Test
    void equalAccountsHaveSameHashCode() {
        AccountNumber accNum = new AccountNumber("1234567890");
        BankAccount debit = new DebitAccount(accNum, "Ivan", 10000.0);
        BankAccount savings = new SavingsAccount(accNum, "Petr", 5000.0, 1000.0);

        assertEquals(debit.hashCode(), savings.hashCode());
    }

    @Test
    void accountNumberMustBeExactly10Digits() {
        assertThrows(IllegalArgumentException.class, () -> new AccountNumber(null));

        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("12345"));

        assertThrows(IllegalArgumentException.class, () -> new AccountNumber("123456789a"));

        AccountNumber valid = new AccountNumber("0000000000");
        assertEquals("0000000000", valid.value());
    }

    @Test
    void negativeDepositThrowsException() {
        BankAccount account = new DebitAccount(VALID_NUMBER, "Ivan", 10000);

        assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(-100)
        );
    }

    @Test
    void zeroDepositThrowsException() {
        BankAccount account = new DebitAccount(VALID_NUMBER, "Ivan", 10000);

        assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(0)
        );
    }
}