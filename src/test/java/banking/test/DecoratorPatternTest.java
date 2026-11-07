package banking.test;

import banking.*;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class DecoratorPatternTest {

    @Test
    void loggingDecoratorAddsLogBeforeDelegation() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            NotificationService service = new LoggingNotificationDecorator(
                    new ConsoleNotificationAdapter()
            );

            service.notify("Transfer completed");

            String result = output.toString();
            assertTrue(result.contains("[LOG] Transfer completed"), "Должно быть лог-сообщение");
            assertTrue(result.contains("Transfer completed"), "Должно быть оригинальное сообщение");
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void loggingDecoratorNullDelegateThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new LoggingNotificationDecorator(null));
    }

    @Test
    void countingDecoratorCountsNotifications() {
        CountingNotificationDecorator counting = new CountingNotificationDecorator(
                message -> { /* no-op */ }
        );

        counting.notify("first");
        counting.notify("second");
        counting.notify("third");

        assertEquals(3, counting.getCount());
    }

    @Test
    void countingDecoratorCanBeReset() {
        CountingNotificationDecorator counting = new CountingNotificationDecorator(
                message -> { /* no-op */ }
        );

        counting.notify("first");
        counting.notify("second");
        assertEquals(2, counting.getCount());

        counting.reset();
        assertEquals(0, counting.getCount());

        counting.notify("third");
        assertEquals(1, counting.getCount());
    }

    @Test
    void retryDecoratorSucceedsOnFirstAttempt() {
        StringBuilder log = new StringBuilder();
        NotificationService service = new RetryNotificationDecorator(
                message -> log.append(message),
                3
        );

        service.notify("hello");

        assertEquals("hello", log.toString());
    }

    @Test
    void retryDecoratorRetriesOnFailure() {
        StringBuilder log = new StringBuilder();
        FailingNotificationService failing = new FailingNotificationService(
                2,
                message -> log.append(message)
        );

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            NotificationService service = new RetryNotificationDecorator(failing, 3);

            service.notify("hello");

            assertEquals("hello", log.toString());
            assertEquals(3, failing.getCurrentAttempt(), "Должно быть 3 попытки");

            String retryOutput = output.toString();
            assertTrue(retryOutput.contains("[RETRY] Attempt 1 failed"));
            assertTrue(retryOutput.contains("[RETRY] Attempt 2 failed"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void retryDecoratorThrowsAfterAllAttemptsFailed() {
        FailingNotificationService failing = new FailingNotificationService(100, null);
        NotificationService service = new RetryNotificationDecorator(failing, 3);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.notify("hello")
        );

        assertTrue(exception.getMessage().contains("Failed to send notification after 3 attempts"));
        assertEquals(3, failing.getCurrentAttempt(), "Должно быть ровно 3 попытки");
    }

    @Test
    void retryDecoratorInvalidMaxAttemptsThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> new RetryNotificationDecorator(message -> {}, 0));
    }

    @Test
    void prefixDecoratorAddsPrefixToMessage() {
        StringBuilder log = new StringBuilder();
        NotificationService service = new PrefixNotificationDecorator(
                message -> log.append(message),
                "[BANK] "
        );

        service.notify("Transfer completed");

        assertEquals("[BANK] Transfer completed", log.toString());
    }

    @Test
    void prefixDecoratorWithEmptyPrefix() {
        StringBuilder log = new StringBuilder();
        NotificationService service = new PrefixNotificationDecorator(
                message -> log.append(message),
                ""
        );

        service.notify("hello");
        assertEquals("hello", log.toString());
    }

    @Test
    void complexChain_countingAndPrefixAndRetry() {
        StringBuilder log = new StringBuilder();

        FailingNotificationService failing = new FailingNotificationService(
                1,
                message -> log.append(message)
        );

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            NotificationService service =
                    new CountingNotificationDecorator(
                            new PrefixNotificationDecorator(
                                    new RetryNotificationDecorator(
                                            failing,
                                            3
                                    ),
                                    "[BANK] "
                            )
                    );

            service.notify("test");

            assertEquals("[BANK] test", log.toString());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void decoratorOrderMatters() {


        StringBuilder log1 = new StringBuilder();
        StringBuilder log2 = new StringBuilder();

        NotificationService service1 = new PrefixNotificationDecorator(
                message -> log1.append(message),
                "[A]"
        );

        NotificationService service2 = new PrefixNotificationDecorator(
                message -> log2.append(message),
                "[B]"
        );

        service1.notify("hello");
        service2.notify("world");

        assertEquals("[A]hello", log1.toString());
        assertEquals("[B]world", log2.toString());
    }

    @Test
    void decoratorWrapsAdapter() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(output));

        try {
            NotificationService service = new LoggingNotificationDecorator(
                    new ConsoleNotificationAdapter()
            );

            service.notify("hello");

            String result = output.toString();
            assertTrue(result.contains("[LOG] hello"));
            assertTrue(result.contains("hello"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void decoratorsDoNotModifyBaseService() {
        StringBuilder baseLog = new StringBuilder();
        NotificationService baseService = message -> baseLog.append(message);

        NotificationService decorated = new PrefixNotificationDecorator(
                new CountingNotificationDecorator(baseService),
                "[TEST] "
        );

        decorated.notify("hello");

        assertEquals("[TEST] hello", baseLog.toString());
    }
}