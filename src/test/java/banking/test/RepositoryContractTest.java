package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RepositoryContractTest {

    private Repository<AccountNumber, BankAccount> repository;
    private AccountNumber id1;
    private AccountNumber id2;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>();
        id1 = new AccountNumber("0000000001");
        id2 = new AccountNumber("0000000002");
    }

    @Test
    void saveAndFindByIdReturnsSavedObject() {
        BankAccount account = new DebitAccount(id1, "Ivan", 1000);

        repository.save(account);
        BankAccount found = repository.findById(id1);

        assertSame(account, found, "findById должен вернуть тот же объект, что был сохранён");
        assertEquals("Ivan", found.getOwner());
        assertEquals(1000, found.getBalance());
    }

    @Test
    void existsByIdReturnsTrueForExistingAndFalseForMissing() {
        repository.save(new DebitAccount(id1, "Ivan", 1000));

        assertTrue(repository.existsById(id1), "Для существующего ID должно вернуть true");
        assertFalse(repository.existsById(id2), "Для отсутствующего ID должно вернуть false");
    }

    @Test
    void sizeIncreasesAfterSavingNewId() {
        assertEquals(0, repository.size());

        repository.save(new DebitAccount(id1, "Ivan", 1000));
        assertEquals(1, repository.size());

        repository.save(new DebitAccount(id2, "Petr", 2000));
        assertEquals(2, repository.size());
    }

    @Test
    void savingSameIdReplacesOldValue() {
        BankAccount oldAccount = new DebitAccount(id1, "Ivan", 1000);
        BankAccount newAccount = new DebitAccount(id1, "Petr", 5000);

        repository.save(oldAccount);
        repository.save(newAccount);

        assertEquals(1, repository.size(), "size() не должен увеличиваться при замене");
        assertSame(newAccount, repository.findById(id1), "Должен вернуться новый объект");
        assertEquals("Petr", repository.findById(id1).getOwner());
    }

    @Test
    void deleteByIdRemovesExistingObject() {
        repository.save(new DebitAccount(id1, "Ivan", 1000));

        boolean deleted = repository.deleteById(id1);

        assertTrue(deleted, "deleteById должен вернуть true при успешном удалении");
        assertFalse(repository.existsById(id1), "После удаления объект не должен существовать");
        assertNull(repository.findById(id1), "findById должен вернуть null");
        assertEquals(0, repository.size());
    }

    @Test
    void deleteByIdReturnsFalseForNonExistentId() {
        boolean deleted = repository.deleteById(id1);

        assertFalse(deleted, "Удаление несуществующего ID должно вернуть false");
    }

    @Test
    void findAllReturnsAllSavedValues() {
        BankAccount acc1 = new DebitAccount(id1, "Ivan", 1000);
        BankAccount acc2 = new DebitAccount(id2, "Petr", 2000);
        repository.save(acc1);
        repository.save(acc2);

        List<BankAccount> all = repository.findAll();

        assertEquals(2, all.size());
        assertTrue(all.contains(acc1));
        assertTrue(all.contains(acc2));
    }

    @Test
    void findAllReturnsEmptyListForEmptyRepository() {
        List<BankAccount> all = repository.findAll();

        assertNotNull(all, "findAll не должен возвращать null");
        assertTrue(all.isEmpty());
    }

    @Test
    void saveNullIsForbidden() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> repository.save(null)
        );
        assertEquals("Value must not be null", exception.getMessage());
        assertEquals(0, repository.size(), "Репозиторий должен остаться пустым");
    }
}
