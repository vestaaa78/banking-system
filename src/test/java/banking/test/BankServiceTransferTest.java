package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceTransferTest {

    private BankService bankService;
    private Repository<AccountNumber, BankAccount> repository;
    private BlockedAccountRegistry blockedRegistry;

    private AccountNumber num1;
    private AccountNumber num2;
    private AccountNumber numMissing;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>();
        blockedRegistry = new BlockedAccountRegistry();

        TransferService transferService = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService(),
                blockedRegistry,
                new TransactionJournal()
        );

        bankService = new BankService(repository, transferService, blockedRegistry);

        num1 = new AccountNumber("1111111111");
        num2 = new AccountNumber("2222222222");
        numMissing = new AccountNumber("9999999999");
    }

    @Test
    void successfulTransferByAccountNumbers() {
        bankService.openAccount(new DebitAccount(num1, "Alice", 5000));
        bankService.openAccount(new DebitAccount(num2, "Bob", 1000));

        bankService.transfer(num1, num2, 2000);

        assertEquals(3000, bankService.findAccount(num1).getBalance());
        assertEquals(3000, bankService.findAccount(num2).getBalance());
    }

    @Test
    void transferWithMissingSenderThrowsExceptionAndDoesNotChangeState() {
        bankService.openAccount(new DebitAccount(num2, "Bob", 1000));
        double initialBalance = bankService.findAccount(num2).getBalance();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.transfer(numMissing, num2, 500)
        );

        assertTrue(exception.getMessage().contains("Source account not found"));
        assertEquals(initialBalance, bankService.findAccount(num2).getBalance());
    }

    @Test
    void transferWithMissingReceiverThrowsExceptionAndDoesNotChangeState() {
        bankService.openAccount(new DebitAccount(num1, "Alice", 5000));
        double initialBalance = bankService.findAccount(num1).getBalance();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bankService.transfer(num1, numMissing, 500)
        );

        assertTrue(exception.getMessage().contains("Target account not found"));
        assertEquals(initialBalance, bankService.findAccount(num1).getBalance());
    }

    @Test
    void transferFromBlockedAccountThrowsExceptionAndDoesNotChangeState() {
        bankService.openAccount(new DebitAccount(num1, "Alice", 5000));
        bankService.openAccount(new DebitAccount(num2, "Bob", 1000));
        blockedRegistry.block(num1);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> bankService.transfer(num1, num2, 500)
        );

        assertEquals("Source account is blocked", exception.getMessage());
        assertEquals(5000, bankService.findAccount(num1).getBalance());
        assertEquals(1000, bankService.findAccount(num2).getBalance());
    }

    @Test
    void transferWithInsufficientFundsThrowsExceptionAndDoesNotChangeState() {
        bankService.openAccount(new DebitAccount(num1, "Alice", 1000));
        bankService.openAccount(new DebitAccount(num2, "Bob", 1000));

        InsufficientFundsException exception = assertThrows(
                InsufficientFundsException.class,
                () -> bankService.transfer(num1, num2, 5000)
        );

        assertTrue(exception.getMessage().contains("Insufficient funds"));
        assertEquals(1000, bankService.findAccount(num1).getBalance());
        assertEquals(1000, bankService.findAccount(num2).getBalance());
    }
}
