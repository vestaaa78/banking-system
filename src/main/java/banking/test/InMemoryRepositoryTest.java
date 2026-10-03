package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryRepositoryTest {

    private Repository<AccountNumber, BankAccount> accountRepo;
    private Repository<Long, Transaction> transactionRepo;

    @BeforeEach
    void setUp() {
        accountRepo = new InMemoryRepository<>();
        transactionRepo = new InMemoryRepository<>();
    }

    @Test
    void shouldSaveAndFindBankAccount() {
        AccountNumber id = new AccountNumber("1234567890");
        BankAccount account = new DebitAccount(id, "Alice", 1000);

        accountRepo.save(account);

        assertTrue(accountRepo.existsById(id));
        assertEquals(1, accountRepo.size());

        BankAccount found = accountRepo.findById(id);
        assertNotNull(found);
        assertEquals("Alice", found.getOwner());
        assertEquals(1000, found.getBalance());
    }

    @Test
    void shouldSaveAndFindTransaction() {
        Long id = 1L;
        Transaction tx = new Transaction(id, TransactionType.DEPOSIT, new AccountNumber("1111111111"), 500, TransactionStatus.SUCCESS);

        transactionRepo.save(tx);

        assertTrue(transactionRepo.existsById(id));
        assertEquals(1, transactionRepo.size());

        Transaction found = transactionRepo.findById(id);
        assertNotNull(found);
        assertEquals(TransactionType.DEPOSIT, found.type());
        assertEquals(500, found.amount());
    }

    @Test
    void shouldReturnNullWhenNotFound() {
        assertNull(accountRepo.findById(new AccountNumber("0000000000")));
        assertFalse(accountRepo.existsById(new AccountNumber("0000000000")));

        assertNull(transactionRepo.findById(999L));
        assertFalse(transactionRepo.existsById(999L));
    }

    @Test
    void shouldOverwriteExistingEntity() {
        AccountNumber id = new AccountNumber("1234567890");
        accountRepo.save(new DebitAccount(id, "Alice", 1000));

        accountRepo.save(new SavingsAccount(id, "Bob", 5000, 1000));

        assertEquals(1, accountRepo.size());
        BankAccount found = accountRepo.findById(id);
        assertEquals("Bob", found.getOwner());
        assertEquals(5000, found.getBalance());
    }
}