package banking.test;

import banking.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AccountUtilsTest {

    @Test
    void shouldReturnRichestAccountAndPreserveSpecificType() {
        List<DebitAccount> accounts = new ArrayList<>();
        DebitAccount acc1 = new DebitAccount(new AccountNumber("1111111111"), "Alice", 1000);
        DebitAccount acc2 = new DebitAccount(new AccountNumber("2222222222"), "Bob", 5000);
        DebitAccount acc3 = new DebitAccount(new AccountNumber("3333333333"), "Charlie", 3000);

        accounts.add(acc1);
        accounts.add(acc2);
        accounts.add(acc3);

        DebitAccount richest = AccountUtils.richest(accounts);

        assertNotNull(richest);
        assertSame(acc2, richest, "Должен вернуться именно этот объект");
        assertEquals(5000, richest.getBalance());
    }

    @Test
    void shouldReturnNullForEmptyList() {
        List<SavingsAccount> accounts = new ArrayList<>();

        SavingsAccount richest = AccountUtils.richest(accounts);

        assertNull(richest, "Для пустого списка должен возвращаться null");
    }

    @Test
    void shouldReturnTheOnlyElementForSingleElementList() {
        List<CreditAccount> accounts = new ArrayList<>();
        CreditAccount acc = new CreditAccount(new AccountNumber("1111111111"), "Dave", 100, 1000);
        accounts.add(acc);

        CreditAccount richest = AccountUtils.richest(accounts);

        assertSame(acc, richest, "Для списка из одного элемента должен вернуться он сам");
    }

    @Test
    void shouldReturnFirstAccountWhenMultipleHaveSameMaxBalance() {
        List<BankAccount> accounts = new ArrayList<>();
        BankAccount acc1 = new DebitAccount(new AccountNumber("1111111111"), "A", 5000);
        BankAccount acc2 = new SavingsAccount(new AccountNumber("2222222222"), "B", 5000, 1000); // Такой же баланс
        BankAccount acc3 = new CreditAccount(new AccountNumber("3333333333"), "C", 5000, 2000);  // Такой же баланс

        accounts.add(acc1);
        accounts.add(acc2);
        accounts.add(acc3);

        BankAccount richest = AccountUtils.richest(accounts);

        assertSame(acc1, richest, "При равенстве балансов должен возвращаться первый элемент");
    }

    @Test
    void shouldThrowExceptionForNullList() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> AccountUtils.richest(null)
        );
        assertEquals("Список счетов не может быть null", exception.getMessage());
    }
}
