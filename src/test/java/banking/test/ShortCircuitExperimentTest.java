package banking.test;

import banking.AccountNumber;
import banking.BankAccount;
import banking.CreditAccount;
import banking.DebitAccount;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class ShortCircuitExperimentTest {
    @Test
    void anyMatchStopsAtFirstMatch() {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Rich", 150000));
        for (int i = 2; i <= 10; i++) {
            String num = String.format("%010d", i);
            accounts.add(new DebitAccount(new AccountNumber(num), "User" + i, 1000));
        }

        int[] callCount = {0};

        boolean result = accounts.stream()
                .anyMatch(account -> {
                    callCount[0]++;
                    return account.getBalance() >= 100000;
                });

        assertTrue(result, "Должен найти богатый счёт");
        assertEquals(1, callCount[0],
                "Предикат должен быть вызван ТОЛЬКО ОДИН РАЗ, так как первый элемент уже удовлетворяет условию. " +
                        "Это доказывает short-circuit поведение!");
    }

    @Test
    void anyMatchProcessesAllWhenNoMatch() {
        List<BankAccount> accounts = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String num = String.format("%010d", i);
            accounts.add(new DebitAccount(new AccountNumber(num), "User" + i, 1000));
        }

        int[] callCount = {0};

        boolean result = accounts.stream()
                .anyMatch(account -> {
                    callCount[0]++;
                    return account.getBalance() >= 100000;
                });

        assertFalse(result, "Не должно быть богатых счетов");
        assertEquals(5, callCount[0],
                "Предикат должен быть вызван для ВСЕХ элементов, так как ни один не подошёл");
    }

    @Test
    void allMatchStopsAtFirstMismatch() {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Zero", 0)); // ← первый!
        for (int i = 2; i <= 10; i++) {
            String num = String.format("%010d", i);
            accounts.add(new DebitAccount(new AccountNumber(num), "User" + i, 5000));
        }
        int[] callCount = {0};
        boolean result = accounts.stream()
                .allMatch(account -> {
                    callCount[0]++;
                    return account.getBalance() > 0;
                });

        assertFalse(result, "Должен вернуть false из-за нулевого баланса");
        assertEquals(1, callCount[0],
                "Предикат должен быть вызван ТОЛЬКО ОДИН РАЗ — первый элемент уже нарушил условие");
    }

    @Test
    void noneMatchStopsAtFirstMatch() {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new CreditAccount(new AccountNumber("1111111111"), "Credit", 5000, 10000)); // ← первый!
        for (int i = 2; i <= 10; i++) {
            String num = String.format("%010d", i);
            accounts.add(new DebitAccount(new AccountNumber(num), "User" + i, 1000));
        }
        int[] callCount = {0};
        boolean result = accounts.stream()
                .noneMatch(account -> {
                    callCount[0]++;
                    return account instanceof CreditAccount;
                });
        assertFalse(result, "Должен вернуть false, так как есть CreditAccount");
        assertEquals(1, callCount[0],
                "Предикат должен быть вызван ТОЛЬКО ОДИН РАЗ — первый элемент уже нарушил noneMatch");
    }

    @Test
    void manualLoopAlwaysProcessesAllElements() {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Rich", 150000));
        for (int i = 2; i <= 10; i++) {
            String num = String.format("%010d", i);
            accounts.add(new DebitAccount(new AccountNumber(num), "User" + i, 1000));
        }
        int[] callCount = {0};
        boolean found = false;
        for (BankAccount account : accounts) {
            callCount[0]++;
            if (account.getBalance() >= 100000) {
                found = true;
            }
        }
        assertTrue(found);
        assertEquals(10, callCount[0],
                "Ручной цикл без break проходит ВСЕ элементы, даже после нахождения нужного");
    }

    @Test
    void manualLoopWithBreakBehavesLikeAnyMatch() {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Rich", 150000));
        for (int i = 2; i <= 10; i++) {
            String num = String.format("%010d", i);
            accounts.add(new DebitAccount(new AccountNumber(num), "User" + i, 1000));
        }
        int[] callCount = {0};
        boolean found = false;
        for (BankAccount account : accounts) {
            callCount[0]++;
            if (account.getBalance() >= 100000) {
                found = true;
                break;
            }
        }

        assertTrue(found);
        assertEquals(1, callCount[0],
                "Цикл с break останавливается на первом совпадении — как anyMatch");
    }
}