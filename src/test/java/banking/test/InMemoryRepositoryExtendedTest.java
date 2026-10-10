package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryRepositoryExtendedTest {

    private Repository<AccountNumber, BankAccount> accountRepo;

    @BeforeEach
    void setUp() {
        accountRepo = new InMemoryRepository<>();
    }

    @Test
    void shouldDeleteExistingEntity() {
        AccountNumber id = new AccountNumber("1234567890");
        accountRepo.save(new DebitAccount(id, "Alice", 1000));

        boolean deleted = accountRepo.deleteById(id);

        assertTrue(deleted, "Удаление существующей сущности должно вернуть true");
        assertFalse(accountRepo.existsById(id), "После удаления сущность не должна существовать");
        assertEquals(0, accountRepo.size());
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentEntity() {
        AccountNumber id = new AccountNumber("0000000000");

        boolean deleted = accountRepo.deleteById(id);

        assertFalse(deleted, "Удаление несуществующей сущности должно вернуть false");
    }

    @Test
    void shouldDeleteOnlySpecifiedEntity() {
        AccountNumber id1 = new AccountNumber("1111111111");
        AccountNumber id2 = new AccountNumber("2222222222");
        accountRepo.save(new DebitAccount(id1, "Alice", 1000));
        accountRepo.save(new DebitAccount(id2, "Bob", 2000));

        boolean deleted = accountRepo.deleteById(id1);

        assertTrue(deleted);
        assertFalse(accountRepo.existsById(id1));
        assertTrue(accountRepo.existsById(id2), "Другая сущность должна остаться");
        assertEquals(1, accountRepo.size());
    }

    @Test
    void shouldReturnAllSavedEntities() {
        AccountNumber id1 = new AccountNumber("1111111111");
        AccountNumber id2 = new AccountNumber("2222222222");
        AccountNumber id3 = new AccountNumber("3333333333");

        accountRepo.save(new DebitAccount(id1, "Alice", 1000));
        accountRepo.save(new SavingsAccount(id2, "Bob", 2000, 500));
        accountRepo.save(new CreditAccount(id3, "Charlie", 3000, 5000));

        List<BankAccount> all = accountRepo.findAll();

        assertEquals(3, all.size());
        assertTrue(all.stream().anyMatch(a -> a.getOwner().equals("Alice")));
        assertTrue(all.stream().anyMatch(a -> a.getOwner().equals("Bob")));
        assertTrue(all.stream().anyMatch(a -> a.getOwner().equals("Charlie")));
    }

    @Test
    void shouldReturnEmptyListForEmptyRepository() {
        List<BankAccount> all = accountRepo.findAll();

        assertNotNull(all, "findAll не должен возвращать null");
        assertTrue(all.isEmpty(), "Для пустого репозитория должен вернуться пустой список");
    }

    @Test
    void shouldReturnCopyNotInternalReference() {
        AccountNumber id = new AccountNumber("1234567890");
        accountRepo.save(new DebitAccount(id, "Alice", 1000));

        List<BankAccount> firstCall = accountRepo.findAll();
        List<BankAccount> secondCall = accountRepo.findAll();

        assertNotSame(firstCall, secondCall, "Каждый вызов findAll должен возвращать новую копию");

        firstCall.clear();
        assertEquals(1, accountRepo.size(), "Очистка возвращённого списка не должна влиять на репозиторий");
    }

    @Test
    void findAllShouldReflectChangesAfterDelete() {
        AccountNumber id1 = new AccountNumber("1111111111");
        AccountNumber id2 = new AccountNumber("2222222222");
        accountRepo.save(new DebitAccount(id1, "Alice", 1000));
        accountRepo.save(new DebitAccount(id2, "Bob", 2000));

        accountRepo.deleteById(id1);
        List<BankAccount> all = accountRepo.findAll();

        assertEquals(1, all.size());
        assertEquals("Bob", all.get(0).getOwner());
    }


    @Test
    void shouldThrowExceptionWhenSavingNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountRepo.save(null)
        );
        assertEquals("Value must not be null", exception.getMessage());
    }
}