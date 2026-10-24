package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;


class TransferServiceLambdaTest {

    private static final AccountNumber NUMBER_FROM = new AccountNumber("1111111111");
    private static final AccountNumber NUMBER_TO = new AccountNumber("2222222222");

    private BankAccount from;
    private BankAccount to;
    private BlockedAccountRegistry blockedAccounts;
    private TransactionJournal journal;

    @BeforeEach
    void setUp() {
        from = new DebitAccount(NUMBER_FROM, "Alice", 10000);
        to = new DebitAccount(NUMBER_TO, "Bob", 2000);
        blockedAccounts = new BlockedAccountRegistry();
        journal = new TransactionJournal();
    }

    @Test
    void transferWithNoCommissionLambda() {
        CommissionPolicy noCommission = amount -> 0.0;

        TransferService service = new TransferService(
                noCommission,
                new ConsoleNotificationService(),
                blockedAccounts,
                journal
        );

        service.transfer(from, to, 5000);

        assertEquals(5000, from.getBalance(), "Списано ровно 5000 (без комиссии)");
        assertEquals(7000, to.getBalance(), "Зачислено ровно 5000");
    }

    @Test
    void transferWithOnePercentCommissionLambda() {
        CommissionPolicy onePercent = amount -> amount * 0.01;

        TransferService service = new TransferService(
                onePercent,
                new ConsoleNotificationService(),
                blockedAccounts,
                journal
        );

        service.transfer(from, to, 10000);

        assertEquals(0, from.getBalance(), "Списано 10000 + 100 комиссии = 10100");
        assertEquals(12000, to.getBalance(), "Зачислено 10000");
    }

    @Test
    void notificationViaLambda() {
        StringBuilder log = new StringBuilder();
        NotificationService lambdaNotification = message -> log.append("NOTIFICATION: ").append(message).append("\n");

        TransferService service = new TransferService(
                amount -> 0.0,
                lambdaNotification,
                blockedAccounts,
                journal
        );

        service.transfer(from, to, 3000);

        assertTrue(log.toString().contains("Transfer 3000.0 completed"));
    }

    @Test
    void notificationViaMethodReference() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        try {
            TransferService service = new TransferService(
                    amount -> 0.0,
                    System.out::println,
                    blockedAccounts,
                    journal
            );

            service.transfer(from, to, 2500);

            String output = outputStream.toString();
            assertTrue(output.contains("Transfer 2500.0 completed"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void allFunctionalInterfacesAsLambdas() {
        CommissionPolicy flatFee = amount -> 50.0;
        StringBuilder notificationLog = new StringBuilder();
        NotificationService loggingNotification = notificationLog::append;

        TransferService service = new TransferService(
                flatFee,
                loggingNotification,
                blockedAccounts,
                journal
        );

        service.transfer(from, to, 1000);

        assertEquals(8950, from.getBalance(), "Списано 1000 + 50 комиссии");
        assertEquals(3000, to.getBalance(), "Зачислено 1000");
        assertTrue(notificationLog.toString().contains("Transfer 1000.0 completed"));
    }

    @Test
    void lambdaCanBeReused() {
        CommissionPolicy noCommission = amount -> 0.0;

        TransferService service = new TransferService(
                noCommission,
                msg -> {},
                blockedAccounts,
                journal
        );

        service.transfer(from, to, 1000);
        service.transfer(from, to, 2000);
        service.transfer(from, to, 3000);

        assertEquals(4000, from.getBalance(), "Списано 1000+2000+3000 = 6000");
        assertEquals(8000, to.getBalance(), "Зачислено 1000+2000+3000 = 6000");
    }
}