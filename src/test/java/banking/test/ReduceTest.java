package banking.test;

import banking.AccountNumber;
import banking.BankAccount;
import banking.DebitAccount;
import banking.SavingsAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ReduceTest {

    private List<BankAccount> accounts;

    @BeforeEach
    void setUp() {
        accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        accounts.add(new DebitAccount(new AccountNumber("2222222222"), "Bob", 10000));
        accounts.add(new SavingsAccount(new AccountNumber("3333333333"), "Charlie", 15000, 1000));
    }

    @Test
    void manualLoopAndReduceProduceSameResult() {
        double totalLoop = 0;
        for (BankAccount a : accounts) {
            totalLoop += a.getBalance();
        }

        double totalReduce = accounts.stream()
                .map(BankAccount::getBalance)
                .reduce(0.0, Double::sum);

        assertEquals(totalLoop, totalReduce,
                "Ручной цикл и reduce должны дать одинаковый результат");
        assertEquals(30000.0, totalReduce);
    }

    @Test
    void reduceWithIdentityReturnsSum() {
        double total = accounts.stream()
                .map(BankAccount::getBalance)
                .reduce(0.0, Double::sum);

        assertEquals(30000.0, total);
    }

    @Test
    void reduceWithIdentityReturnsIdentityForEmptyList() {
        List<BankAccount> emptyList = new ArrayList<>();

        double total = emptyList.stream()
                .map(BankAccount::getBalance)
                .reduce(0.0, Double::sum);

        assertEquals(0.0, total);
    }

    @Test
    void reduceWithIdentityAndLambda() {
        double total = accounts.stream()
                .map(BankAccount::getBalance)
                .reduce(0.0, (a, b) -> a + b);

        assertEquals(30000.0, total);
    }

    @Test
    void reduceWithoutIdentityReturnsOptional() {
        Optional<Double> total = accounts.stream()
                .map(BankAccount::getBalance)
                .reduce(Double::sum);

        assertTrue(total.isPresent());
        assertEquals(30000.0, total.get());
    }

    @Test
    void reduceWithoutIdentityReturnsEmptyForEmptyList() {
        List<BankAccount> emptyList = new ArrayList<>();

        Optional<Double> total = emptyList.stream()
                .map(BankAccount::getBalance)
                .reduce(Double::sum);

        assertTrue(total.isEmpty());
    }

    @Test
    void reduceForMax() {
        Optional<Double> max = accounts.stream()
                .map(BankAccount::getBalance)
                .reduce(Double::max);

        assertTrue(max.isPresent());
        assertEquals(15000.0, max.get());
    }

    @Test
    void reduceForMin() {
        Optional<Double> min = accounts.stream()
                .map(BankAccount::getBalance)
                .reduce(Double::min);

        assertTrue(min.isPresent());
        assertEquals(5000.0, min.get());
    }

    @Test
    void reduceForConcatenation() {
        String owners = accounts.stream()
                .map(BankAccount::getOwner)
                .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b);

        assertEquals("Alice, Bob, Charlie", owners);
    }

    @Test
    void reduceForCount() {
        int count = accounts.stream()
                .reduce(0, (n, account) -> n + 1, Integer::sum);

        assertEquals(3, count);
    }

    @Test
    void reduceForAccumulatingStatistics() {
        AccountStatistics stats = accounts.stream()
                .reduce(
                        new AccountStatistics(0, 0, 0),
                        (acc, account) -> new AccountStatistics(
                                acc.count + 1,
                                acc.totalBalance + account.getBalance(),
                                Math.max(acc.maxBalance, account.getBalance())
                        ),
                        (acc1, acc2) -> new AccountStatistics(
                                acc1.count + acc2.count,
                                acc1.totalBalance + acc2.totalBalance,
                                Math.max(acc1.maxBalance, acc2.maxBalance)
                        )
                );

        assertEquals(3, stats.count);
        assertEquals(30000.0, stats.totalBalance);
        assertEquals(15000.0, stats.maxBalance);
    }

    @Test
    void reduceEquivalentToMapToDoubleSum() {
        double withReduce = accounts.stream()
                .map(BankAccount::getBalance)
                .reduce(0.0, Double::sum);

        double withMapToDouble = accounts.stream()
                .mapToDouble(BankAccount::getBalance)
                .sum();

        assertEquals(withReduce, withMapToDouble,
                "reduce и mapToDouble().sum() должны дать одинаковый результат");
    }

    @Test
    void reduceAppliesAccumulatorLeftToRight() {

        List<String> words = List.of("A", "B", "C");

        String result = words.stream()
                .reduce("", (a, b) -> a + b);
        assertEquals("ABC", result);
    }

    @Test
    void reduceWithNonCommutativeOperation() {
        List<Integer> numbers = List.of(1, 2, 3, 4);

        int result = numbers.stream()
                .reduce(0, (a, b) -> a - b);
        assertEquals(-10, result);
    }

    record AccountStatistics(int count, double totalBalance, double maxBalance) {
    }
}