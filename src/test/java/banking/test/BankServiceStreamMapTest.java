package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceStreamMapTest {

    private BankService bankService;
    private Repository<AccountNumber, BankAccount> repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>();
        bankService = new BankService(repository, null, null);

        bankService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        bankService.openAccount(new SavingsAccount(new AccountNumber("2222222222"), "Bob", 10000, 1000));
        bankService.openAccount(new CreditAccount(new AccountNumber("3333333333"), "Charlie", 3000, 50000));
    }

    @Test
    void getAccountSummariesReturnsCorrectCount() {
        List<AccountSummary> summaries = bankService.getAccountSummaries();

        assertEquals(3, summaries.size(), "Должно быть столько же summaries, сколько счетов");
    }

    @Test
    void getAccountSummariesPreservesAccountNumber() {
        List<AccountSummary> summaries = bankService.getAccountSummaries();

        boolean has1111 = false;
        boolean has2222 = false;
        boolean has3333 = false;

        for (AccountSummary summary : summaries) {
            String number = summary.number().value();
            if (number.equals("1111111111")) has1111 = true;
            if (number.equals("2222222222")) has2222 = true;
            if (number.equals("3333333333")) has3333 = true;
        }

        assertTrue(has1111, "Должен быть счёт 1111111111");
        assertTrue(has2222, "Должен быть счёт 2222222222");
        assertTrue(has3333, "Должен быть счёт 3333333333");
    }

    @Test
    void getAccountSummariesPreservesBalance() {
        List<AccountSummary> summaries = bankService.getAccountSummaries();

        AccountSummary aliceSummary = null;
        for (AccountSummary s : summaries) {
            if (s.number().value().equals("1111111111")) {
                aliceSummary = s;
                break;
            }
        }

        assertNotNull(aliceSummary);
        assertEquals(5000.0, aliceSummary.balance());
    }

    @Test
    void getAccountSummariesContainsCorrectTypeNames() {
        List<AccountSummary> summaries = bankService.getAccountSummaries();

        boolean hasDebit = false;
        boolean hasSavings = false;
        boolean hasCredit = false;

        for (AccountSummary summary : summaries) {
            String type = summary.type();
            if (type.equals("DebitAccount")) hasDebit = true;
            if (type.equals("SavingsAccount")) hasSavings = true;
            if (type.equals("CreditAccount")) hasCredit = true;
        }

        assertTrue(hasDebit, "Должен быть DebitAccount");
        assertTrue(hasSavings, "Должен быть SavingsAccount");
        assertTrue(hasCredit, "Должен быть CreditAccount");
    }

    @Test
    void getAccountSummariesReturnsEmptyListForEmptyRepository() {
        Repository<AccountNumber, BankAccount> emptyRepo = new InMemoryRepository<>();
        BankService emptyService = new BankService(emptyRepo, null, null);

        List<AccountSummary> summaries = emptyService.getAccountSummaries();

        assertTrue(summaries.isEmpty());
    }

    @Test
    void getAccountSummariesDoesNotModifyOriginalRepository() {
        int originalSize = bankService.accountCount();

        List<AccountSummary> summaries = bankService.getAccountSummaries();

        assertEquals(originalSize, bankService.accountCount());

        assertNotNull(bankService.findAccount(new AccountNumber("1111111111")));
        assertNotNull(bankService.findAccount(new AccountNumber("2222222222")));
        assertNotNull(bankService.findAccount(new AccountNumber("3333333333")));
    }

    @Test
    void getAccountSummariesReturnsNewListOnEachCall() {
        List<AccountSummary> first = bankService.getAccountSummaries();
        List<AccountSummary> second = bankService.getAccountSummaries();

        assertNotSame(first, second, "Каждый вызов должен возвращать новый список");

        assertEquals(first.size(), second.size());
        assertEquals(first, second, "Содержимое должно совпадать");
    }

    @Test
    void accountSummaryDoesNotExposeOwnerInformation() {
        List<AccountSummary> summaries = bankService.getAccountSummaries();

        AccountSummary summary = summaries.get(0);

        assertNotNull(summary.number());
        assertNotNull(summary.type());
        assertTrue(summary.balance() >= 0);

        AccountSummary copy = new AccountSummary(
                summary.number(),
                summary.type(),
                summary.balance()
        );
        assertEquals(summary, copy, "Две одинаковые summary должны быть равны");
    }

    @Test
    void mapPreservesAllAccounts() {
        List<AccountSummary> summaries = bankService.getAccountSummaries();
        List<BankAccount> allAccounts = bankService.findAccounts(a -> true);

        assertEquals(allAccounts.size(), summaries.size(),
                "Каждый счёт должен быть преобразован в summary");

        for (BankAccount account : allAccounts) {
            boolean found = false;
            for (AccountSummary summary : summaries) {
                if (summary.number().equals(account.getId()) &&
                        summary.balance() == account.getBalance()) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "Для счёта " + account.getId() + " должна быть summary");
        }
    }
}