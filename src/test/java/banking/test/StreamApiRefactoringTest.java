package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

import static org.junit.jupiter.api.Assertions.*;

class StreamApiRefactoringTest {

    private BankService bankService;
    private Repository<AccountNumber, BankAccount> repository;
    private BlockedAccountRegistry blockedAccounts;

    private AccountNumber num1;
    private AccountNumber num2;
    private AccountNumber num3;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>();
        blockedAccounts = new BlockedAccountRegistry();
        TransferService transferService = new TransferService(
                new NoCommission(),
                new ConsoleNotificationService(),
                blockedAccounts,
                new TransactionJournal()
        );
        bankService = new BankService(repository, transferService, null);

        num1 = new AccountNumber("1111111111");
        num2 = new AccountNumber("2222222222");
        num3 = new AccountNumber("3333333333");
    }

    @Test
    void findRichAccounts_zeroMatches() {
        bankService.openAccount(new DebitAccount(num1, "Alice", 1000));
        bankService.openAccount(new DebitAccount(num2, "Bob", 2000));

        List<BankAccount> result = bankService.findRichAccounts(100_000);

        assertTrue(result.isEmpty(), "Ни один счёт не должен удовлетворять условию");
    }

    @Test
    void findRichAccounts_oneMatch() {
        bankService.openAccount(new DebitAccount(num1, "Alice", 1000));
        bankService.openAccount(new DebitAccount(num2, "Bob", 150_000));

        List<BankAccount> result = bankService.findRichAccounts(100_000);

        assertEquals(1, result.size());
        assertEquals("Bob", result.get(0).getOwner());
    }

    @Test
    void findRichAccounts_multipleMatches() {
        bankService.openAccount(new DebitAccount(num1, "Alice", 150_000));
        bankService.openAccount(new DebitAccount(num2, "Bob", 200_000));
        bankService.openAccount(new DebitAccount(num3, "Charlie", 5000));

        List<BankAccount> result = bankService.findRichAccounts(100_000);

        assertEquals(2, result.size());
    }

    @Test
    void getAccountSummaries_correctTypeAndBalance() {
        bankService.openAccount(new DebitAccount(num1, "Alice", 5000));
        bankService.openAccount(new SavingsAccount(num2, "Bob", 10000, 1000));
        bankService.openAccount(new CreditAccount(num3, "Charlie", 3000, 50000));

        List<AccountSummary> summaries = bankService.getAccountSummaries();

        assertEquals(3, summaries.size());

        boolean hasDebit = false, hasSavings = false, hasCredit = false;
        for (AccountSummary s : summaries) {
            if (s.type().equals("DebitAccount") && s.balance() == 5000) hasDebit = true;
            if (s.type().equals("SavingsAccount") && s.balance() == 10000) hasSavings = true;
            if (s.type().equals("CreditAccount") && s.balance() == 3000) hasCredit = true;
        }
        assertTrue(hasDebit && hasSavings && hasCredit,
                "Все типы и балансы должны быть корректно преобразованы");
    }

    @Test
    void richestFirst_sortedAndSourceUnchanged() {
        bankService.openAccount(new DebitAccount(num1, "Alice", 5000));
        bankService.openAccount(new DebitAccount(num2, "Bob", 150_000));
        bankService.openAccount(new DebitAccount(num3, "Charlie", 50_000));

        int originalSize = bankService.accountCount();

        List<BankAccount> sorted = bankService.richestFirst();

        assertEquals(3, sorted.size());
        assertEquals(150_000.0, sorted.get(0).getBalance());
        assertEquals(50_000.0, sorted.get(1).getBalance());
        assertEquals(5000.0, sorted.get(2).getBalance());

        assertEquals(originalSize, bankService.accountCount(),
                "Исходный репозиторий не должен измениться после сортировки");
    }

    @Test
    void hasBlockedAccounts_trueWhenBlocked() {
        bankService.openAccount(new DebitAccount(num1, "Alice", 5000));
        bankService.openAccount(new DebitAccount(num2, "Bob", 3000));
        blockedAccounts.block(num1);

        assertTrue(bankService.hasBlockedAccounts(blockedAccounts));
    }

    @Test
    void hasBlockedAccounts_falseWhenNoneBlocked() {
        bankService.openAccount(new DebitAccount(num1, "Alice", 5000));
        bankService.openAccount(new DebitAccount(num2, "Bob", 3000));

        assertFalse(bankService.hasBlockedAccounts(blockedAccounts));
    }

    @Test
    void hasBlockedAccounts_shortCircuitExperiment() {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(new DebitAccount(new AccountNumber("1111111111"), "Blocked", 5000));
        for (int i = 2; i <= 100; i++) {
            String num = String.format("%010d", i);
            accounts.add(new DebitAccount(new AccountNumber(num), "User" + i, 1000));
        }

        BlockedAccountRegistry registry = new BlockedAccountRegistry();
        registry.block(new AccountNumber("1111111111"));

        int[] callCount = {0};
        boolean result = accounts.stream()
                .anyMatch(account -> {
                    callCount[0]++;
                    return registry.isBlocked(account.getId());
                });

        assertTrue(result);
        assertEquals(1, callCount[0],
                "anyMatch должен остановиться на первом элементе (short-circuit)");
    }

    @Test
    void findFirstRichAccount_withResult() {
        bankService.openAccount(new DebitAccount(num1, "Alice", 5000));
        bankService.openAccount(new DebitAccount(num2, "Bob", 150_000));

        Optional<BankAccount> result = bankService.findFirstRichAccount(100_000);

        assertTrue(result.isPresent());
        assertEquals("Bob", result.get().getOwner());
    }

    @Test
    void findFirstRichAccount_empty() {
        bankService.openAccount(new DebitAccount(num1, "Alice", 5000));

        Optional<BankAccount> result = bankService.findFirstRichAccount(100_000);

        assertTrue(result.isEmpty());
    }

    @Test
    void totalBalance_nonEmptyList() {
        bankService.openAccount(new DebitAccount(num1, "Alice", 5000));
        bankService.openAccount(new DebitAccount(num2, "Bob", 10000));

        assertEquals(15000.0, bankService.totalBalance());
    }

    @Test
    void totalBalance_emptyList() {
        assertEquals(0.0, bankService.totalBalance(),
                "Сумма для пустого репозитория должна быть 0.0");
    }

    @Test
    void averageBalance_nonEmpty() {
        bankService.openAccount(new DebitAccount(num1, "Alice", 5000));
        bankService.openAccount(new DebitAccount(num2, "Bob", 15000));

        OptionalDouble avg = bankService.averageBalance();

        assertTrue(avg.isPresent());
        assertEquals(10000.0, avg.getAsDouble());
    }

    @Test
    void averageBalance_empty() {
        OptionalDouble avg = bankService.averageBalance();

        assertTrue(avg.isEmpty(),
                "Среднее для пустого репозитория должно быть OptionalDouble.empty()");
    }

    @Test
    void richestAccount_correctMax() {
        bankService.openAccount(new DebitAccount(num1, "Alice", 5000));
        bankService.openAccount(new DebitAccount(num2, "Bob", 150_000));
        bankService.openAccount(new DebitAccount(num3, "Charlie", 50_000));

        Optional<BankAccount> richest = bankService.richestAccount();

        assertTrue(richest.isPresent());
        assertEquals("Bob", richest.get().getOwner());
        assertEquals(150_000.0, richest.get().getBalance());
    }

    @Test
    void richestAccount_empty() {
        Optional<BankAccount> richest = bankService.richestAccount();

        assertTrue(richest.isEmpty(),
                "Для пустого репозитория richestAccount должен вернуть пустой Optional");
    }
}