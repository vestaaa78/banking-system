package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.OptionalDouble;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceNumericStreamTest {

    private BankService bankService;
    private Repository<AccountNumber, BankAccount> repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>();
        bankService = new BankService(repository, null, null);

        bankService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));
        bankService.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 10000));
        bankService.openAccount(new SavingsAccount(new AccountNumber("3333333333"), "Charlie", 15000, 1000));
    }

    @Test
    void totalBalanceReturnsSumOfAllBalances() {
        double total = bankService.totalBalance();

        assertEquals(30000.0, total);
    }

    @Test
    void totalBalanceReturnsZeroForEmptyRepository() {
        Repository<AccountNumber, BankAccount> emptyRepo = new InMemoryRepository<>();
        BankService emptyService = new BankService(emptyRepo, null, null);

        assertEquals(0.0, emptyService.totalBalance(),
                "Сумма для пустого репозитория должна быть 0.0");
    }

    @Test
    void totalBalanceReturnsSingleValueForOneAccount() {
        Repository<AccountNumber, BankAccount> singleRepo = new InMemoryRepository<>();
        BankService singleService = new BankService(singleRepo, null , null);
        singleService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 7500));

        assertEquals(7500.0, singleService.totalBalance());
    }

    @Test
    void totalBalanceHandlesLargeValues() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        BankService service = new BankService(repo, null, null);
        service.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 1_000_000_000.0));
        service.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 2_000_000_000.0));

        assertEquals(3_000_000_000.0, service.totalBalance());
    }

    @Test
    void averageBalanceReturnsCorrectAverage() {
        OptionalDouble average = bankService.averageBalance();

        assertTrue(average.isPresent());
        assertEquals(10000.0, average.getAsDouble());
    }

    @Test
    void averageBalanceReturnsEmptyForEmptyRepository() {
        Repository<AccountNumber, BankAccount> emptyRepo = new InMemoryRepository<>();
        BankService emptyService = new BankService(emptyRepo, null, null);

        OptionalDouble average = emptyService.averageBalance();

        assertTrue(average.isEmpty(),
                "Среднее для пустого репозитория должно быть OptionalDouble.empty()");
    }

    @Test
    void averageBalanceReturnsValueForSingleAccount() {
        Repository<AccountNumber, BankAccount> singleRepo = new InMemoryRepository<>();
        BankService singleService = new BankService(singleRepo, null, null);
        singleService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 7500));

        OptionalDouble average = singleService.averageBalance();

        assertTrue(average.isPresent());
        assertEquals(7500.0, average.getAsDouble());
    }

    @Test
    void averageBalanceHandlesFractionalResult() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        BankService service = new BankService(repo, null, null);
        service.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 1000));
        service.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 2000));
        service.openAccount(new DebitAccount(new AccountNumber("3333333333"), "Charlie", 3000));

        OptionalDouble average = service.averageBalance();

        assertTrue(average.isPresent());
        assertEquals(2000.0, average.getAsDouble(), 0.0001);
    }

    @Test
    void averageBalanceOrElseUsage() {
        double averageOrDefault = bankService.averageBalance().orElse(0.0);

        assertEquals(10000.0, averageOrDefault);

        Repository<AccountNumber, BankAccount> emptyRepo = new InMemoryRepository<>();
        BankService emptyService = new BankService(emptyRepo, null, null);
        double emptyAverage = emptyService.averageBalance().orElse(0.0);
        assertEquals(0.0, emptyAverage);
    }

    @Test
    void richestAccountReturnsAccountWithMaxBalance() {
        Optional<BankAccount> richest = bankService.richestAccount();

        assertTrue(richest.isPresent());
        assertEquals("3333333333", richest.get().getId().value());
        assertEquals("Charlie", richest.get().getOwner());
        assertEquals(15000.0, richest.get().getBalance());
    }

    @Test
    void richestAccountReturnsEmptyForEmptyRepository() {
        Repository<AccountNumber, BankAccount> emptyRepo = new InMemoryRepository<>();
        BankService emptyService = new BankService(emptyRepo, null, null);

        Optional<BankAccount> richest = emptyService.richestAccount();

        assertTrue(richest.isEmpty());
    }

    @Test
    void richestAccountReturnsSingleAccountForOneElement() {
        Repository<AccountNumber, BankAccount> singleRepo = new InMemoryRepository<>();
        BankService singleService = new BankService(singleRepo, null, null);
        singleService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));

        Optional<BankAccount> richest = singleService.richestAccount();

        assertTrue(richest.isPresent());
        assertEquals("Alice", richest.get().getOwner());
    }

    @Test
    void richestAccountReturnsFirstWhenMultipleHaveSameMax() {
        Repository<AccountNumber, BankAccount> repo = new InMemoryRepository<>();
        BankService service = new BankService(repo, null, null);
        service.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 10000));
        service.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 10000)); // такой же баланс
        service.openAccount(new DebitAccount(new AccountNumber("3333333333"), "Charlie", 5000));

        Optional<BankAccount> richest = service.richestAccount();

        assertTrue(richest.isPresent());
        assertEquals("1111111111", richest.get().getId().value(),
                "При равенстве балансов должен вернуться первый по порядку");
    }

    @Test
    void richestAccountOrElseThrowUsage() {
        BankAccount richest = bankService.richestAccount()
                .orElseThrow(() -> new IllegalStateException("No accounts"));

        assertEquals("Charlie", richest.getOwner());
    }

    @Test
    void richestAccountMapToLabel() {
        String label = bankService.richestAccount()
                .map(a -> a.getOwner() + " : " + a.getBalance())
                .orElse("no accounts");

        assertEquals("Charlie : 15000.0", label);
    }

    @Test
    void richestAccountMapToLabelForEmptyRepository() {
        Repository<AccountNumber, BankAccount> emptyRepo = new InMemoryRepository<>();
        BankService emptyService = new BankService(emptyRepo, null, null);

        String label = emptyService.richestAccount()
                .map(a -> a.getOwner() + " : " + a.getBalance())
                .orElse("no accounts");

        assertEquals("no accounts", label);
    }
}