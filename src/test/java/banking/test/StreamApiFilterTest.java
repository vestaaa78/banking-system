package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StreamApiFilterTest {

    private List<BankAccount> accounts;

    @BeforeEach
    void setUp() {
        accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Alice", 150000));
        accounts.add(new DebitAccount(new AccountNumber("2222222222"), "Bob", 50000));
        accounts.add(new SavingsAccount(new AccountNumber("3333333333"), "Charlie", 200000, 10000));
        accounts.add(new CreditAccount(new AccountNumber("4444444444"), "Dave", 5000, 50000));
        accounts.add(new DebitAccount(new AccountNumber("5555555555"), "Eve", 300000));
    }

    @Test
    void filterByBalanceUsingLoop() {
        List<BankAccount> result = new ArrayList<>();
        for (BankAccount account : accounts) {
            if (account.getBalance() >= 100_000) {
                result.add(account);
            }
        }

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(a -> a.getBalance() >= 100_000));
    }

    @Test
    void filterByBalanceUsingStream() {
        List<BankAccount> result = accounts.stream()
                .filter(a -> a.getBalance() >= 100_000)
                .toList();

        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(a -> a.getBalance() >= 100_000));
    }

    @Test
    void loopAndStreamProduceSameResult() {
        List<BankAccount> loopResult = new ArrayList<>();
        for (BankAccount account : accounts) {
            if (account.getBalance() >= 100_000) {
                loopResult.add(account);
            }
        }

        List<BankAccount> streamResult = accounts.stream()
                .filter(a -> a.getBalance() >= 100_000)
                .toList();

        assertEquals(loopResult.size(), streamResult.size());
        assertEquals(loopResult, streamResult, "Оба подхода должны вернуть одинаковые элементы в том же порядке");
    }

    @Test
    void filterByDifferentThreshold() {
        double threshold = 50_000;

        List<BankAccount> result = accounts.stream()
                .filter(a -> a.getBalance() >= threshold)
                .toList();

        assertEquals(4, result.size());
    }

    @Test
    void streamDoesNotModifyOriginalList() {
        int originalSize = accounts.size();

        accounts.stream()
                .filter(a -> a.getBalance() >= 100_000)
                .toList();

        assertEquals(originalSize, accounts.size(), "Исходный список не должен измениться");
    }

    @Test
    void filterReturnsEmptyListWhenNoMatches() {
        List<BankAccount> result = accounts.stream()
                .filter(a -> a.getBalance() >= 1_000_000)
                .toList();

        assertTrue(result.isEmpty());
    }

    @Test
    void filterReturnsAllWhenThresholdIsZero() {
        List<BankAccount> result = accounts.stream()
                .filter(a -> a.getBalance() >= 0)
                .toList();

        assertEquals(accounts.size(), result.size());
    }
}