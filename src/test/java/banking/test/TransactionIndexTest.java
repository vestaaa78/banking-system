package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransactionIndexTest {

    private TransactionIndex index;
    private AccountNumber account1;
    private AccountNumber account2;

    @BeforeEach
    void setUp() {
        index = new TransactionIndex();
        account1 = new AccountNumber("1111111111");
        account2 = new AccountNumber("2222222222");
    }

    @Test
    void transactionCanBeFoundByEqualAccountNumber() {
        AccountNumber stored = new AccountNumber("1111111111");
        AccountNumber lookup = new AccountNumber("1111111111");

        Transaction tx = new Transaction(1L, TransactionType.DEPOSIT, stored, 1000, TransactionStatus.SUCCESS);

        index.add(tx);
        List<Transaction> result = index.findByAccount(lookup);

        assertEquals(1, result.size(), "Должна быть найдена 1 транзакция");
        assertSame(tx, result.get(0), "Должен вернуться тот же самый объект транзакции");
    }

    @Test
    void indexConsistencyWithJournalBehavior() {
        TransactionJournal journal = new TransactionJournal();

        Transaction tx1 = new Transaction(1L, TransactionType.DEPOSIT, account1, 1000, TransactionStatus.SUCCESS);
        Transaction tx2 = new Transaction(2L, TransactionType.WITHDRAWAL, account1, 500, TransactionStatus.SUCCESS);
        Transaction tx3 = new Transaction(3L, TransactionType.TRANSFER, account2, 200, TransactionStatus.SUCCESS);

        journal.add(tx1); index.add(tx1);
        journal.add(tx2); index.add(tx2);
        journal.add(tx3); index.add(tx3);

        List<Transaction> indexedResult = index.findByAccount(account1);

        assertEquals(2, indexedResult.size());
        assertTrue(indexedResult.contains(tx1));
        assertTrue(indexedResult.contains(tx2));
        assertFalse(indexedResult.contains(tx3));
    }

    @Test
    void findByAccountReturnsDefensiveCopy() {
        Transaction tx = new Transaction(1L, TransactionType.DEPOSIT, account1, 1000, TransactionStatus.SUCCESS);
        index.add(tx);

        List<Transaction> result = index.findByAccount(account1);
        result.clear();

        assertEquals(1, index.findByAccount(account1).size(), "Очистка возвращённого списка не должна влиять на индекс");
    }

    @Test
    void findByAccountReturnsEmptyListForUnknownAccount() {
        index.add(new Transaction(1L, TransactionType.DEPOSIT, account1, 1000, TransactionStatus.SUCCESS));

        List<Transaction> result = index.findByAccount(account2);

        assertNotNull(result, "Метод не должен возвращать null");
        assertTrue(result.isEmpty(), "Для неизвестного счёта должен вернуться пустой список");
    }

    @Test
    void addNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> index.add(null));
    }
}
