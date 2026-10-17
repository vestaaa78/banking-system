package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionJournalFindByAccountTest {

    private TransactionJournal journal;
    private AccountNumber account1;
    private AccountNumber account2;
    private AccountNumber account3;

    @BeforeEach
    void setUp() {
        journal = new TransactionJournal();
        account1 = new AccountNumber("1111111111");
        account2 = new AccountNumber("2222222222");
        account3 = new AccountNumber("3333333333");
    }

    @Test
    void shouldFindAllTransactionsForSpecificAccount() {
        Transaction tx1 = new Transaction(1L, TransactionType.DEPOSIT, account1, 1000, TransactionStatus.SUCCESS);
        Transaction tx2 = new Transaction(2L, TransactionType.WITHDRAWAL, account2, 500, TransactionStatus.SUCCESS);
        Transaction tx3 = new Transaction(3L, TransactionType.TRANSFER, account1, 200, TransactionStatus.SUCCESS);

        journal.add(tx1);
        journal.add(tx2);
        journal.add(tx3);

        List<Transaction> result = journal.findByAccount(account1);

        assertEquals(2, result.size());
        assertTrue(result.contains(tx1));
        assertTrue(result.contains(tx3));
        assertFalse(result.contains(tx2));
    }

    @Test
    void shouldReturnEmptyListForAccountWithNoTransactions() {
        journal.add(new Transaction(1L, TransactionType.DEPOSIT, account1, 1000, TransactionStatus.SUCCESS));

        List<Transaction> result = journal.findByAccount(account3);

        assertNotNull(result, "Метод не должен возвращать null");
        assertTrue(result.isEmpty(), "Для счёта без транзакций должен вернуться пустой список");
    }

    @Test
    void shouldPreserveOrderOfTransactions() {
        Transaction tx1 = new Transaction(1L, TransactionType.DEPOSIT, account1, 1000, TransactionStatus.SUCCESS);
        Transaction tx2 = new Transaction(2L, TransactionType.WITHDRAWAL, account1, 500, TransactionStatus.SUCCESS);

        journal.add(tx1);
        journal.add(tx2);

        List<Transaction> result = journal.findByAccount(account1);

        assertEquals(tx1, result.get(0), "Порядок транзакций должен совпадать с порядком добавления");
        assertEquals(tx2, result.get(1));
    }

    @Test
    void shouldReturnDefensiveCopy() {
        journal.add(new Transaction(1L, TransactionType.DEPOSIT, account1, 1000, TransactionStatus.SUCCESS));

        List<Transaction> result = journal.findByAccount(account1);
        result.clear();

        assertEquals(1, journal.size(), "Очистка результата не должна влиять на внутренний журнал");
    }

    @Test
    void shouldThrowExceptionOnNullAccountNumber() {
        assertThrows(IllegalArgumentException.class, () -> journal.findByAccount(null));
    }
}