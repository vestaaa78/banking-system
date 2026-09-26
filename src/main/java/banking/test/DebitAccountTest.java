package banking.test;

import banking.DebitAccount;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DebitAccountTest {

    @Test
    void shouldCreateAccountWithCorrectData() {
        String number = "123456";
        String owner = "Ivan";
        double initialBalance = 10000;

        DebitAccount account = new DebitAccount(number, owner, initialBalance);

        assertEquals("123456", account.getNumber());
        assertEquals("Ivan", account.getOwner());
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldDepositPositiveAmount() {
        DebitAccount account = new DebitAccount("123456", "Ivan", 10000);

        account.deposit(5000);

        assertEquals(15000, account.getBalance());
    }

    @Test
    void shouldNotDepositZeroAmount() {
        DebitAccount account = new DebitAccount("123456", "Ivan", 10000);

        account.deposit(0);

        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldNotDepositNegativeAmount() {
        DebitAccount account = new DebitAccount("123456", "Ivan", 10000);

        account.deposit(-500);

        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldWithdrawSuccessfully() {
        DebitAccount account = new DebitAccount("123456", "Ivan", 10000);

        boolean result = account.withdraw(8000);

        assertTrue(result);
        assertEquals(2000, account.getBalance());
    }

    @Test
    void shouldNotWithdrawWhenInsufficientFunds() {
        DebitAccount account = new DebitAccount("123456", "Ivan", 10000);

        boolean result = account.withdraw(15000);

        assertFalse(result);
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldNotWithdrawZeroAmount() {
        DebitAccount account = new DebitAccount("123456", "Ivan", 10000);

        boolean result = account.withdraw(0);

        assertFalse(result);
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldNotWithdrawNegativeAmount() {
        DebitAccount account = new DebitAccount("123456", "Ivan", 10000);

        boolean result = account.withdraw(-100);

        assertFalse(result);
        assertEquals(10000, account.getBalance());
    }

    @Test
    void shouldNotCreateAccountWithNegativeBalance() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DebitAccount("111", "Test", -100);
        });
    }

    @Test
    void testToStringOutput() {
        DebitAccount account = new DebitAccount("001", "Ivan", 10000.0);
        String expected = "DebitAccount{\n" +
                " number='001',\n" +
                " owner='Ivan',\n" +
                " balance=10000.0\n" +
                "}";

        String actual = account.toString();
        assertEquals(expected, actual);
    }
}