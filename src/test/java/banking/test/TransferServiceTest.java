package banking.test;

import banking.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransferServiceTest {

    private static final AccountNumber NUMBER_A = new AccountNumber("1234567890");
    private static final AccountNumber NUMBER_B = new AccountNumber("0987654321");

    @Test
    void shouldTransferSuccessfully() {
        DebitAccount accountA = new DebitAccount(NUMBER_A, "Alice", 10000);
        DebitAccount accountB = new DebitAccount(NUMBER_B, "Bob", 2000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        service.transfer(accountA, accountB, 3000);

        assertEquals(7000, accountA.getBalance());
        assertEquals(5000, accountB.getBalance());
    }

    @Test
    void shouldTransferBetweenDifferentAccountTypes() {
        DebitAccount debitAccount = new DebitAccount(NUMBER_A, "Alice", 10000);
        SavingsAccount savingsAccount = new SavingsAccount(NUMBER_B, "Bob", 2000, 1000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        service.transfer(debitAccount, savingsAccount, 1500);

        assertEquals(8500, debitAccount.getBalance());
        assertEquals(3500, savingsAccount.getBalance());
    }

    @Test
    void shouldTransferFromCreditAccount() {
        CreditAccount creditAccount = new CreditAccount(NUMBER_A, "Alice", 1000, 5000);
        DebitAccount debitAccount = new DebitAccount(NUMBER_B, "Bob", 2000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        service.transfer(creditAccount, debitAccount, 4000);

        assertEquals(-3000, creditAccount.getBalance());
        assertEquals(6000, debitAccount.getBalance());
    }

    @Test
    void shouldTransferWithPercentCommission() {
        DebitAccount accountA = new DebitAccount(NUMBER_A, "Alice", 11000);
        DebitAccount accountB = new DebitAccount(NUMBER_B, "Bob", 2000);
        TransferService service = new TransferService(new PercentCommission(1), new ConsoleNotificationService());

        service.transfer(accountA, accountB, 10000);

        assertEquals(900, accountA.getBalance());   // 11000 - 10100
        assertEquals(12000, accountB.getBalance());  // 2000 + 10000
    }

    @Test
    void shouldThrowExceptionOnZeroAmount() {
        DebitAccount accountA = new DebitAccount(NUMBER_A, "Alice", 1000);
        DebitAccount accountB = new DebitAccount(NUMBER_B, "Bob", 2000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        assertThrows(InvalidAmountException.class, () -> {
            service.transfer(accountA, accountB, 0);
        });
    }

    @Test
    void shouldThrowExceptionOnNegativeAmount() {
        DebitAccount accountA = new DebitAccount(NUMBER_A, "Alice", 1000);
        DebitAccount accountB = new DebitAccount(NUMBER_B, "Bob", 2000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        assertThrows(InvalidAmountException.class, () -> {
            service.transfer(accountA, accountB, -500);
        });
    }

    @Test
    void shouldThrowExceptionOnSameAccount() {
        DebitAccount accountA = new DebitAccount(NUMBER_A, "Alice", 1000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        assertThrows(IllegalArgumentException.class, () -> {
            service.transfer(accountA, accountA, 500);
        });
    }

    @Test
    void shouldThrowExceptionOnInsufficientFunds() {
        DebitAccount accountA = new DebitAccount(NUMBER_A, "Alice", 1000);
        DebitAccount accountB = new DebitAccount(NUMBER_B, "Bob", 2000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        assertThrows(InsufficientFundsException.class, () -> {
            service.transfer(accountA, accountB, 3000);
        });

        assertEquals(1000, accountA.getBalance());
        assertEquals(2000, accountB.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenCannotPayCommission() {
        DebitAccount accountA = new DebitAccount(NUMBER_A, "Alice", 10050);
        DebitAccount accountB = new DebitAccount(NUMBER_B, "Bob", 2000);
        TransferService service = new TransferService(new PercentCommission(1), new ConsoleNotificationService());

        assertThrows(InsufficientFundsException.class, () -> {
            service.transfer(accountA, accountB, 10000);
        });

        assertEquals(10050, accountA.getBalance());
        assertEquals(2000, accountB.getBalance());
    }

    @Test
    void shouldThrowExceptionOnTransferLimitExceeded() {
        DebitAccount accountA = new DebitAccount(NUMBER_A, "Alice", 100000);
        DebitAccount accountB = new DebitAccount(NUMBER_B, "Bob", 2000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        assertThrows(TransferLimitExceededException.class, () -> {
            service.transfer(accountA, accountB, 60000);
        });
    }

    @Test
    void shouldNotTransferFromSavingsBelowMinimum() {
        SavingsAccount savingsAccount = new SavingsAccount(NUMBER_A, "Alice", 2000, 1000);
        DebitAccount debitAccount = new DebitAccount(NUMBER_B, "Bob", 5000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        assertThrows(InsufficientFundsException.class, () -> {
            service.transfer(savingsAccount, debitAccount, 1500);
        });

        assertEquals(2000, savingsAccount.getBalance());
        assertEquals(5000, debitAccount.getBalance());
    }

    @Test
    void shouldSendNotificationOnSuccessfulTransfer() {
        DebitAccount accountA = new DebitAccount(NUMBER_A, "Alice", 10000);
        DebitAccount accountB = new DebitAccount(NUMBER_B, "Bob", 2000);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        service.transfer(accountA, accountB, 3000);

        assertEquals(1, notificationService.getNotificationCount());
        assertEquals("Transfer 3000.0 completed", notificationService.getLastMessage());
    }

    @Test
    void shouldNotSendNotificationOnFailedTransfer() {
        DebitAccount accountA = new DebitAccount(NUMBER_A, "Alice", 1000);
        DebitAccount accountB = new DebitAccount(NUMBER_B, "Bob", 2000);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        assertThrows(InsufficientFundsException.class, () -> {
            service.transfer(accountA, accountB, 3000);
        });

        assertEquals(0, notificationService.getNotificationCount());
    }
}