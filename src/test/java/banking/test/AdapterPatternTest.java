package banking.test;

import banking.adapters.*;
import banking.application.AccountService;
import banking.application.BlockedAccountRegistry;
import banking.application.TransactionJournal;
import banking.domain.AccountNumber;
import banking.domain.BankAccount;
import banking.domain.DebitAccount;
import banking.ports.AccountRepository;
import banking.ports.NotificationService;
import banking.application.TransferService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


import static org.junit.jupiter.api.Assertions.*;

class AdapterPatternTest {

    private static final AccountNumber NUM_FROM = new AccountNumber("1111111111");
    private static final AccountNumber NUM_TO = new AccountNumber("2222222222");

    @Test
    void inMemoryAccountRepository_isAdapterOfAccountRepositoryPort() {
        AccountRepository port = new InMemoryAccountRepository();

        BankAccount account = new DebitAccount(NUM_FROM, "Alice", 5000);

        port.save(account);

        assertTrue(port.findById(NUM_FROM).isPresent());
        assertEquals(5000, port.findById(NUM_FROM).get().getBalance());
    }

    @Test
    void inMemoryAccountRepository_canBeReplacedWithAnotherAdapter() {
        AccountRepository port = new FakeAccountRepository();

        BankAccount account = new DebitAccount(NUM_FROM, "Alice", 5000);

        port.save(account);

        assertTrue(port.findById(NUM_FROM).isPresent());
        assertEquals(5000, port.findById(NUM_FROM).get().getBalance());
    }

    @Test
    void consoleNotificationAdapter_isAdapterOfNotificationServicePort() {
        NotificationService port = new ConsoleNotificationAdapter();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            port.notify("Test message");

            assertTrue(output.toString().contains("Test message"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void loggingNotificationAdapter_isAnotherAdapterOfSamePort() {
        NotificationService port = new LoggingNotificationAdapter();

        port.notify("Transfer completed");
        port.notify("Account opened");

        assertTrue(port instanceof LoggingNotificationAdapter);
        LoggingNotificationAdapter logging = (LoggingNotificationAdapter) port;
        assertEquals(2, logging.size());
        assertTrue(logging.getLog().get(0).contains("Transfer completed"));
    }

    @Test
    void transferService_worksWithConsoleAdapter() {

        TransferService service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationAdapter(),
                new BlockedAccountRegistry(),
                new TransactionJournal()
        );

        BankAccount from = new DebitAccount(NUM_FROM, "Alice", 10000);
        BankAccount to = new DebitAccount(NUM_TO, "Bob", 5000);

        assertDoesNotThrow(() -> service.transfer(from, to, 1000));
        assertEquals(9000, from.getBalance());
    }

    @Test
    void transferService_worksWithLoggingAdapter() {
        LoggingNotificationAdapter logging = new LoggingNotificationAdapter();
        TransferService service = new TransferService(
                new NoCommission(),
                logging,
                new BlockedAccountRegistry(),
                new TransactionJournal()
        );

        BankAccount from = new DebitAccount(NUM_FROM, "Alice", 10000);
        BankAccount to = new DebitAccount(NUM_TO, "Bob", 5000);

        service.transfer(from, to, 1000);

        assertEquals(1, logging.size());
        assertTrue(logging.getLog().get(0).contains("Transfer"));
    }

    @Test
    void transferService_worksWithCompositeAdapter() {
        LoggingNotificationAdapter logging = new LoggingNotificationAdapter();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            CompositeNotificationAdapter composite = new CompositeNotificationAdapter(
                    new ConsoleNotificationAdapter(),
                    logging
            );

            TransferService service = new TransferService(
                    new NoCommission(),
                    composite,
                    new BlockedAccountRegistry(),
                    new TransactionJournal()
            );

            BankAccount from = new DebitAccount(NUM_FROM, "Alice", 10000);
            BankAccount to = new DebitAccount(NUM_TO, "Bob", 5000);

            service.transfer(from, to, 1000);

            assertTrue(output.toString().contains("Transfer"), "Консольный адаптер получил уведомление");
            assertEquals(1, logging.size(), "Логгирующий адаптер получил уведомление");
            assertEquals(2, composite.getAdapterCount(), "Композит содержит 2 адаптера");
        } finally {
            System.setOut(originalOut);
        }
    }


    @Test
    void lambdaAsAdapter() {
        NotificationService port = message -> { /* ничего не делаем */ };

        assertDoesNotThrow(() -> port.notify("test"));
    }

    @Test
    void businessLayerDoesNotKnowAboutAdapters() {
        AccountRepository port = new InMemoryAccountRepository();
        AccountService service = new AccountService(port);

        service.open(new DebitAccount(NUM_FROM, "Alice", 5000));

        assertTrue(service.find(NUM_FROM).isPresent());

        AccountRepository anotherPort = new FakeAccountRepository();
        AccountService anotherService = new AccountService(anotherPort);
        anotherService.open(new DebitAccount(NUM_FROM, "Bob", 3000));
        assertTrue(anotherService.find(NUM_FROM).isPresent());
    }
}