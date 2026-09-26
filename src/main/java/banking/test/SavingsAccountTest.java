package banking.test;

import banking.AccountNumber;
import banking.SavingsAccount;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {
    private static final AccountNumber VALID_NUMBER = new AccountNumber("1234567890");
    private static final AccountNumber ANOTHER_NUMBER = new AccountNumber("0987654321");
    @Test
    void shouldCreateAccountWithCorrectData() {
        String number = "789012";
        String owner = "Petr";
        double initialBalance = 10000;
        double minimumBalance = 1000;

        SavingsAccount account = new SavingsAccount(ANOTHER_NUMBER, owner, initialBalance, minimumBalance);

        assertEquals("789012", account.getNumber());
        assertEquals("Petr", account.getOwner());
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldWithdrawSuccessfully() {
        SavingsAccount account = new SavingsAccount(ANOTHER_NUMBER, "Petr", 10000, 1000);
        boolean result = account.withdraw(8500);
        assertTrue(result);
        assertEquals(1500, account.getBalance());
    }

    @Test
    void shouldNotWithdrawBelowMinimumBalance() {
        SavingsAccount account = new SavingsAccount(ANOTHER_NUMBER, "Petr", 10000, 1000);
        boolean result = account.withdraw(9500);
        assertFalse(result);
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldWithdrawExactlyToMinimumBalance() {
        SavingsAccount account = new SavingsAccount(ANOTHER_NUMBER, "Petr", 10000, 1000);
        boolean result = account.withdraw(9000);
        assertTrue(result);
        assertEquals(1000, account.getBalance());
    }

    @Test
    void shouldNotWithdrawZeroAmount() {
        SavingsAccount account = new SavingsAccount(ANOTHER_NUMBER, "Petr", 10000, 1000);
        boolean result = account.withdraw(0);
        assertFalse(result);
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldNotWithdrawNegativeAmount() {
        SavingsAccount account = new SavingsAccount(ANOTHER_NUMBER, "Petr", 10000, 1000);
        boolean result = account.withdraw(-100);
        assertFalse(result);
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldDepositPositiveAmount() {
        SavingsAccount account = new SavingsAccount(ANOTHER_NUMBER, "Petr", 10000, 1000);
        account.deposit(5000);
        assertEquals(15000, account.getBalance());
    }

    @Test
    void shouldNotCreateAccountWithNegativeBalance() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SavingsAccount(VALID_NUMBER, "Test", -100, 1000);
        });
    }

    @Test
    void shouldNotCreateAccountWithNegativeMinimumBalance() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SavingsAccount(VALID_NUMBER, "Test", 1000, -100);
        });
    }

    @Test
    void zeroDepositThrowsException() {
        SavingsAccount account = new SavingsAccount(VALID_NUMBER, "Petr", 10000, 1000);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(0)
        );

        assertEquals("Amount must be positive", exception.getMessage());
        assertEquals(10000, account.getBalance());
    }

    @Test
    void negativeDepositThrowsException() {
        SavingsAccount account = new SavingsAccount(VALID_NUMBER, "Petr", 10000, 1000);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> account.deposit(-500)
        );

        assertEquals("Amount must be positive", exception.getMessage());
        assertEquals(10000, account.getBalance());
    }
}