package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransferServiceBlockedAccountTest {

    private static final AccountNumber NUMBER_FROM = new AccountNumber("1234567890");
    private static final AccountNumber NUMBER_TO = new AccountNumber("0987654321");

    private TransferService service;
    private BlockedAccountRegistry blockedAccounts;
    private BankAccount from;
    private BankAccount to;
    private FakeNotificationService notificationService;

    @BeforeEach
    void setUp() {
        blockedAccounts = new BlockedAccountRegistry();
        notificationService = new FakeNotificationService();
        service = new TransferService(
                new NoCommission(),
                notificationService,
                blockedAccounts,
                new TransactionJournal()
        );
        from = new DebitAccount(NUMBER_FROM, "Alice", 10000);
        to = new DebitAccount(NUMBER_TO, "Bob", 2000);
    }

    @Test
    void transferFromBlockedAccountThrowsException() {
        blockedAccounts.block(NUMBER_FROM);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> service.transfer(from, to, 3000)
        );

        assertEquals("Source account is blocked", exception.getMessage());
    }

    @Test
    void balancesDoNotChangeWhenSourceAccountIsBlocked() {
        blockedAccounts.block(NUMBER_FROM);

        assertThrows(IllegalStateException.class, () -> {
            service.transfer(from, to, 3000);
        });

        assertEquals(10000, from.getBalance(), "Баланс отправителя не должен измениться");
        assertEquals(2000, to.getBalance(), "Баланс получателя не должен измениться");
    }

    @Test
    void notificationNotSentWhenSourceAccountIsBlocked() {
        blockedAccounts.block(NUMBER_FROM);

        assertThrows(IllegalStateException.class, () -> {
            service.transfer(from, to, 3000);
        });

        assertEquals(0, notificationService.getNotificationCount(),
                "Уведомление не должно быть отправлено при блокировке счёта");
    }

    @Test
    void transferSucceedsWhenAccountIsNotBlocked() {

        service.transfer(from, to, 3000);

        assertEquals(7000, from.getBalance());
        assertEquals(5000, to.getBalance());
        assertEquals(1, notificationService.getNotificationCount());
    }

    @Test
    void transferSucceedsAfterUnblocking() {
        blockedAccounts.block(NUMBER_FROM);
        blockedAccounts.unblock(NUMBER_FROM);

        service.transfer(from, to, 3000);

        assertEquals(7000, from.getBalance());
        assertEquals(5000, to.getBalance());
    }
}