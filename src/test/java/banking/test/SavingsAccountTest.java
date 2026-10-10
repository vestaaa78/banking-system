package banking.test;

import banking.AccountNumber;
import banking.InsufficientFundsException;
import banking.InvalidAmountException;
import banking.SavingsAccount;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SavingsAccountTest {

    private static final AccountNumber VALID_NUMBER = new AccountNumber("1234567890");
    private static final AccountNumber ANOTHER_NUMBER = new AccountNumber("0987654321");

    @Test
    void shouldCreateAccountWithCorrectData() {
        String owner = "Petr";
        double initialBalance = 10000;
        double minimumBalance = 1000;

        SavingsAccount account = new SavingsAccount(VALID_NUMBER, owner, initialBalance, minimumBalance);

        assertEquals(VALID_NUMBER, account.getNumber());
        assertEquals("Petr", account.getOwner());
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldWithdrawSuccessfully() {
        SavingsAccount account = new SavingsAccount(VALID_NUMBER, "Petr", 10000, 1000);

        account.withdraw(8500);

        assertEquals(1500, account.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenWithdrawBelowMinimum() {
        SavingsAccount account = new SavingsAccount(VALID_NUMBER, "Petr", 10000, 1000);

        assertThrows(InsufficientFundsException.class, () -> account.withdraw(9500));

        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldWithdrawExactlyToMinimumBalance() {
        SavingsAccount account = new SavingsAccount(VALID_NUMBER, "Petr", 10000, 1000);

        account.withdraw(9000);

        assertEquals(1000, account.getBalance());
    }

    @Test
    void shouldThrowExceptionOnZeroWithdraw() {
        SavingsAccount account = new SavingsAccount(VALID_NUMBER, "Petr", 10000, 1000);

        assertThrows(InvalidAmountException.class, () -> account.withdraw(0));

        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldThrowExceptionOnNegativeWithdraw() {
        SavingsAccount account = new SavingsAccount(VALID_NUMBER, "Petr", 10000, 1000);

        assertThrows(InvalidAmountException.class, () -> account.withdraw(-100));

        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldDepositPositiveAmount() {
        SavingsAccount account = new SavingsAccount(VALID_NUMBER, "Petr", 10000, 1000);

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

        assertThrows(InvalidAmountException.class, () -> account.deposit(0));

        assertEquals(10000, account.getBalance());
    }

    @Test
    void negativeDepositThrowsException() {
        SavingsAccount account = new SavingsAccount(VALID_NUMBER, "Petr", 10000, 1000);

        assertThrows(InvalidAmountException.class, () -> account.deposit(-500));

        assertEquals(10000, account.getBalance());
    }
}