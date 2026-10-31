package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceStreamSortedTest {

    private BankService bankService;
    private Repository<AccountNumber, BankAccount> repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>();
        bankService = new BankService(repository, null, null);

        bankService.openAccount(new DebitAccount(new AccountNumber("3333333333"), "Charlie", 5000));
        bankService.openAccount(new SavingsAccount(new AccountNumber("1111111111"), "Alice", 150000, 10000));
        bankService.openAccount(new CreditAccount(new AccountNumber("5555555555"), "Eve", 2000, 50000));
        bankService.openAccount(new DebitAccount(new AccountNumber("2222222222"), "Bob", 100000));
        bankService.openAccount(new SavingsAccount(new AccountNumber("4444444444"), "Alice", 50000, 5000));
    }

    @Test
    void richestFirstReturnsAccountsSortedByBalanceDescending() {
        List<BankAccount> result = bankService.richestFirst();

        assertEquals(5, result.size());
        assertEquals(150000.0, result.get(0).getBalance(), "Первый — максимальный баланс");
        assertEquals(100000.0, result.get(1).getBalance());
        assertEquals(50000.0, result.get(2).getBalance());
        assertEquals(5000.0, result.get(3).getBalance());
        assertEquals(2000.0, result.get(4).getBalance(), "Последний — минимальный баланс");
    }

    @Test
    void richestFirstIsDescending() {
        List<BankAccount> result = bankService.richestFirst();

        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getBalance() >= result.get(i + 1).getBalance(),
                    "Список должен быть отсортирован по убыванию баланса");
        }
    }

    @Test
    void sortedByAccountNumberReturnsLexicographicalOrder() {
        List<BankAccount> result = bankService.sortedByAccountNumber();

        assertEquals(5, result.size());
        assertEquals("1111111111", result.get(0).getId().value());
        assertEquals("2222222222", result.get(1).getId().value());
        assertEquals("3333333333", result.get(2).getId().value());
        assertEquals("4444444444", result.get(3).getId().value());
        assertEquals("5555555555", result.get(4).getId().value());
    }

    @Test
    void sortedByTypeThenBalanceGroupsByTypeAndSortsByBalance() {
        List<BankAccount> result = bankService.sortedByTypeThenBalance();

        assertEquals(5, result.size());

        assertEquals("CreditAccount", result.get(0).getClass().getSimpleName());
        assertEquals("DebitAccount", result.get(1).getClass().getSimpleName());
        assertEquals("DebitAccount", result.get(2).getClass().getSimpleName());
        assertEquals("SavingsAccount", result.get(3).getClass().getSimpleName());
        assertEquals("SavingsAccount", result.get(4).getClass().getSimpleName());

        assertEquals(2000.0, result.get(0).getBalance());
        assertEquals(5000.0, result.get(1).getBalance());
        assertEquals(100000.0, result.get(2).getBalance());
        assertEquals(50000.0, result.get(3).getBalance());
        assertEquals(150000.0, result.get(4).getBalance());
    }

    @Test
    void sortedByTypeThenBalanceAscendingWithinGroup() {
        List<BankAccount> result = bankService.sortedByTypeThenBalance();

        for (int i = 0; i < result.size() - 1; i++) {
            if (result.get(i).getClass().equals(result.get(i + 1).getClass())) {
                assertTrue(result.get(i).getBalance() <= result.get(i + 1).getBalance(),
                        "Внутри одной группы типа баланс должен возрастать");
            }
        }
    }

    @Test
    void sortedByOwnerThenNumberGroupsByOwnerAndSortsByNumber() {
        List<BankAccount> result = bankService.sortedByOwnerThenNumber();

        assertEquals(5, result.size());

        assertEquals("Alice", result.get(0).getOwner());
        assertEquals("Alice", result.get(1).getOwner());
        assertEquals("Bob", result.get(2).getOwner());
        assertEquals("Charlie", result.get(3).getOwner());
        assertEquals("Eve", result.get(4).getOwner());

        assertEquals("1111111111", result.get(0).getId().value());
        assertEquals("4444444444", result.get(1).getId().value());
    }

    @Test
    void sortedByOwnerThenNumberAscendingWithinGroup() {
        List<BankAccount> result = bankService.sortedByOwnerThenNumber();

        for (int i = 0; i < result.size() - 1; i++) {
            if (result.get(i).getOwner().equals(result.get(i + 1).getOwner())) {
                assertTrue(
                        result.get(i).getId().value().compareTo(result.get(i + 1).getId().value()) <= 0,
                        "Внутри одного владельца номера должны идти по возрастанию"
                );
            }
        }
    }

    @Test
    void sortingDoesNotModifyOriginalRepository() {
        List<BankAccount> originalOrder = repository.findAll();
        String[] originalIds = new String[originalOrder.size()];
        for (int i = 0; i < originalOrder.size(); i++) {
            originalIds[i] = originalOrder.get(i).getId().value();
        }

        bankService.richestFirst();
        bankService.sortedByAccountNumber();
        bankService.sortedByTypeThenBalance();
        bankService.sortedByOwnerThenNumber();

        List<BankAccount> afterSorting = repository.findAll();
        assertEquals(originalOrder.size(), afterSorting.size(), "Размер репозитория не изменился");

        for (int i = 0; i < afterSorting.size(); i++) {
            assertEquals(originalIds[i], afterSorting.get(i).getId().value(),
                    "Порядок элементов в репозитории не должен измениться после сортировки");
        }
    }

    @Test
    void eachSortCallReturnsNewList() {
        List<BankAccount> first = bankService.richestFirst();
        List<BankAccount> second = bankService.richestFirst();

        assertNotSame(first, second, "Каждый вызов должен возвращать новый список");

        assertEquals(first.size(), second.size());
        assertEquals(first, second);
    }

    @Test
    void sortingEmptyRepositoryReturnsEmptyList() {
        Repository<AccountNumber, BankAccount> emptyRepo = new InMemoryRepository<>();
        BankService emptyService = new BankService(emptyRepo, null, null);

        assertTrue(emptyService.richestFirst().isEmpty());
        assertTrue(emptyService.sortedByAccountNumber().isEmpty());
        assertTrue(emptyService.sortedByTypeThenBalance().isEmpty());
        assertTrue(emptyService.sortedByOwnerThenNumber().isEmpty());
    }

    @Test
    void sortingSingleElementReturnsSameElement() {
        Repository<AccountNumber, BankAccount> singleRepo = new InMemoryRepository<>();
        BankService singleService = new BankService(singleRepo, null, null);
        singleService.openAccount(new DebitAccount(new AccountNumber("1111111111"), "Alice", 5000));

        assertEquals(1, singleService.richestFirst().size());
        assertEquals(1, singleService.sortedByAccountNumber().size());
        assertEquals(1, singleService.sortedByTypeThenBalance().size());
        assertEquals(1, singleService.sortedByOwnerThenNumber().size());
    }
}
