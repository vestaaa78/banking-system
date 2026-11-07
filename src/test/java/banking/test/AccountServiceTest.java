package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AccountServiceTest {

    private AccountService accountService;
    private Repository<AccountNumber, BankAccount> repository;
    private AccountNumber num1;
    private AccountNumber num2;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>();
        accountService = new AccountService(repository);
        num1 = new AccountNumber("1111111111");
        num2 = new AccountNumber("2222222222");
    }

    @Test
    void openAccountSuccessfully() {
        BankAccount account = new DebitAccount(num1, "Alice", 1000);

        accountService.open(account);

        Optional<BankAccount> found = accountService.find(num1);
        assertTrue(found.isPresent());
        assertSame(account, found.get());
    }

    @Test
    void openNullAccountThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> accountService.open(null));
    }

    @Test
    void openMultipleAccounts() {
        accountService.open(new DebitAccount(num1, "Alice", 1000));
        accountService.open(new DebitAccount(num2, "Bob", 2000));

        assertEquals(2, accountService.count());
    }


    @Test
    void findExistingAccountReturnsOptionalWithValue() {
        accountService.open(new DebitAccount(num1, "Alice", 1000));

        Optional<BankAccount> result = accountService.find(num1);

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getOwner());
    }

    @Test
    void findNonExistentAccountReturnsEmptyOptional() {
        Optional<BankAccount> result = accountService.find(num1);

        assertTrue(result.isEmpty());
    }

    @Test
    void findWithNullNumberThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> accountService.find(null));
    }


    @Test
    void closeExistingAccountReturnsTrue() {
        accountService.open(new DebitAccount(num1, "Alice", 1000));

        boolean closed = accountService.close(num1);

        assertTrue(closed);
        assertTrue(accountService.find(num1).isEmpty());
        assertEquals(0, accountService.count());
    }

    @Test
    void closeNonExistentAccountReturnsFalse() {
        boolean closed = accountService.close(num1);

        assertFalse(closed);
    }

    @Test
    void closeWithNullNumberThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> accountService.close(null));
    }


    @Test
    void countReturnsZeroForEmptyRepository() {
        assertEquals(0, accountService.count());
    }

    @Test
    void countIncreasesAfterOpen() {
        accountService.open(new DebitAccount(num1, "Alice", 1000));
        assertEquals(1, accountService.count());

        accountService.open(new DebitAccount(num2, "Bob", 2000));
        assertEquals(2, accountService.count());
    }

    @Test
    void countDecreasesAfterClose() {
        accountService.open(new DebitAccount(num1, "Alice", 1000));
        accountService.open(new DebitAccount(num2, "Bob", 2000));

        accountService.close(num1);
        assertEquals(1, accountService.count());
    }


    @Test
    void accountServiceDoesNotDependOnTransferService() {
        AccountService standaloneService = new AccountService(new InMemoryRepository<>());
        AccountNumber testNum = new AccountNumber("9999999999");

        standaloneService.open(new DebitAccount(testNum, "Test", 5000));
        Optional<BankAccount> found = standaloneService.find(testNum);

        assertTrue(found.isPresent());
        assertEquals(5000, found.get().getBalance());

    }
}