package banking.test;

import banking.AccountNumber;
import banking.DebitAccount;
import banking.InsufficientFundsException;
import banking.InvalidAmountException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DebitAccountTest {
    private static final AccountNumber VALID_NUMBER = new AccountNumber("1234567890");
    private static final AccountNumber ANOTHER_NUMBER = new AccountNumber("0987654321");

    @Test
    void shouldCreateAccountWithCorrectData() {
        String number = "123456";
        String owner = "Ivan";
        double initialBalance = 10000;

        DebitAccount account = new DebitAccount(VALID_NUMBER, owner, initialBalance);

        assertEquals("123456", account.getNumber());
        assertEquals("Ivan", account.getOwner());
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldDepositPositiveAmount() {
        DebitAccount account = new DebitAccount(VALID_NUMBER, "Ivan", 10000);

        account.deposit(5000);

        assertEquals(15000, account.getBalance());
    }

    @Test
    void zeroDepositThrowsException() {
        DebitAccount account = new DebitAccount(VALID_NUMBER, "Ivan", 10000);

        assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(0)
        );
    }

    @Test
    void negativeDepositThrowsException() {
        DebitAccount account = new DebitAccount(VALID_NUMBER, "Ivan", 10000);

        assertThrows(
                InvalidAmountException.class,
                () -> account.deposit(-100)
        );
    }

    @Test
    void shouldWithdrawSuccessfully() {
        DebitAccount account = new DebitAccount(VALID_NUMBER, "Ivan", 10000);

        account.withdraw(8000);

        assertEquals(2000, account.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenInsufficientFunds() {
        DebitAccount account = new DebitAccount(VALID_NUMBER, "Ivan", 10000);

        InsufficientFundsException exception = assertThrows(
                InsufficientFundsException.class,
                () -> account.withdraw(15000)
        );

        assertTrue(exception.getMessage().contains("Insufficient funds"));
        assertEquals(10000, account.getBalance()); // Баланс не изменился
    }

    @Test
    void shouldThrowExceptionOnZeroWithdraw() {
        DebitAccount account = new DebitAccount(VALID_NUMBER, "Ivan", 10000);

        assertThrows(
                InvalidAmountException.class,
                () -> account.withdraw(0)
        );
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldThrowExceptionOnNegativeWithdraw() {
        DebitAccount account = new DebitAccount(VALID_NUMBER, "Ivan", 10000);
        assertThrows(
                InvalidAmountException.class,
                () -> account.withdraw(-100)
        );
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldNotCreateAccountWithNegativeBalance() {
        AccountNumber number = new AccountNumber("1234567890");
        assertThrows(IllegalArgumentException.class, () -> {
            new DebitAccount(number, "Test", -100);
        });
    }
    @Test
    void testToStringOutput() {
        DebitAccount account = new DebitAccount(VALID_NUMBER, "Ivan", 10000.0);

        String expected = "DebitAccount{\n" +
                " number=" + VALID_NUMBER.value() + ",\n" +
                " owner='Ivan',\n" +
                " balance=10000.0\n" +
                "}";

        String actual = account.toString();
        assertEquals(expected, actual);
    }
}