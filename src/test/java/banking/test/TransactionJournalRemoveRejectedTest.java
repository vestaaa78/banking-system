package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionJournalRemoveRejectedTest {

    private TransactionJournal journal;
    private Transaction successTx;
    private Transaction rejectedTx1;
    private Transaction rejectedTx2;
    private Transaction anotherSuccessTx;

    @BeforeEach
    void setUp() {
        journal = new TransactionJournal();
        AccountNumber accountNumber = new AccountNumber("1234567890");

        successTx = new Transaction(1L, TransactionType.DEPOSIT, accountNumber, 1000, TransactionStatus.SUCCESS);
        rejectedTx1 = new Transaction(2L, TransactionType.WITHDRAWAL, accountNumber, 500, TransactionStatus.REJECTED);
        rejectedTx2 = new Transaction(3L, TransactionType.TRANSFER, accountNumber, 750, TransactionStatus.REJECTED);
        anotherSuccessTx = new Transaction(4L, TransactionType.DEPOSIT, accountNumber, 2000, TransactionStatus.SUCCESS);
    }

    @Test
    void shouldRemoveAllRejectedTransactions() {
        journal.add(successTx);
        journal.add(rejectedTx1);
        journal.add(rejectedTx2);
        journal.add(anotherSuccessTx);

        journal.removeRejected();

        assertEquals(2, journal.size(), "Должно остаться 2 транзакции (только SUCCESS)");
        List<Transaction> all = journal.findAll();
        assertTrue(all.contains(successTx));
        assertTrue(all.contains(anotherSuccessTx));
        assertFalse(all.contains(rejectedTx1));
        assertFalse(all.contains(rejectedTx2));
    }

    @Test
    void shouldPreserveOrderOfRemainingTransactions() {
        journal.add(successTx);
        journal.add(rejectedTx1);
        journal.add(anotherSuccessTx);

        journal.removeRejected();

        List<Transaction> all = journal.findAll();
        assertEquals(2, all.size());
        assertEquals(successTx, all.get(0), "Первая транзакция должна остаться на первом месте");
        assertEquals(anotherSuccessTx, all.get(1), "Вторая транзакция должна остаться на втором месте");
    }

    @Test
    void shouldDoNothingWhenNoRejectedTransactions() {
        journal.add(successTx);
        journal.add(anotherSuccessTx);

        journal.removeRejected();

        assertEquals(2, journal.size(), "Размер не должен измениться");
    }

    @Test
    void shouldHandleEmptyJournal() {
        journal.removeRejected();

        assertEquals(0, journal.size());
    }

    @Test
    void shouldRemoveAllTransactionsIfAllRejected() {
        journal.add(rejectedTx1);
        journal.add(rejectedTx2);

        journal.removeRejected();

        assertEquals(0, journal.size(), "Все транзакции должны быть удалены");
        assertTrue(journal.findAll().isEmpty());
    }

    @Test
    void shouldNotThrowConcurrentModificationException() {
        journal.add(successTx);
        journal.add(rejectedTx1);
        journal.add(rejectedTx2);
        journal.add(anotherSuccessTx);

        assertDoesNotThrow(() -> journal.removeRejected());
        assertEquals(2, journal.size());
    }
}