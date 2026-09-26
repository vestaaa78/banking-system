package banking.test;

import banking.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TransferServiceTest {

    @Test
    void shouldTransferSuccessfully() {
        DebitAccount accountA = new DebitAccount("111", "Alice", 10000);
        DebitAccount accountB = new DebitAccount("222", "Bob", 2000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        boolean result = service.transfer(accountA, accountB, 3000);

        assertTrue(result);
        assertEquals(7000, accountA.getBalance());
        assertEquals(5000, accountB.getBalance());
    }

    @Test
    void shouldNotTransferWhenInsufficientFunds() {
        DebitAccount accountA = new DebitAccount("111", "Alice", 1000);
        DebitAccount accountB = new DebitAccount("222", "Bob", 2000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        boolean result = service.transfer(accountA, accountB, 3000);

        assertFalse(result);
        assertEquals(1000, accountA.getBalance());
        assertEquals(2000, accountB.getBalance());
    }

    @Test
    void shouldNotTransferZeroAmount() {
        DebitAccount accountA = new DebitAccount("111", "Alice", 1000);
        DebitAccount accountB = new DebitAccount("222", "Bob", 2000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        boolean result = service.transfer(accountA, accountB, 0);

        assertFalse(result);
        assertEquals(1000, accountA.getBalance());
        assertEquals(2000, accountB.getBalance());
    }

    @Test
    void shouldNotTransferNegativeAmount() {
        DebitAccount accountA = new DebitAccount("111", "Alice", 1000);
        DebitAccount accountB = new DebitAccount("222", "Bob", 2000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        boolean result = service.transfer(accountA, accountB, -500);

        // Assert
        assertFalse(result);
        assertEquals(1000, accountA.getBalance());
        assertEquals(2000, accountB.getBalance());
    }

    @Test
    void shouldNotTransferToSameAccount() {
        DebitAccount accountA = new DebitAccount("111", "Alice", 1000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        boolean result = service.transfer(accountA, accountA, 500);

        assertFalse(result);
        assertEquals(1000, accountA.getBalance());
    }

    @Test
    void shouldTransferBetweenDifferentAccountTypes() {
        DebitAccount debitAccount = new DebitAccount("111", "Alice", 10000);
        SavingsAccount savingsAccount = new SavingsAccount("222", "Bob", 2000, 1000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        boolean result = service.transfer(debitAccount, savingsAccount, 1500);

        assertTrue(result);
        assertEquals(8500, debitAccount.getBalance());
        assertEquals(3500, savingsAccount.getBalance());
    }

    @Test
    void shouldNotTransferFromSavingsBelowMinimum() {
        SavingsAccount savingsAccount = new SavingsAccount("111", "Alice", 2000, 1000);
        DebitAccount debitAccount = new DebitAccount("222", "Bob", 5000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        boolean result = service.transfer(savingsAccount, debitAccount, 1500);

        assertFalse(result);
        assertEquals(2000, savingsAccount.getBalance());
        assertEquals(5000, debitAccount.getBalance());
    }

    @Test
    void shouldTransferFromCreditAccount() {
        CreditAccount creditAccount = new CreditAccount("111", "Alice", 1000, 5000);
        DebitAccount debitAccount = new DebitAccount("222", "Bob", 2000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        boolean result = service.transfer(creditAccount, debitAccount, 4000);

        assertTrue(result);
        assertEquals(-3000, creditAccount.getBalance());
        assertEquals(6000, debitAccount.getBalance());
    }

    @Test
    void shouldTransferFromSavingsToDebit() {
        SavingsAccount savingsAccount = new SavingsAccount("111", "Alice", 10000, 1000);
        DebitAccount debitAccount = new DebitAccount("222", "Bob", 2000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        boolean result = service.transfer(savingsAccount, debitAccount, 5000);

        assertTrue(result);
        assertEquals(5000, savingsAccount.getBalance());
        assertEquals(7000, debitAccount.getBalance());
    }

    @Test
    void shouldNotTransferFromSavingsToDebitWhenBelowMinimum() {
        SavingsAccount savingsAccount = new SavingsAccount("111", "Alice", 2000, 1000);
        DebitAccount debitAccount = new DebitAccount("222", "Bob", 5000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        boolean result = service.transfer(savingsAccount, debitAccount, 1500);

        assertFalse(result);
        assertEquals(2000, savingsAccount.getBalance());
        assertEquals(5000, debitAccount.getBalance());
    }

    @Test
    void shouldTransferFromCreditToSavings() {
        CreditAccount creditAccount = new CreditAccount("111", "Alice", 1000, 5000);
        SavingsAccount savingsAccount = new SavingsAccount("222", "Bob", 2000, 1000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        boolean result = service.transfer(creditAccount, savingsAccount, 3000);

        assertTrue(result);
        assertEquals(-2000, creditAccount.getBalance());
        assertEquals(5000, savingsAccount.getBalance());
    }

    @Test
    void shouldTransferBetweenTwoSavingsAccounts() {
        SavingsAccount sender = new SavingsAccount("111", "Alice", 10000, 1000);
        SavingsAccount receiver = new SavingsAccount("222", "Bob", 5000, 500);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        boolean result = service.transfer(sender, receiver, 4000);

        assertTrue(result);
        assertEquals(6000, sender.getBalance());
        assertEquals(9000, receiver.getBalance());
    }

    @Test
    void shouldTransferBetweenTwoCreditAccounts() {
        CreditAccount sender = new CreditAccount("111", "Alice", 1000, 5000);
        CreditAccount receiver = new CreditAccount("222", "Bob", -2000, 3000);
        TransferService service = new TransferService(new NoCommission(), new ConsoleNotificationService());

        boolean result = service.transfer(sender, receiver, 2000);

        assertTrue(result);
        assertEquals(-1000, sender.getBalance());
        assertEquals(0, receiver.getBalance());
    }

    @Test
    void shouldTransferWithPercentCommission() {
        DebitAccount accountA = new DebitAccount("111", "Alice", 11000);
        DebitAccount accountB = new DebitAccount("222", "Bob", 2000);
        TransferService service = new TransferService(new PercentCommission(1), new ConsoleNotificationService());

        boolean result = service.transfer(accountA, accountB, 10000);

        assertTrue(result);
        assertEquals(900, accountA.getBalance());
        assertEquals(12000, accountB.getBalance());
    }

    @Test
    void shouldNotTransferWhenCannotPayCommission() {
        DebitAccount accountA = new DebitAccount("111", "Alice", 10050);
        DebitAccount accountB = new DebitAccount("222", "Bob", 2000);
        TransferService service = new TransferService(new PercentCommission(1), new ConsoleNotificationService());

        boolean result = service.transfer(accountA, accountB, 10000);

        assertFalse(result);
        assertEquals(10050, accountA.getBalance());
        assertEquals(2000, accountB.getBalance());
    }

    @Test
    void shouldTransferWithPercentCommissionFromCreditAccount() {
        CreditAccount creditAccount = new CreditAccount("111", "Alice", 1000, 5000);
        DebitAccount debitAccount = new DebitAccount("222", "Bob", 2000);
        TransferService service = new TransferService(new PercentCommission(2), new ConsoleNotificationService());

        boolean result = service.transfer(creditAccount, debitAccount, 4000);

        assertTrue(result);
        assertEquals(-3080, creditAccount.getBalance());
        assertEquals(6000, debitAccount.getBalance());
    }

    @Test
    void shouldSendExactlyOneNotificationOnSuccessfulTransfer() {
        DebitAccount accountA = new DebitAccount("111", "Alice", 10000);
        DebitAccount accountB = new DebitAccount("222", "Bob", 2000);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        boolean result = service.transfer(accountA, accountB, 3000);

        assertTrue(result);
        assertEquals(1, notificationService.getNotificationCount());
    }

    @Test
    void shouldNotSendNotificationOnFailedTransfer() {
        DebitAccount accountA = new DebitAccount("111", "Alice", 1000);
        DebitAccount accountB = new DebitAccount("222", "Bob", 2000);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        boolean result = service.transfer(accountA, accountB, 3000);

        assertFalse(result);
        assertEquals(0, notificationService.getNotificationCount());
    }

    @Test
    void shouldHaveCorrectNotificationMessage() {
        DebitAccount accountA = new DebitAccount("111", "Alice", 10000);
        DebitAccount accountB = new DebitAccount("222", "Bob", 2000);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        service.transfer(accountA, accountB, 3000);

        assertEquals("Transfer 3000.0 completed", notificationService.getLastMessage());
    }

    @Test
    void shouldSendNotificationWithCommission() {
        DebitAccount accountA = new DebitAccount("111", "Alice", 11000);
        DebitAccount accountB = new DebitAccount("222", "Bob", 2000);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new PercentCommission(1), notificationService);

        boolean result = service.transfer(accountA, accountB, 10000);

        assertTrue(result);
        assertEquals(1, notificationService.getNotificationCount());
        assertEquals("Transfer 10000.0 completed", notificationService.getLastMessage());
    }

    @Test
    void shouldCountMultipleNotifications() {
        DebitAccount accountA = new DebitAccount("111", "Alice", 20000);
        DebitAccount accountB = new DebitAccount("222", "Bob", 2000);
        FakeNotificationService notificationService = new FakeNotificationService();
        TransferService service = new TransferService(new NoCommission(), notificationService);

        service.transfer(accountA, accountB, 3000);
        service.transfer(accountA, accountB, 5000);

        assertEquals(2, notificationService.getNotificationCount());
        assertEquals("Transfer 5000.0 completed", notificationService.getLastMessage());
    }
}