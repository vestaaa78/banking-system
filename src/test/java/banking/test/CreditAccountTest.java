package banking.test;

import banking.AccountNumber;
import banking.CreditAccount;
import banking.InsufficientFundsException;
import banking.InvalidAmountException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreditAccountTest {

    private static final AccountNumber VALID_NUMBER = new AccountNumber("1234567890");
    private static final AccountNumber ANOTHER_NUMBER = new AccountNumber("0987654321");

    @Test
    void shouldCreateAccountWithCorrectData() {
        String owner = "Alex";
        double initialBalance = 1000;
        double creditLimit = 5000;

        CreditAccount account = new CreditAccount(VALID_NUMBER, owner, initialBalance, creditLimit);

        assertEquals(VALID_NUMBER, account.getNumber());
        assertEquals("Alex", account.getOwner());
        assertEquals(1000, account.getBalance());
        assertEquals(5000, account.getCreditLimit());
    }

    @Test
    void shouldWithdrawWithinCreditLimit() {
        CreditAccount account = new CreditAccount(VALID_NUMBER, "Alex", 1000, 5000);

        account.withdraw(4000);

        assertEquals(-3000, account.getBalance());
    }

    @Test
    void shouldWithdrawExactlyAtCreditLimit() {
        CreditAccount account = new CreditAccount(VALID_NUMBER, "Alex", 1000, 5000);

        account.withdraw(6000);

        assertEquals(-5000, account.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenExceedingCreditLimit() {
        CreditAccount account = new CreditAccount(VALID_NUMBER, "Alex", 1000, 5000);
        account.withdraw(4000); // balance = -3000

        assertThrows(InsufficientFundsException.class, () -> {
            account.withdraw(3000); // -3000 - 3000 = -6000 < -5000
        });

        assertEquals(-3000, account.getBalance());
    }

    @Test
    void shouldThrowExceptionOnZeroWithdraw() {
        CreditAccount account = new CreditAccount(VALID_NUMBER, "Alex", 1000, 5000);

        assertThrows(InvalidAmountException.class, () -> {
            account.withdraw(0);
        });

        assertEquals(1000, account.getBalance());
    }

    @Test
    void shouldThrowExceptionOnNegativeWithdraw() {
        CreditAccount account = new CreditAccount(VALID_NUMBER, "Alex", 1000, 5000);

        assertThrows(InvalidAmountException.class, () -> {
            account.withdraw(-100);
        });

        assertEquals(1000, account.getBalance());
    }

    @Test
    void shouldDepositToNegativeBalance() {
        CreditAccount account = new CreditAccount(VALID_NUMBER, "Alex", 1000, 5000);
        account.withdraw(3000); // balance = -2000

        account.deposit(3000);

        assertEquals(1000, account.getBalance());
    }

    @Test
    void shouldNotCreateAccountWithZeroCreditLimit() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CreditAccount(VALID_NUMBER, "Test", 1000, 0);
        });
    }

    @Test
    void shouldNotCreateAccountWithNegativeCreditLimit() {
        assertThrows(IllegalArgumentException.class, () -> {
            new CreditAccount(VALID_NUMBER, "Test", 1000, -100);
        });
    }

    @Test
    void shouldDepositPositiveAmount() {
        CreditAccount account = new CreditAccount(VALID_NUMBER, "Alex", 1000, 5000);

        account.deposit(5000);
        assertEquals(6000, account.getBalance());
    }

    @Test
    void zeroDepositThrowsException() {
        CreditAccount account = new CreditAccount(VALID_NUMBER, "Alex", 1000, 5000);

        assertThrows(InvalidAmountException.class, () -> {
            account.deposit(0);
        });

        assertEquals(1000, account.getBalance());
    }

    @Test
    void negativeDepositThrowsException() {
        CreditAccount account = new CreditAccount(VALID_NUMBER, "Alex", 1000, 5000);

        assertThrows(InvalidAmountException.class, () -> {
            account.deposit(-100);
        });

        assertEquals(1000, account.getBalance());
    }
}