package banking.test;

import banking.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankServiceTest {

    private BankService bankService;
    private Repository<AccountNumber, BankAccount> repository;
    private AccountNumber number1;
    private AccountNumber number2;

    @BeforeEach
    void setUp() {
        repository = new InMemoryRepository<>();
        bankService = new BankService(repository, null, null);
        number1 = new AccountNumber("1111111111");
        number2 = new AccountNumber("2222222222");
    }

    @Test
    void openAccountThenFind() {
        BankAccount account = new DebitAccount(number1, "Alice", 1000);

        bankService.openAccount(account);
        BankAccount found = bankService.findAccount(number1);

        assertNotNull(found, "Счёт должен быть найден после открытия");
        assertSame(account, found, "findAccount должен вернуть тот же объект");
        assertEquals("Alice", found.getOwner());
        assertEquals(1000, found.getBalance());
    }

    @Test
    void openAccountWithSameIdReplacesOld() {
        BankAccount oldAccount = new DebitAccount(number1, "Alice", 1000);
        BankAccount newAccount = new DebitAccount(number1, "Bob", 5000);

        bankService.openAccount(oldAccount);
        bankService.openAccount(newAccount);

        BankAccount found = bankService.findAccount(number1);
        assertNotNull(found);
        assertSame(newAccount, found, "Должен быть сохранён новый счёт (контракт Repository - upsert)");
        assertEquals("Bob", found.getOwner());
        assertEquals(5000, found.getBalance());
        assertEquals(1, bankService.accountCount(), "Размер не должен увеличиться");
    }

    @Test
    void closeAccountThenNotFound() {
        bankService.openAccount(new DebitAccount(number1, "Alice", 1000));

        boolean closed = bankService.closeAccount(number1);

        assertTrue(closed, "closeAccount должен вернуть true при успешном закрытии");
        assertNull(bankService.findAccount(number1), "После закрытия счёт не должен находиться");
        assertEquals(0, bankService.accountCount());
    }

    @Test
    void openAccountWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> bankService.openAccount(null));
    }

    @Test
    void findAccountWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> bankService.findAccount(null));
    }

    @Test
    void closeAccountWithNullThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> bankService.closeAccount(null));
    }

    @Test
    void findNonExistentAccountReturnsNull() {
        assertNull(bankService.findAccount(number1));
    }

    @Test
    void closeNonExistentAccountReturnsFalse() {
        assertFalse(bankService.closeAccount(number1));
    }

    @Test
    void accountCountIncreasesAfterOpen() {
        assertEquals(0, bankService.accountCount());
        bankService.openAccount(new DebitAccount(number1, "Alice", 1000));
        assertEquals(1, bankService.accountCount());
        bankService.openAccount(new DebitAccount(number2, "Bob", 2000));
        assertEquals(2, bankService.accountCount());
    }
}