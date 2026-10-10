package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransferServiceJournalTest {

    private static final AccountNumber NUMBER_FROM = new AccountNumber("1234567890");
    private static final AccountNumber NUMBER_TO = new AccountNumber("0987654321");

    private TransferService service;
    private TransactionJournal journal;
    private BankAccount from;
    private BankAccount to;

    @BeforeEach
    void setUp() {
        journal = new TransactionJournal();
        service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService(),
                new BlockedAccountRegistry(),
                journal
        );
        from = new DebitAccount(NUMBER_FROM, "Alice", 10000);
        to = new DebitAccount(NUMBER_TO, "Bob", 2000);
    }

    @Test
    void successfulTransferAddsEntryToJournal() {
        service.transfer(from, to, 3000);

        assertEquals(1, journal.size());
        List<Transaction> all = journal.findAll();
        Transaction tx = all.get(0);

        assertEquals(TransactionType.TRANSFER, tx.type());
        assertEquals(NUMBER_FROM, tx.account());
        assertEquals(3000.0, tx.amount());
        assertEquals(TransactionStatus.SUCCESS, tx.status());
    }

    @Test
    void failedTransferDoesNotAddEntryToJournal() {
        BlockedAccountRegistry blockedAccounts = new BlockedAccountRegistry();
        blockedAccounts.block(NUMBER_FROM);
        service = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService(),
                blockedAccounts,
                journal
        );

        assertThrows(IllegalStateException.class, () -> {
            service.transfer(from, to, 3000);
        });

        assertEquals(0, journal.size());
    }

    @Test
    void multipleTransfersAddMultipleEntriesInOrder() {
        service.transfer(from, to, 1000);
        service.transfer(from, to, 2000);
        service.transfer(from, to, 500);

        assertEquals(3, journal.size());
        List<Transaction> all = journal.findAll();

        assertEquals(1000.0, all.get(0).amount());
        assertEquals(2000.0, all.get(1).amount());
        assertEquals(500.0, all.get(2).amount());

        assertEquals(1L, all.get(0).id());
        assertEquals(2L, all.get(1).id());
        assertEquals(3L, all.get(2).id());
    }

    @Test
    void journalStoresSenderAccountNumber() {
        service.transfer(from, to, 3000);

        Transaction tx = journal.findAll().get(0);
        assertEquals(NUMBER_FROM, tx.account(), "Журнал должен хранить номер счёта отправителя");
    }
}