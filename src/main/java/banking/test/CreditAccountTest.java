package banking.test;

import banking.AccountNumber;
import banking.CreditAccount;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CreditAccountTest {
    private static final AccountNumber VALID_NUMBER = new AccountNumber("1234567890");
    private static final AccountNumber ANOTHER_NUMBER = new AccountNumber("0987654321");
    @Test
    void shouldCreateAccountWithCorrectData() {
        String number = "111222";
        String owner = "Alex";
        double initialBalance = 1000;
        double creditLimit = 5000;

        CreditAccount account = new CreditAccount(VALID_NUMBER, owner, initialBalance, creditLimit);

        assertEquals("111222", account.getNumber());
        assertEquals("Alex", account.getOwner());
        assertEquals(1000, account.getBalance());
        assertEquals(5000, account.getCreditLimit());
    }

    @Test
    void shouldWithdrawWithinCreditLimit() {
        CreditAccount account = new CreditAccount(VALID_NUMBER, "Alex", 1000, 5000);

        boolean result = account.withdraw(4000);
        assertTrue(result);
        assertEquals(-3000, account.getBalance());
    }

    @Test
    void shouldWithdrawExactlyAtCreditLimit() {
        CreditAccount account = new CreditAccount(VALID_NUMBER, "Alex", 1000, 5000);

        boolean result = account.withdraw(6000);

        assertTrue(result);
        assertEquals(-5000, account.getBalance());
    }

    @Test
    void shouldNotWithdrawExceedingCreditLimit() {
        CreditAccount account = new CreditAccount(VALID_NUMBER, "Alex", 1000, 5000);
        account.withdraw(4000); // balance = -3000

        boolean result = account.withdraw(3000); // -3000 - 3000 = -6000 < -5000

        assertFalse(result);
        assertEquals(-3000, account.getBalance());
    }

    @Test
    void shouldNotWithdrawZeroAmount() {
        CreditAccount account = new CreditAccount(VALID_NUMBER, "Alex", 1000, 5000);

        boolean result = account.withdraw(0);

        assertFalse(result);
        assertEquals(1000, account.getBalance());
    }

    @Test
    void shouldNotWithdrawNegativeAmount() {

        CreditAccount account = new CreditAccount(VALID_NUMBER, "Alex", 1000, 5000);

        boolean result = account.withdraw(-100);

        assertFalse(result);
        assertEquals(1000, account.getBalance());
    }

    @Test
    void shouldDepositToNegativeBalance() {
        CreditAccount account = new CreditAccount(VALID_NUMBER, "Alex", -2000, 5000);

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
}