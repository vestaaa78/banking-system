package banking.test;

import banking.SavingsAccount;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {

    @Test
    void shouldCreateAccountWithCorrectData() {
        String number = "789012";
        String owner = "Petr";
        double initialBalance = 10000;
        double minimumBalance = 1000;

        SavingsAccount account = new SavingsAccount(number, owner, initialBalance, minimumBalance);

        assertEquals("789012", account.getNumber());
        assertEquals("Petr", account.getOwner());
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldWithdrawSuccessfully() {
        SavingsAccount account = new SavingsAccount("789012", "Petr", 10000, 1000);
        boolean result = account.withdraw(8500);
        assertTrue(result);
        assertEquals(1500, account.getBalance());
    }

    @Test
    void shouldNotWithdrawBelowMinimumBalance() {
        SavingsAccount account = new SavingsAccount("789012", "Petr", 10000, 1000);
        boolean result = account.withdraw(9500);
        assertFalse(result);
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldWithdrawExactlyToMinimumBalance() {
        SavingsAccount account = new SavingsAccount("789012", "Petr", 10000, 1000);
        boolean result = account.withdraw(9000);
        assertTrue(result);
        assertEquals(1000, account.getBalance());
    }

    @Test
    void shouldNotWithdrawZeroAmount() {
        SavingsAccount account = new SavingsAccount("789012", "Petr", 10000, 1000);
        boolean result = account.withdraw(0);
        assertFalse(result);
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldNotWithdrawNegativeAmount() {
        SavingsAccount account = new SavingsAccount("789012", "Petr", 10000, 1000);
        boolean result = account.withdraw(-100);
        assertFalse(result);
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldDepositPositiveAmount() {
        SavingsAccount account = new SavingsAccount("789012", "Petr", 10000, 1000);
        account.deposit(5000);
        assertEquals(15000, account.getBalance());
    }

    @Test
    void shouldNotCreateAccountWithNegativeBalance() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SavingsAccount("111", "Test", -100, 1000);
        });
    }

    @Test
    void shouldNotCreateAccountWithNegativeMinimumBalance() {
        assertThrows(IllegalArgumentException.class, () -> {
            new SavingsAccount("111", "Test", 1000, -100);
        });
    }
}