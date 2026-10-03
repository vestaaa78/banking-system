package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryRepositoryTest {

    private Repository<AccountNumber, BankAccount> accountRepo;

    @BeforeEach
    void setUp() {
        accountRepo = new InMemoryRepository<>();
    }

    @Test
    void shouldThrowExceptionWhenSavingNull() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> accountRepo.save(null)
        );
        assertEquals("Cannot save null value", exception.getMessage());
    }

    @Test
    void shouldSaveAndFindEntity() {
        AccountNumber id = new AccountNumber("1234567890");
        BankAccount account = new DebitAccount(id, "Alice", 1000);

        accountRepo.save(account);

        assertEquals(1, accountRepo.size());
        assertTrue(accountRepo.existsById(id));

        BankAccount found = accountRepo.findById(id);
        assertNotNull(found);
        assertEquals("Alice", found.getOwner());
    }

    @Test
    void shouldReturnNullWhenEntityNotFound() {
        BankAccount found = accountRepo.findById(new AccountNumber("0000000000"));

        assertNull(found);
        assertFalse(accountRepo.existsById(new AccountNumber("0000000000")));
    }

    @Test
    void shouldReplaceExistingEntityOnSave() {
        AccountNumber id = new AccountNumber("1234567890");
        accountRepo.save(new DebitAccount(id, "Alice", 1000));

        accountRepo.save(new SavingsAccount(id, "Bob", 5000, 1000));

        assertEquals(1, accountRepo.size(), "Размер не должен увеличиться, дубликатов нет");

        BankAccount found = accountRepo.findById(id);
        assertNotNull(found);
        assertEquals("Bob", found.getOwner());
        assertEquals(5000, found.getBalance());
        assertTrue(found instanceof SavingsAccount);
    }
}