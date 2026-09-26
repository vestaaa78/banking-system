package banking.test;

import banking.AccountNumber;
import banking.DebitAccount;
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
    void shouldNotDepositZeroAmount() {
        DebitAccount account = new DebitAccount(VALID_NUMBER, "Ivan", 10000);

        account.deposit(0);

        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldNotDepositNegativeAmount() {
        DebitAccount account = new DebitAccount(VALID_NUMBER, "Ivan", 10000);

        account.deposit(-500);

        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldWithdrawSuccessfully() {
        DebitAccount account = new DebitAccount(VALID_NUMBER, "Ivan", 10000);

        boolean result = account.withdraw(8000);

        assertTrue(result);
        assertEquals(2000, account.getBalance());
    }

    @Test
    void shouldNotWithdrawWhenInsufficientFunds() {
        DebitAccount account = new DebitAccount(VALID_NUMBER, "Ivan", 10000);

        boolean result = account.withdraw(15000);

        assertFalse(result);
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldNotWithdrawZeroAmount() {
        DebitAccount account = new DebitAccount(VALID_NUMBER, "Ivan", 10000);

        boolean result = account.withdraw(0);

        assertFalse(result);
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldNotWithdrawNegativeAmount() {
        DebitAccount account = new DebitAccount(VALID_NUMBER, "Ivan", 10000);

        boolean result = account.withdraw(-100);

        assertFalse(result);
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