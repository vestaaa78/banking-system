package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferServiceExceptionTest {

    private static final AccountNumber NUMBER_FROM = new AccountNumber("1234567890");
    private static final AccountNumber NUMBER_TO = new AccountNumber("0987654321");

    private TransferService service;
    private BankAccount from;
    private BankAccount to;

    @BeforeEach
    void setUp() {
        service = new TransferService(new NoCommission(), new ConsoleNotificationService());
        from = new DebitAccount(NUMBER_FROM, "Alice", 10000);
        to = new DebitAccount(NUMBER_TO, "Bob", 2000);
    }

    @Test
    void negativeTransferThrowsException() {
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> service.transfer(from, to, -500)
        );

        assertEquals("Amount must be positive", exception.getMessage());

        assertEquals(10000, from.getBalance());
        assertEquals(2000, to.getBalance());
    }

    @Test
    void zeroTransferThrowsException() {
        InvalidAmountException exception = assertThrows(
                InvalidAmountException.class,
                () -> service.transfer(from, to, 0)
        );

        assertEquals("Amount must be positive", exception.getMessage());
        assertEquals(10000, from.getBalance());
        assertEquals(2000, to.getBalance());
    }

    @Test
    void transferToSameAccountThrowsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.transfer(from, from, 500)
        );

        assertEquals("Cannot transfer to the same account", exception.getMessage());
        assertEquals(10000, from.getBalance());
    }

    @Test
    void transferWithoutEnoughMoneyThrowsException() {
        InsufficientFundsException exception = assertThrows(
                InsufficientFundsException.class,
                () -> service.transfer(from, to, 15000)
        );

        assertTrue(exception.getMessage().contains("Insufficient funds"));

        assertEquals(10000, from.getBalance());
        assertEquals(2000, to.getBalance());
    }

    @Test
    void transferOverLimitThrowsException() {
        TransferLimitExceededException exception = assertThrows(
                TransferLimitExceededException.class,
                () -> service.transfer(from, to, 60000)
        );

        assertEquals("Transfer limit exceeded: 60000.0", exception.getMessage());

        assertEquals(10000, from.getBalance());
        assertEquals(2000, to.getBalance());
    }
}