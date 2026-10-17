package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransactionStatisticsTest {

    private TransactionStatistics statistics;
    private AccountNumber account1;
    private AccountNumber account2;

    @BeforeEach
    void setUp() {
        statistics = new TransactionStatistics();
        account1 = new AccountNumber("1111111111");
        account2 = new AccountNumber("2222222222");
    }

    @Test
    void countByAccountReturnsEmptyMapForEmptyList() {
        Map<AccountNumber, Integer> result = statistics.countByAccount(new ArrayList<>());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void totalAmountByAccountReturnsEmptyMapForEmptyList() {
        Map<AccountNumber, Double> result = statistics.totalAmountByAccount(new ArrayList<>());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void statisticsForSingleAccountWithOneOperation() {
        List<Transaction> transactions = List.of(
                new Transaction(1L, TransactionType.DEPOSIT, account1, 5000.0, TransactionStatus.SUCCESS)
        );

        Map<AccountNumber, Integer> counts = statistics.countByAccount(transactions);
        Map<AccountNumber, Double> totals = statistics.totalAmountByAccount(transactions);

        assertEquals(1, counts.size());
        assertEquals(1, counts.get(account1));

        assertEquals(1, totals.size());
        assertEquals(5000.0, totals.get(account1));
    }

    @Test
    void statisticsForMultipleAccounts() {
        List<Transaction> transactions = List.of(
                new Transaction(1L, TransactionType.DEPOSIT, account1, 1000.0, TransactionStatus.SUCCESS),
                new Transaction(2L, TransactionType.WITHDRAWAL, account2, 2000.0, TransactionStatus.SUCCESS)
        );

        Map<AccountNumber, Integer> counts = statistics.countByAccount(transactions);
        Map<AccountNumber, Double> totals = statistics.totalAmountByAccount(transactions);

        assertEquals(2, counts.size());
        assertEquals(1, counts.get(account1));
        assertEquals(1, counts.get(account2));

        assertEquals(1000.0, totals.get(account1));
        assertEquals(2000.0, totals.get(account2));
    }

    @Test
    void statisticsForMultipleOperationsOfSingleAccount() {
        List<Transaction> transactions = List.of(
                new Transaction(1L, TransactionType.DEPOSIT, account1, 1000.0, TransactionStatus.SUCCESS),
                new Transaction(2L, TransactionType.WITHDRAWAL, account1, 500.0, TransactionStatus.SUCCESS),
                new Transaction(3L, TransactionType.TRANSFER, account1, 200.0, TransactionStatus.SUCCESS)
        );

        Map<AccountNumber, Integer> counts = statistics.countByAccount(transactions);
        Map<AccountNumber, Double> totals = statistics.totalAmountByAccount(transactions);

        assertEquals(1, counts.size(), "В результате должен быть только один ключ");
        assertEquals(3, counts.get(account1), "Количество операций должно быть 3");

        assertEquals(1700.0, totals.get(account1), "Сумма должна быть 1000 + 500 + 200 = 1700");
    }

    @Test
    void differentButEqualAccountNumberObjectsAggregateIntoOneRecord() {
        AccountNumber storedNumber = new AccountNumber("3333333333");
        AccountNumber lookupNumber = new AccountNumber("3333333333");

        assertNotSame(storedNumber, lookupNumber, "Это должны быть разные объекты в памяти");
        assertEquals(storedNumber, lookupNumber, "Но они должны быть равны по значению (equals)");

        List<Transaction> transactions = List.of(
                new Transaction(1L, TransactionType.DEPOSIT, storedNumber, 1000.0, TransactionStatus.SUCCESS),
                new Transaction(2L, TransactionType.WITHDRAWAL, lookupNumber, 500.0, TransactionStatus.SUCCESS)
        );

        Map<AccountNumber, Integer> counts = statistics.countByAccount(transactions);
        Map<AccountNumber, Double> totals = statistics.totalAmountByAccount(transactions);

        assertEquals(1, counts.size(), "Должна быть только одна запись в Map, так как номера равны");

        assertEquals(2, counts.get(storedNumber));
        assertEquals(2, counts.get(lookupNumber));

        assertEquals(1500.0, totals.get(storedNumber));
        assertEquals(1500.0, totals.get(lookupNumber));
    }

    @Test
    void nullTransactionsListThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> statistics.countByAccount(null));
        assertThrows(IllegalArgumentException.class, () -> statistics.totalAmountByAccount(null));
    }
}