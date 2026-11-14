package banking.test;

import banking.adapters.ConsoleNotificationService;
import banking.adapters.FixedCommissionPolicy;
import banking.adapters.ProgressiveCommissionPolicy;
import banking.adapters.WeekendCommissionPolicy;
import banking.application.BlockedAccountRegistry;
import banking.application.TransactionJournal;
import banking.domain.AccountNumber;
import banking.domain.BankAccount;
import banking.domain.CommissionPolicy;
import banking.domain.DebitAccount;
import banking.application.TransferService;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class OcpDemonstrationTest {

    private static final AccountNumber NUM_FROM = new AccountNumber("1111111111");
    private static final AccountNumber NUM_TO = new AccountNumber("2222222222");

    @Test
    void transferWithFixedCommission() {
        CommissionPolicy policy = new FixedCommissionPolicy(100);

        TransferService service = new TransferService(
                policy,
                new ConsoleNotificationService(),
                new BlockedAccountRegistry(),
                new TransactionJournal()
        );

        BankAccount from = new DebitAccount(NUM_FROM, "Alice", 10000);
        BankAccount to = new DebitAccount(NUM_TO, "Bob", 5000);

        service.transfer(from, to, 5000);

        assertEquals(4900, from.getBalance());
        assertEquals(10000, to.getBalance());
    }

    @Test
    void transferWithProgressiveCommission() {
        CommissionPolicy policy = new ProgressiveCommissionPolicy(
                10_000, 50_000,
                0.0, 0.01, 0.02
        );

        TransferService service = new TransferService(
                policy,
                new ConsoleNotificationService(),
                new BlockedAccountRegistry(),
                new TransactionJournal()
        );

        BankAccount from = new DebitAccount(NUM_FROM, "Alice", 100000);
        BankAccount to = new DebitAccount(NUM_TO, "Bob", 5000);

        service.transfer(from, to, 30000);

        assertEquals(69700, from.getBalance());
        assertEquals(35000, to.getBalance());
    }

    @Test
    void transferWithWeekendCommission() {
        Clock saturdayClock = Clock.fixed(
                Instant.parse("2026-11-07T12:00:00Z"),
                ZoneId.of("UTC")
        );

        CommissionPolicy policy = new WeekendCommissionPolicy(
                amount -> amount * 0.01,
                amount -> amount * 0.05,
                saturdayClock
        );

        TransferService service = new TransferService(
                policy,
                new ConsoleNotificationService(),
                new BlockedAccountRegistry(),
                new TransactionJournal()
        );

        BankAccount from = new DebitAccount(NUM_FROM, "Alice", 100000);
        BankAccount to = new DebitAccount(NUM_TO, "Bob", 5000);

        service.transfer(from, to, 10000);

        assertEquals(89500, from.getBalance());
        assertEquals(15000, to.getBalance());
    }

    @Test
    void composedStrategy_vipOnWeekend() {
        Clock saturdayClock = Clock.fixed(
                Instant.parse("2026-11-07T12:00:00Z"),
                ZoneId.of("UTC")
        );

        CommissionPolicy policy = new WeekendCommissionPolicy(
                amount -> amount * 0.002,
                amount -> amount * 0.01,
                saturdayClock
        );

        TransferService service = new TransferService(
                policy,
                new ConsoleNotificationService(),
                new BlockedAccountRegistry(),
                new TransactionJournal()
        );

        BankAccount from = new DebitAccount(NUM_FROM, "Alice", 100000);
        BankAccount to = new DebitAccount(NUM_TO, "Bob", 5000);

        service.transfer(from, to, 10000);

        assertEquals(89900, from.getBalance(), "Alice: 100000 - 10000 - 100 = 89900");
        assertEquals(15000, to.getBalance(), "Bob: 5000 + 10000 = 15000");
    }

    @Test
    void lambdaAsStrategy() {
        CommissionPolicy policy = amount -> amount > 10000 ? 100 : 0;

        TransferService service = new TransferService(
                policy,
                new ConsoleNotificationService(),
                new BlockedAccountRegistry(),
                new TransactionJournal()
        );

        BankAccount from = new DebitAccount(NUM_FROM, "Alice", 10000);
        BankAccount to = new DebitAccount(NUM_TO, "Bob", 5000);

        service.transfer(from, to, 5000);

        assertEquals(5000, from.getBalance());
        assertEquals(10000, to.getBalance());
    }
}